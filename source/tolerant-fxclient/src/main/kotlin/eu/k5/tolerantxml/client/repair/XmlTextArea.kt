package eu.k5.tolerantxml.client.repair

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import tornadofx.*
import java.util.regex.Pattern


class XmlTextArea {


    companion object {
        private val XML_TAG = Pattern.compile(
                "(?<ELEMENT>(</?\\h*)(\\w+)(:\\w+)?([^<>]*)(\\h*/?>))"
                        + "|(?<COMMENT><!--[^<>]+-->)"
        )
        private val ATTRIBUTES =
                Pattern.compile("(\\w+:)?(\\w+\\h*)(=)(\\h*\"[^\"]+\")")
        private const val GROUP_OPEN_BRACKET = 2
        private const val GROUP_ELEMENT_NAME = 3
        private const val GROUP_ELEMENT_NAME2 = 4

        private const val GROUP_ATTRIBUTES_SECTION = 5
        private const val GROUP_CLOSE_BRACKET = 6
        private const val GROUP_ATTRIBUTE_PREFIX = 1
        private const val GROUP_ATTRIBUTE_NAME = 2
        private const val GROUP_EQUAL_SYMBOL = 3
        private const val GROUP_ATTRIBUTE_VALUE = 4


        fun newCodeArea(bindings: (CodeArea) -> Unit): CodeArea {
            val codeArea = CodeArea()
            codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

            codeArea.textProperty().addListener { obs: ObservableValue<out String?>?, oldText: String?, newText: String? ->
                codeArea.setStyleSpans(0, computeHighlighting(newText!!))
            }
            codeArea.setPrefSize(400.0, 600.0)
            codeArea.addStylesheet(XmlStyle::class)

            bindings(codeArea)
            return codeArea
        }


        fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
            val matcher = XML_TAG.matcher(text)
            var lastKwEnd = 0
            val spansBuilder: StyleSpansBuilder<Collection<String>> = StyleSpansBuilder()
            while (matcher.find()) {
                spansBuilder.add(emptyList(), matcher.start() - lastKwEnd)
                if (matcher.group("COMMENT") != null) {
                    spansBuilder.add(setOf("comment"), matcher.end() - matcher.start())
                } else {
                    if (matcher.group("ELEMENT") != null) {
                        val attributesText =
                                matcher.group(GROUP_ATTRIBUTES_SECTION)
                        spansBuilder.add(
                                setOf("tagmark"),
                                matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET)
                        )
                        if (matcher.group(GROUP_ELEMENT_NAME2).isNullOrEmpty()) {
                            spansBuilder.add(
                                    setOf("anytag"),
                                    matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET)
                            )
                        } else {
                            spansBuilder.add(
                                    setOf("prefix"),
                                    matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET)
                            )
                            spansBuilder.add(
                                    setOf("anytag"),
                                    matcher.end(GROUP_ELEMENT_NAME2) - matcher.end(GROUP_ELEMENT_NAME)
                            )
                        }
                        if (!attributesText.isEmpty()) {
                            lastKwEnd = 0
                            val amatcher =
                                    ATTRIBUTES.matcher(attributesText)
                            while (amatcher.find()) {
                                spansBuilder.add(emptyList(), amatcher.start() - lastKwEnd)
                                if (!amatcher.group(GROUP_ATTRIBUTE_PREFIX).isNullOrEmpty()) {
                                    spansBuilder.add(
                                            setOf("prefix"),
                                            amatcher.end(GROUP_ATTRIBUTE_PREFIX) - amatcher.start(
                                                    GROUP_ATTRIBUTE_PREFIX
                                            )
                                    )
                                }
                                spansBuilder.add(
                                        setOf("attribute"),
                                        amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(
                                                GROUP_ATTRIBUTE_NAME
                                        )
                                )
                                spansBuilder.add(
                                        setOf("tagmark"),
                                        amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(
                                                GROUP_ATTRIBUTE_NAME
                                        )
                                )
                                spansBuilder.add(
                                        setOf("avalue"),
                                        amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(
                                                GROUP_EQUAL_SYMBOL
                                        )
                                )
                                lastKwEnd = amatcher.end()
                            }
                            if (attributesText.length > lastKwEnd) spansBuilder.add(
                                    emptyList(),
                                    attributesText.length - lastKwEnd
                            )
                        }
                        lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION)
                        spansBuilder.add(
                                setOf("tagmark"),
                                matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd
                        )
                    }
                }
                lastKwEnd = matcher.end()
            }
            spansBuilder.add(emptyList(), text.length - lastKwEnd)
            return spansBuilder.create()
        }
    }

    class XmlStyle : Stylesheet() {
        companion object {
            val tagmark by cssclass()
            val anytag by cssclass()
            val paren by cssclass()
            val attribute by cssclass()
            val prefix by cssclass()
            val avalue by cssclass()
            val comment by cssclass()
        }

        init {
            tagmark {
                fill = Color.GRAY

            }
            anytag {
                fill = Color.CRIMSON
            }
            paren {
                fill = Color.FIREBRICK
                fontWeight = FontWeight.BOLD
            }

            attribute {
                fill = Color.DARKVIOLET
            }

            prefix {
                fill = Color.DARKORCHID
            }

            avalue {
                fill = Color.DARKGREEN
            }

            comment {
                fill = Color.DARKGRAY
                fontStyle = FontPosture.ITALIC
            }
        }

    }
}