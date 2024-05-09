package tech.insight.engine.innodb.utils

import tech.insight.engine.innodb.page.ConstantSize
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.type.PageType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object Console {


    private val LINE: String = System.getProperty("line.separator", "\n")
    private const val LINE_LENGTH = 138
    private const val DATA_LINE_LENGTH = 130
    private val BASE_TITLE_LINE = "+${"-".repeat(128)}+"
    private val BOX_BORDER = "${"-".repeat(9)}+${"-".repeat(64)}+${"-".repeat(63)}+"
    private const val BLANK = " "
    private const val LINE_COUNT = 1 shl 5 // aka 32
    private val preLine: Array<String>
    private val byte2String: Array<String>
    private val titleCache: MutableMap<String, String> = mutableMapOf()

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


    fun pageDescription(innoDbPage: InnoDbPage) {
        val bytes = innoDbPage.toBytes()
        val totalLine = bytes.size / LINE_COUNT
        val breakLength = with(innoDbPage) {
            bytes.size - freeSpace.toInt() - pageDirectory.toBytes().size - fileTrailer.toBytes().size
        }
        val dump = StringBuilder(ConstantSize.PAGE.size() shr 2)
        dump.append(BLANK.repeat(9))
        when (innoDbPage.pageType()) {
            PageType.FIL_PAGE_TYPE_UNDO_LOG -> dump.appendLine(createTitle("Page(log)"))
            PageType.FIL_PAGE_INDEX -> dump.appendLine(createTitle("Page(data)"))
            PageType.FIL_PAGE_INODE -> dump.appendLine(createTitle("Page(index)"))
        }
        dump.append(BLANK.repeat(9))
        dump.append("| offset: [${innoDbPage.fileHeader.offset}]$BLANK")
        dump.append("user record: [${innoDbPage.pageHeader.recordCount}]$BLANK")
        dump.append("page directory: [${innoDbPage.pageDirectory.slotCount()}]$BLANK")
        dump.append(LINE)
        dump.append(BLANK.repeat(9))
        dump.append(
            """
            +----------------------------------------------------------------+---------------------------------------------------------------+
                     |  0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f  10  11  12  13  14  15  16  17  18  19  1a  1b  1c  1d  1e  1f |
        """.trimIndent()
        )
        dump.appendLine()
        dump.append(BOX_BORDER)
        dump.appendLine()
        val beforeFreeSpaceRows = (breakLength / LINE_COUNT) + 1
        if (beforeFreeSpaceRows >= totalLine - 10) {
            dumpAllBytes(dump, bytes)
        } else {
            for (row in 0 until beforeFreeSpaceRows) {
                dumpSkipBytesRow(dump, bytes, row * LINE_COUNT)
            }
            val remainRows = with(innoDbPage) {
                ((pageDirectory.toBytes().size + fileTrailer.toBytes().size) / LINE_COUNT) + 1
            }
            val startIndex = (totalLine - remainRows) * LINE_COUNT
            appendSkipLine(dump, beforeFreeSpaceRows, remainRows)
            for (row in 0 until remainRows) {
                val rowStart = startIndex + row * LINE_COUNT
                dumpSkipBytesRow(dump, bytes, rowStart)
            }
        }
        dump.append(BOX_BORDER)
        dump.append(LINE)
        println(dump.toString())
    }

    private fun dumpSkipBytesRow(dump: StringBuilder, bytes: ByteArray, startIndex: Int) {
        appendLinePre(dump, startIndex / LINE_COUNT)
        for (col in 0 until LINE_COUNT) {
            dump.append(byte2String[bytes[startIndex + col].toUByte().toInt()])
            dump.append(BLANK)
        }
        dump.append("|")
        dump.append(LINE)
    }

    private fun dumpAllBytes(dump: StringBuilder, bytes: ByteArray) {
        val rows = bytes.size / LINE_COUNT
        for (row in 0 until rows) {
            val rowStart = row * LINE_COUNT
            appendLinePre(dump, row)
            for (col in 0 until LINE_COUNT) {
                dump.append(byte2String[bytes[rowStart + col].toUByte().toInt()])
                dump.append(BLANK)
            }
            dump.append("|")
            dump.append(LINE)
        }
    }


    private fun appendSkipLine(dump: StringBuilder, beforeRow: Int, remainRow: Int) {
        val preStringBuilder = StringBuilder(12)
        preStringBuilder.append("|........|")
        val omitBytes = ((ConstantSize.PAGE.size() shr 5) - beforeRow - remainRow) shl 5
        preStringBuilder.append("     omit free space length:[0${java.lang.Long.toHexString(omitBytes.toLong() and 0xFFFFFFFFL or 0x100000000L)}]")
        preStringBuilder.setCharAt(preStringBuilder.length - 9, 'X')
        preStringBuilder.append("[$omitBytes]")
        preStringBuilder.append(BLANK.repeat(LINE_LENGTH - preStringBuilder.length))
        preStringBuilder.append("|")
        //        val skipRow = beforeRow + remainRow
        //        val skipBytes = skipRow shl 5
        //        preStringBuilder.append(java.lang.Long.toHexString(skipBytes.toLong() and 0xFFFFFFFFL or 0x100000000L))

        dump.appendLine(preStringBuilder)
    }

    private fun appendLinePre(dump: StringBuilder, row: Int) {
        dump.append(preLine[row])
    }

    private fun StringBuilder.appendLine(): StringBuilder {
        return this.append(LINE)
    }

    private fun createTitle(text: String): String {
        return titleCache.computeIfAbsent(text) {
            val left = 1 + ((BASE_TITLE_LINE.length - 2 - text.length) shr 1)
            BASE_TITLE_LINE.replaceRange(left, left + text.length, text)
        }
    }
}

