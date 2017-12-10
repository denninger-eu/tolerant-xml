package eu.k5.tolerantreader.xs

import com.google.common.base.Joiner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.xml.bind.JAXBContext

object Schema {
    val context = JAXBContext.newInstance(XsSchema::class.java)

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
            val obj = context.createUnmarshaller().unmarshal(it)
            if (obj is XsSchema) {
                obj.schemaLocation = stream.absolutPath
                obj.complete()
                return obj
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

class ClasspathStreamSource(private val classloader: ClassLoader) : StreamSource {
    override fun resolveRelative(parentLocation: String, location: String): Stream {
        val resolved = ArrayDeque(parentLocation.split('/').reversed())

        resolved.pop() // remove filename from stack
        for (part in location.split(':')) {
            when (part) {
                "." -> {
                }
                ".." -> resolved.pop()
                else -> resolved.push(part)
            }
        }
        return ClasspathStream(classloader, Joiner.on('/').join(resolved.reversed()))
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