package tech.insight.core.bean

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueNull


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class ColumnTest {

    private lateinit var column: Column

    @BeforeEach
    fun setup() {
        column = Column()
    }

    @Test
    fun `checkMyself with valid length and non-primary key`() {
        column.length = 10
        column.primaryKey = false
        column.defaultValue = ValueInt(5)

        assertDoesNotThrow { column.checkMyself() }
    }

    @Test
    fun `checkMyself with invalid length`() {
        column.length = -2

        assertThrows<IllegalStateException> { column.checkMyself() }
    }

    @Test
    fun `checkMyself with length exceeding max value`() {
        column.length = UShort.MAX_VALUE.toInt() + 1

        assertThrows<IllegalStateException> { column.checkMyself() }
    }

    @Test
    fun `checkMyself with primary key and non-null default value`() {
        column.length = 10
        column.primaryKey = true
        column.defaultValue = ValueInt(5)

        assertThrows<IllegalStateException> { column.checkMyself() }
    }

    @Test
    fun `checkMyself with primary key and null default value`() {
        column.length = 10
        column.primaryKey = true
        column.defaultValue = ValueNull

        assertDoesNotThrow { column.checkMyself() }
    }
}
