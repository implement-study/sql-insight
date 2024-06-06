package tech.insight.core.bean.condition

import tech.insight.core.bean.value.Value
import kotlin.experimental.or


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class ExpressionRange(val start: Value<*>, val end: Value<*>, val type: RangeType) {

    init {
        require(type != RangeType.IMPOSSIBLE && end < start) {
            "end value must be greater than start value"
        }
    }

    operator fun contains(value: Value<*>): Boolean {
        if (value < start || value > end) {
            return false
        }
        if (value == start && (type == RangeType.OPEN_CLOSE || type == RangeType.OPEN_OPEN)) {
            return false
        }
        if (value == end && (type == RangeType.CLOSE_OPEN || type == RangeType.OPEN_OPEN)) {
            return false
        }
        return true
    }

    fun union(other: ExpressionRange): ExpressionRange {
        if (this.type == RangeType.IMPOSSIBLE) {
            return this
        }
        if (other.type == RangeType.IMPOSSIBLE) {
            return other
        }

        val (newStart, startType) = if (this.start > other.start) {
            this.start to this.type
        } else {
            other.start to other.type
        }

        val (newEnd, endType) = if (this.end < other.end) {
            this.end to this.type
        } else {
            other.end to other.type
        }
        if (newStart > newEnd) {
            return ExpressionRange(newStart, newEnd, RangeType.IMPOSSIBLE)
        }
        var bit: Byte = 0
        if (startType == RangeType.CLOSE_OPEN || startType == RangeType.CLOSE_CLOSE) {
            bit = bit or 0b10
        }
        if (endType == RangeType.OPEN_CLOSE || endType == RangeType.CLOSE_CLOSE) {
            bit = bit or 0b01
        }
        val rangeType = RangeType.valueOf(bit)
        if (newStart == newEnd && rangeType != RangeType.CLOSE_CLOSE) {
            return ExpressionRange(newStart, newEnd, RangeType.IMPOSSIBLE)
        }
        return ExpressionRange(newStart, newEnd, RangeType.valueOf(bit))
    }

    /**
     * Returns whether the range contains only one value
     */
    fun unique(): Boolean {
        return start == end && type == RangeType.CLOSE_CLOSE
    }
}

enum class RangeType(val bit: Byte) {
    OPEN_OPEN(0b00),
    OPEN_CLOSE(0b01),
    CLOSE_OPEN(0b10),
    CLOSE_CLOSE(0b11),
    IMPOSSIBLE(Byte.MAX_VALUE);

    companion object {
        fun valueOf(bit: Byte): RangeType {
            return entries.first { it.bit == bit }
        }
    }

}
