package tech.insight.core.bean

import tech.insight.buffer.SerializableObject


/**
 * user record represents a physics row in disk.
 * different row format have different performance in storage.
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface UserRecord : SerializableObject, Row {
    /**
     * the record to byte array.
     */
    fun rowBytes(): ByteArray

    /**
     * absolute offset in page， field not in page , application calculate
     */
    fun offsetInPage(): Int

    /**
     * [offset]
     * absolute offset in page don't in source.
     * require set up
     */
    fun setOffsetInPage(offset: Int)

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
