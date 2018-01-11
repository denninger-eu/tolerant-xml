package eu.k5.tolerant.webclient

import com.google.inject.Guice
import com.google.inject.Module
import eu.k5.tolerant.webclient.converter.Converter
import eu.k5.tolerant.webclient.resources.ConvertResource
import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.servlets.assets.AssetServlet
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import java.net.URL
import java.nio.charset.StandardCharsets


fun main(args: Array<String>) {
    ConverterApplication().run(*args)
}


class ConverterApplication : Application<ConverterConfiguration>() {


    override fun initialize(bootstrap: Bootstrap<ConverterConfiguration>) {
        //bootstrap.addBundle(AssetsBundle("/META-INF/static", "/static", "", "static"))


        //bootstrap.addBundle(FileAssetsBundle("src/main/resources/META-INF/static", "/file"))

        bootstrap.addBundle(object : AssetsBundle("/META-INF/static", "/static", "static") {
            override fun createServlet(): AssetServlet {
                return object : AssetServlet(resourcePath, uriPath, indexFile, StandardCharsets.UTF_8) {
                    override fun getResourceUrl(absoluteRequestedResourcePath: String): URL {
                        if (absoluteRequestedResourcePath == "static/kotlin.js") {
                            return super.getResourceUrl("kotlin.js")
                        }
                        return super.getResourceUrl(absoluteRequestedResourcePath)
                    }

                }
            }
        })
    }

    override fun run(configuration: ConverterConfiguration, environment: Environment) {

        val converter = Converter()

        val injector = Guice.createInjector(Module {
            it.bind(Converter::class.java).toInstance(converter)
        })
        //environment.jersey().register(MultiPartFeature::class.java)
        environment.jersey().register(injector.getInstance(ConvertResource::class.java))
        //environment.jersey().register(injector.getInstance(SoapUiResource::class.java))
    }
}