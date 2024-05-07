package tech.insight.engine.innodb.utils

import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object Console {


    private val LINE: String = System.getProperty("line.separator", "\n")
    private val BASE_TITLE_LINE = "+${"-".repeat(148)}+"
    private val DATA_PAGE_TITLE_LINE =
        "+-------------------------------------------------------Page(Data)---------------------------------------------------------------+"
    private const val BLANK = " "
    private const val LINE_COUNT = 1 shl 5 // aka 32
    private val preLine: Array<String>
    private val byte2String: Array<String>

    init {
        preLine = Array(ConstantSize.PAGE.size() / LINE_COUNT) {
            val buf = StringBuilder(12)
            buf.append(java.lang.Long.toHexString(it.toLong() shl 5 and 0xFFFFFFFFL or 0x100000000L))
            buf.setCharAt(buf.length - 9, '|')
            buf.append('|')
            buf.toString()
        }
        byte2String = Array(1 shl Byte.SIZE_BITS) {
            val buf = StringBuilder(3)
            buf.append(BLANK)
            if (it <= 0xf) {
                buf.append('0')
            }
            buf.append(Integer.toHexString(it))
            buf.toString()
        }

    }


    fun a(bytes: ByteArray) {
        val dump = StringBuilder(ConstantSize.PAGE.size() shr 2)
        dump.append(BLANK.repeat(9))
        //        when(innoDbPage.pageType()){
        //            PageType.FIL_PAGE_TYPE_UNDO_LOG -> TODO()
        //            PageType.FIL_PAGE_INDEX -> TODO()
        //            PageType.FIL_PAGE_INODE -> TODO()
        //        }
        dump.append(DATA_PAGE_TITLE_LINE)
        dump.append(LINE)
        dump.append(
            """
            +----------------------------------------------------------------+---------------------------------------------------------------+
                     |  0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f  10  11  12  13  14  15  16  17  18  19  1a  1b  1c  1d  1e  1f |
        """.trimIndent()
        )
        dump.append(LINE)
        val rows = bytes.size / LINE_COUNT
        for (row in 0 until rows) {
            val rowStart = row * LINE_COUNT
            appendLinePre(dump, row)
            for (col in 0 until LINE_COUNT) {
                dump.append(byte2String[bytes[rowStart + col].toInt()])
                dump.append(BLANK)
            }
            dump.append("|")
            dump.append(LINE)
        }

        println(dump.toString())
    }

    fun printDescription(innoDbPage: InnoDbPage) {
        val bytes = innoDbPage.toBytes()
        val dump = StringBuilder(ConstantSize.PAGE.size() shr 2)
        dump.append(BLANK.repeat(9))
        //        when(innoDbPage.pageType()){
        //            PageType.FIL_PAGE_TYPE_UNDO_LOG -> TODO()
        //            PageType.FIL_PAGE_INDEX -> TODO()
        //            PageType.FIL_PAGE_INODE -> TODO()
        //        }
        dump.append(DATA_PAGE_TITLE_LINE)
        dump.append(
            """
            +----------------------------------------------------------------+---------------------------------------------------------------+
                     |  0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f  10  11  12  13  14  15  16  17  18  19  1a  1b  1c  1d  1e  1f |
        """.trimIndent()
        )
        dump.append(LINE)
        val rows = bytes.size / LINE_COUNT
        for (row in 0 until rows) {
            val rowStart = row * LINE_COUNT
            appendLinePre(dump, row)
            for (col in 0 until LINE_COUNT) {
                dump.append(byte2String[bytes[rowStart + col].toInt()])
                dump.append(BLANK)
            }
            dump.append("|")
            dump.append(LINE)
        }

        println(dump.toString())
    }

    private fun appendLinePre(dump: StringBuilder, row: Int) {
        dump.append(preLine[row])
    }

     fun createTitle(text: String): String {
        val left = 1 + ((BASE_TITLE_LINE.length - 2 - text.length) shr 1)
        return BASE_TITLE_LINE.replaceRange(left, left + text.length, text)
    }
}

fun main() {
    val console = Console
    val bytes = ByteArray(32)
    bytes[0] = 1
    Console.a(bytes)
    println(Console.createTitle("data page"))
}
