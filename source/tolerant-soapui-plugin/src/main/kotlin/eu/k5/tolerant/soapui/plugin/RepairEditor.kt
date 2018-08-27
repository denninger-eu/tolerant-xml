package eu.k5.tolerant.soapui.plugin

import eu.k5.tolerant.converter.TolerantConverterResult
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.JSplitPane
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


fun main(args: Array<String>) {

    // Start all Swing applications on the EDT.
    SwingUtilities.invokeLater() {
        val editor = RepairEditor("Example", "<test></test>") { TolerantConverterResult(it) }
        editor.isVisible = true
    }
}

class RepairEditor(
        private val requestName: String,
        private val initial: String,
        private val repair: (String) -> TolerantConverterResult
) : JDialog(null as Frame?, true) {

    val request = RTextArea(40, 80);
    private val preview = RSyntaxTextArea(40, 80)

    private val okButton = JButton("Ok")
    private val cancelButton = JButton("Cancel")

    var ok: ((String) -> Unit)? = null
    var cancel: (() -> Unit)? = null


    init {
        println("changed" + Thread.currentThread().name)
        val cp = JPanel(BorderLayout());

        println(request.javaClass.name)

        //   request.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_XML;
        //  request.isCodeFoldingEnabled = true;
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
            t.printStackTrace()
        }
    }


}

class DocumentChanged(private val handle: () -> Unit) : DocumentListener {

    override fun removeUpdate(e: DocumentEvent?) = handle()

    override fun insertUpdate(e: DocumentEvent?) = handle()

    override fun changedUpdate(e: DocumentEvent?) = handle()

}