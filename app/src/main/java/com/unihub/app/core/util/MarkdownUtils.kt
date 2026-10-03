package com.unihub.app.core.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object MarkdownUtils {

    /**
     * Parses lightweight markdown syntax:
     * - **bold**
     * - *italic*
     * - `code`
     * - bullet lists: lines starting with "- " or "* " converted to "• "
     */
    fun parseMarkdown(text: String): AnnotatedString {
        val lines = text.split("\n")
        return buildAnnotatedString {
            lines.forEachIndexed { lineIndex, rawLine ->
                val line = when {
                    rawLine.startsWith("- ") -> "  • " + rawLine.removePrefix("- ")
                    rawLine.startsWith("* ") -> "  • " + rawLine.removePrefix("* ")
                    rawLine.startsWith("### ") -> rawLine.removePrefix("### ")
                    rawLine.startsWith("## ") -> rawLine.removePrefix("## ")
                    rawLine.startsWith("# ") -> rawLine.removePrefix("# ")
                    else -> rawLine
                }

                val isHeading = rawLine.startsWith("#")
                if (isHeading) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                }

                parseLineInlineFormatting(line, this)

                if (isHeading) {
                    pop()
                }

                if (lineIndex < lines.size - 1) {
                    append("\n")
                }
            }
        }
    }

    private fun parseLineInlineFormatting(line: String, builder: AnnotatedString.Builder) {
        var i = 0
        val len = line.length

        while (i < len) {
            when {
                // **bold**
                i + 1 < len && line[i] == '*' && line[i + 1] == '*' -> {
                    val end = line.indexOf("**", i + 2)
                    if (end != -1) {
                        builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            // Check for nested italic inside bold
                            val boldContent = line.substring(i + 2, end)
                            parseSimpleItalicAndCode(boldContent, this)
                        }
                        i = end + 2
                    } else {
                        builder.append(line[i])
                        i++
                    }
                }
                // `code`
                line[i] == '`' -> {
                    val end = line.indexOf('`', i + 1)
                    if (end != -1) {
                        builder.withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append(line.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        builder.append(line[i])
                        i++
                    }
                }
                // *italic*
                line[i] == '*' -> {
                    val end = line.indexOf('*', i + 1)
                    if (end != -1) {
                        builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(line.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        builder.append(line[i])
                        i++
                    }
                }
                else -> {
                    builder.append(line[i])
                    i++
                }
            }
        }
    }

    private fun parseSimpleItalicAndCode(text: String, builder: AnnotatedString.Builder) {
        var i = 0
        while (i < text.length) {
            if (text[i] == '*' && i + 1 < text.length) {
                val end = text.indexOf('*', i + 1)
                if (end != -1) {
                    builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                    continue
                }
            }
            builder.append(text[i])
            i++
        }
    }
}
