package tech.insight.core.util

/**
 * @author gongxuanzhangmelt@gmail.com
 **/

fun truncateStringIfTooLong(input: String, maxLength: Int = 64): String {
    return if (input.length > maxLength) {
        input.substring(0, maxLength) + "..."
    } else {
        input
    }
}

