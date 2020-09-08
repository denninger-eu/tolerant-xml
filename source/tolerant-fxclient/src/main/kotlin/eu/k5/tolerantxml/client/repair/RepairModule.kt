package eu.k5.tolerantxml.client.repair

import eu.k5.soapui.fx.NewTabEvent
import eu.k5.soapui.fx.SoapUiModule
import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.config.*
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


        val scope = RepairScope(createTolerantConverter(event))

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

        writerConfig.explicitPrefix = explicitPrefix()
        return writerConfig
    }

    private fun explicitPrefix(): List<Explicit> {
        val soap = Explicit("http://schemas.xmlsoap.org/wsdl/soap/", "soap")
        val env = Explicit("http://schemas.xmlsoap.org/soap/envelope/", "soapenv")
        val xsi = Explicit("http://www.w3.org/2001/XMLSchema-instance", "xsi")

        return listOf(soap, env, xsi)
    }
}