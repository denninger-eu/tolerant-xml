package eu.k5.tolerantxml.client.repair

import eu.k5.soapui.fx.NewTabEvent
import eu.k5.soapui.fx.SoapUiModule
import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.config.ReaderConfig
import eu.k5.tolerant.converter.config.TolerantConverterConfiguration
import eu.k5.tolerant.converter.config.WriterConfig
import eu.k5.tolerant.converter.config.XsdContent
import javafx.scene.control.Tab
import tornadofx.Component
import tornadofx.find
import java.util.ArrayList

class RepairModule : Component(), SoapUiModule {
    override val name: String = "tolerantrepair"

    override fun init() {

        subscribe<CreateTolerantConverter> {
            println("Create subscribed")
            startTab(it)
        }
    }

    private fun startTab(event: CreateTolerantConverter) {
        println("Event received")

        println(event.xsds)



        val scope = RepairScope(     createTolerantConverter(event))

        val view = find<RepairView>(scope)

        val tab = Tab("Repair " + event.name)
        tab.contentProperty().set(view.root)
        fire(NewTabEvent(tab))

        println("New tab fired")
    }

    fun createTolerantConverter(event: CreateTolerantConverter): TolerantConverter {
        val writerConfig = createWriterConfiguration(event)
        val readerConfig = createReaderConfig(event)

        val config = TolerantConverterConfiguration(key = "id", name = "test", reader = readerConfig)
        return TolerantConverter(config)
    }

    private fun createReaderConfig(event: CreateTolerantConverter): ReaderConfig {
        val xsdContents = ArrayList<XsdContent>()

        for (xsd in event.xsds.entries) {
            val xsdContent = XsdContent()
            xsdContent.content = xsd.value
            xsdContent.name = xsd.key
            xsdContents.add(xsdContent)
        }

        val readerConfig = ReaderConfig()
        readerConfig.xsd = event.mainPart
        readerConfig.xsdContent = xsdContents
        readerConfig.key = event.name
        return readerConfig
    }

    private fun createWriterConfiguration(event: CreateTolerantConverter): WriterConfig {
        val writerConfig = WriterConfig()
        writerConfig.key = event.name
        return writerConfig
    }
}