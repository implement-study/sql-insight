package tech.insight.core.bean

import org.gongxuanzhang.easybyte.core.ByteWrapper


/**
 * user record represents a physics row in disk.
 * different row format have different performance in storage.
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface UserRecord : ByteWrapper, Row {
    /**
     * the record to byte array.
     */
    fun rowBytes(): ByteArray

    /**
     * absolute offset in page， field not in page , application calculate
     */
    fun offset(): Int

    /**
     * [offset]
     * absolute offset in page don't in source.
     * require set up
     */
    fun setOffset(offset: Int)

    /**
     * next node relative offset
     */
    fun nextRecordOffset(): Int

    /**
     * @return user record delete sign
     */
    fun deleteSign(): Boolean

    /**
     * [rowBytes]
     */
    override fun toBytes(): ByteArray {
        return rowBytes()
    }
}
