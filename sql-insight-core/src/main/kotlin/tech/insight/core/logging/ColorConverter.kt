package tech.insight.core.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter
import java.util.*


/**
 * fine tuned spring boot ColorConverter
 */
open class ColorConverter : CompositeConverter<ILoggingEvent>() {


    override fun transform(event: ILoggingEvent, `in`: String): String {
        var element = ELEMENTS[firstOption]
        if (element == null) {
            // Assume highlighting
            element = LEVELS[event.level.toInteger()]
            element = element ?: AnsiColor.GREEN
        }
        val result = toAnsiString(`in`, element)
        return result
    }

    private fun toAnsiString(`in`: String, element: AnsiElement): String {
        return AnsiOutput.toString(element, `in`)
    }

    companion object {
        private val ELEMENTS: Map<String, AnsiElement>

        init {
            val ansiElements: MutableMap<String, AnsiElement> = HashMap()
            ansiElements["faint"] = AnsiStyle.FAINT
            ansiElements["red"] = AnsiColor.RED
            ansiElements["green"] = AnsiColor.GREEN
            ansiElements["yellow"] = AnsiColor.YELLOW
            ansiElements["blue"] = AnsiColor.BLUE
            ansiElements["magenta"] = AnsiColor.MAGENTA
            ansiElements["cyan"] = AnsiColor.CYAN
            ELEMENTS = Collections.unmodifiableMap(ansiElements)
            PatternLayout.DEFAULT_CONVERTER_MAP["gaga"] = ColorConverter::class.java.name
        }

        private val LEVELS: Map<Int, AnsiElement>

        init {
            val ansiLevels: MutableMap<Int, AnsiElement> = HashMap()
            ansiLevels[Level.ERROR_INTEGER] = AnsiColor.RED
            ansiLevels[Level.WARN_INTEGER] = AnsiColor.YELLOW
            LEVELS = Collections.unmodifiableMap(ansiLevels)
        }
    }
}

enum class AnsiColor(private val code: String) : AnsiElement {
    DEFAULT("39"),
    BLACK("30"),
    RED("31"),
    GREEN("32"),
    YELLOW("33"),
    BLUE("34"),
    MAGENTA("35"),
    CYAN("36"),
    WHITE("37"),
    BRIGHT_BLACK("90"),
    BRIGHT_RED("91"),
    BRIGHT_GREEN("92"),
    BRIGHT_YELLOW("93"),
    BRIGHT_BLUE("94"),
    BRIGHT_MAGENTA("95"),
    BRIGHT_CYAN("96"),
    BRIGHT_WHITE("97");

    override fun toString(): String {
        return this.code
    }
}

interface AnsiElement {
    /**
     * @return the ANSI escape code
     */
    override fun toString(): String
}

object AnsiOutput {
    private const val ENCODE_JOIN = ";"
    private var ansiCapable: Boolean? = null
    private val OPERATING_SYSTEM_NAME = System.getProperty("os.name").lowercase()
    private const val ENCODE_START = "\u001b["
    private const val ENCODE_END = "m"
    private val RESET = "0;" + AnsiColor.DEFAULT


    /**
     * Create a new ANSI string from the specified elements. Any [AnsiElement]s will
     * be encoded as required.
     * @param elements the elements to encode
     * @return a string of the encoded elements
     */
    fun toString(vararg elements: Any): String {
        val sb = StringBuilder()
        buildString(sb, elements)
        return sb.toString()
    }

    private fun buildString(sb: StringBuilder, elements: Array<out Any>) {
        var writingAnsi = false
        var containsEncoding = false
        for (element in elements) {
            if (element is AnsiElement) {
                containsEncoding = true
                if (!writingAnsi) {
                    sb.append(ENCODE_START)
                    writingAnsi = true
                } else {
                    sb.append(ENCODE_JOIN)
                }
            } else {
                if (writingAnsi) {
                    sb.append(ENCODE_END)
                    writingAnsi = false
                }
            }
            sb.append(element)
        }
        if (containsEncoding) {
            sb.append(if (writingAnsi) ENCODE_JOIN else ENCODE_START)
            sb.append(RESET)
            sb.append(ENCODE_END)
        }
    }

}

enum class AnsiStyle(private val code: String) : AnsiElement {
    NORMAL("0"),
    BOLD("1"),
    FAINT("2"),
    ITALIC("3"),
    UNDERLINE("4");

    override fun toString(): String {
        return this.code
    }
}
