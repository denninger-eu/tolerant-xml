package eu.k5.tolerant.soapui.plugin

import eu.k5.tolerant.converter.TolerantConverter
import eu.k5.tolerant.converter.TolerantConverterRequest
import eu.k5.tolerant.converter.TolerantConverterResult
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.xml.bind.JAXB


fun main(args: Array<String>) {

    val request = JAXB.unmarshal(File(args[0]), RepairRequest::class.java)

    val repair = Repair(request)
    repair.start()
}

class Repair(private val request: RepairRequest) {

    private val converter: TolerantConverter

    private var result: String? = null

    private var ok: Boolean? = null

    init {
        converter = TolerantConverter(request.converter!!.queryConverterConfiguration(request.converterKey!!))
    }

    private fun convert(request: String): TolerantConverterResult {
        val converterRequest = TolerantConverterRequest()
        converterRequest.content = request
        return converter.convert(converterRequest)
    }

    fun start() {
        SwingUtilities.invokeAndWait({
            val editor = RepairEditor(request.name ?: "", request.request ?: "", this::convert)
            editor.cancel = this::cancel
            editor.ok = this::ok
            editor.isVisible = true
        })

    }

    fun cancel() {

    }

    fun ok(result: String) {
        this.result = result
        ok = true
    }

}

class RepairEditor(
        private val requestName: String,
        private val initial: String,
        private val repair: (String) -> TolerantConverterResult
) : JDialog(null as Frame?, true) {

    val request = RSyntaxTextArea(40, 80);
    private val preview = RSyntaxTextArea(40, 80)

    private val okButton = JButton("Ok")
    private val cancelButton = JButton("Cancel")

    var ok: ((String) -> Unit)? = null
    var cancel: (() -> Unit)? = null


    init {
        val cp = JPanel(BorderLayout());
        request.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_XML;
        request.isCodeFoldingEnabled = true;
        request.isEditable = true
        request.document.insertString(0, initial, null)
        request.document.addDocumentListener(DocumentChanged(this::documentChanged))

        preview.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_XML
        preview.isCodeFoldingEnabled = true
        preview.isEditable = false


        okButton.addActionListener(this::okButtonClicked)
        cancelButton.addActionListener(this::cancelButtonClicked)


        var buttons = JPanel(FlowLayout(FlowLayout.RIGHT))
        buttons.add(okButton)
        buttons.add(cancelButton)

        cp.add(JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                RTextScrollPane(request), RTextScrollPane(preview)), BorderLayout.CENTER)

        cp.add(buttons, BorderLayout.SOUTH)

        contentPane = cp
        title = "Repair " + requestName;
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE;
        pack();
        setLocationRelativeTo(null);
        modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE
    }

    private fun okButtonClicked(event: ActionEvent) {
        this.ok?.invoke(preview.document.getText(0, preview.document.length))
        dispose()
    }

    private fun cancelButtonClicked(event: ActionEvent) {
        this.cancel?.invoke()
        dispose()
    }


    private fun documentChanged() {
        try {
            val result = repair(request.document.getText(0, request.document.length))

            if (result.error != null && !result.error!!.isEmpty()) {
                preview.replaceRange(result.error, 0, preview.document.length)
                okButton.isEnabled = false
            } else {
                preview.replaceRange(result.content, 0, preview.document.length)
                okButton.isEnabled = true
            }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
    }


}

class DocumentChanged(private val handle: () -> Unit) : DocumentListener {

    override fun removeUpdate(e: DocumentEvent?) = handle()

    override fun insertUpdate(e: DocumentEvent?) = handle()

    override fun changedUpdate(e: DocumentEvent?) = handle()

}