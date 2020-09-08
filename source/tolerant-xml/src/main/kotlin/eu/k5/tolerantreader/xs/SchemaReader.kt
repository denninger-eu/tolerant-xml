package eu.k5.tolerantreader.xs

import com.google.common.base.Joiner
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.xml.bind.JAXBContext
import kotlin.collections.HashMap

object Schema {
    val context = JAXBContext.newInstance(XsSchema::class.java, WsdlDefinitions::class.java)


    fun parse(location: String): XsRegistry {

        if (location.startsWith("classpath:", ignoreCase = true)) {
            return parse(location.substring("classpath:".length), ClasspathStreamSource(Thread.currentThread().contextClassLoader))
        } else if (location.startsWith("file:", ignoreCase = true)) {
            return parse(location.substring("file:".length), PathStreamSource())
        }
        TODO("Support other protocols")
    }

    fun parse(location: String, contents: Map<String, String>): XsRegistry {
        return parse(location, MapStreamSource(contents))
    }

    fun parse(location: String, source: StreamSource): XsRegistry {

        val stream = source.resolveAbsolute(location)

        val init = read(stream)
        val all = HashMap<String, XsSchema>()
        all.put(stream.absolutPath, init)
        readRecursive(source, init, all)

        return XsRegistry(init, all)
    }

    private fun readRecursive(source: StreamSource, current: XsSchema, resolved: MutableMap<String, XsSchema>) {

        for (im in current.imports) {

            val stream = source.resolveRelative(current.schemaLocation!!, im.schemaLocation!!)
            val imported: XsSchema
            if (resolved.containsKey(stream.absolutPath)) {
                imported = resolved[stream.absolutPath]!!
            } else {
                imported = read(stream)
                resolved.put(stream.absolutPath, imported)
                readRecursive(source, imported, resolved)
            }
            im.resolvedSchema = imported
        }
    }


    private fun read(stream: Stream): XsSchema {
        stream.openStream().use {
            if (it == null) {
                throw IllegalArgumentException("Stream for path " + stream.absolutPath + " is null")
            }
            val obj = context.createUnmarshaller().unmarshal(it)
            if (obj is XsSchema) {
                obj.schemaLocation = stream.absolutPath
                obj.complete()
                return obj
            } else if (obj is WsdlDefinitions) {
                val schema = obj.types!!.schema!!
                schema.schemaLocation = stream.absolutPath
                schema.complete()
                return schema
            } else {
                throw RuntimeException("Invalid file content, expected xsSchema")
            }
        }
    }

}

abstract class Stream(val absolutPath: String) {

    abstract fun openStream(): InputStream?
}

interface StreamSource {
    fun resolveAbsolute(location: String): Stream

    fun resolveRelative(parentLocation: String, location: String): Stream
}


class PathStreamSource : StreamSource {
    override fun resolveAbsolute(location: String): Stream {
        val path = Paths.get(location).toAbsolutePath()
        val absoluteFileName = path.normalize().toString()
        return PathStream(absoluteFileName, path)
    }

    override fun resolveRelative(parentLocation: String, location: String): Stream {
        val path = Paths.get(parentLocation).parent.resolve(location)
        val absoluteFileName = path.toAbsolutePath().normalize().toString()
        return PathStream(absoluteFileName, path)
    }
}

class PathStream(absolutPath: String, private val path: Path) : Stream(absolutPath) {
    override fun openStream(): InputStream {
        return FileInputStream(path.toFile())
    }
}

class ZipStreamSource(private val inputStream: InputStream) : StreamSource {

    private val content: Map<String, ByteArray>

    init {
        content = HashMap()
        val zis = ZipInputStream(inputStream)

        while (true) {
            var entry: ZipEntry = zis.nextEntry ?: break
            if (!entry.isDirectory) {
                LOGGER.info("Caching entry {} from zip", entry.name)
                val content = readEntry(zis)
                this.content.put(entry.name, content)
            }
        }

    }

    private fun readEntry(zis: ZipInputStream): ByteArray {
        val buffer = ByteArray(8192)

        val baos = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(baos)
        while (true) {
            var read = zis.read(buffer, 0, buffer.size)
            if (read < 0)
                break
            gzip.write(buffer, 0, read)
        }
        gzip.close()
        baos.close()
        return baos.toByteArray()
    }

    override fun resolveAbsolute(location: String): Stream {
        return ZipStream(location, content[location])
    }

    override fun resolveRelative(parentLocation: String, location: String): Stream {

        val relative = resolveRelative(parentLocation, location, '/')
        return resolveAbsolute(relative)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ZipStreamSource::class.java)
    }
}

class ZipStream(absolutePath: String, private val content: ByteArray?) : Stream(absolutePath) {

    override fun openStream(): InputStream? {

        LOGGER.debug("Opening stream to {}", absolutPath)
        return if (content != null)
            GZIPInputStream(ByteArrayInputStream(content))
        else
            null
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ZipStream::class.java)
    }
}

class ClasspathStreamSource(private val classloader: ClassLoader) : StreamSource {
    override fun resolveRelative(parentLocation: String, location: String): Stream {
        val res = resolveRelative(parentLocation, location, '/')
        return ClasspathStream(classloader, res)
    }

    override fun resolveAbsolute(location: String): Stream {
        return ClasspathStream(classloader, location)
    }

}

class ClasspathStream(private val classloader: ClassLoader, absolutPath: String) : Stream(absolutPath) {
    override fun openStream(): InputStream? {
        LOGGER.debug("Opening stream to {}", absolutPath)
        return classloader.getResourceAsStream(absolutPath)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ClasspathStream::class.java)
    }
}


class MapStreamSource(private val content: Map<String, String>) : StreamSource {
    override fun resolveAbsolute(location: String): Stream {
        return StringStream(content[location], location)
    }

    override fun resolveRelative(parentLocation: String, location: String): Stream {
        val res = resolveRelative(parentLocation, location, '/')
        return StringStream(content[res], res)
    }
}

class StringStream(private val content: String?, absolutPath: String) : Stream(absolutPath) {
    override fun openStream(): InputStream? {
        if (content == null) {
            return null
        }
        return ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8))
    }

}

private fun resolveRelative(parent: String, location: String, delimiter: Char): String {
    val resolved = ArrayDeque(parent.split(delimiter).reversed())
    resolved.pop() // remove filename from stack
    for (part in location.split(':')) {
        when (part) {
            "." -> {
            }
            ".." -> resolved.pop()
            else -> resolved.push(part)
        }
    }
    return Joiner.on(delimiter).join(resolved.reversed())
}