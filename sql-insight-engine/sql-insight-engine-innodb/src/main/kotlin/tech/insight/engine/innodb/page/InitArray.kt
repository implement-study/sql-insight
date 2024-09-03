package tech.insight.engine.innodb.page

import io.netty.buffer.Unpooled
import tech.insight.engine.innodb.page.compact.RecordType
import tech.insight.engine.innodb.page.type.DataPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
val initFileHeaderArray: ByteArray = run {
    Unpooled.buffer(ConstantSize.FILE_HEADER.size())
        .writeInt(FileHeader.checkSum)
        .writeInt(0)  //  offset
        //  todo allocate page type
        .writeShort(DataPage.FIL_PAGE_INDEX_VALUE) // page type 
        .writeInt(0) //  pre offset
        .writeInt(0) //  next offset
        .writeLong(0) //  lsn
        .writeLong(0) //  flush lsn
        .writeInt(0) //  space id
        .array()
}

val initPageHeaderArray: ByteArray = run {
    Unpooled.buffer(ConstantSize.PAGE_HEADER.size())
        .writeShort(0) //  slot count
        .writeShort(ConstantSize.USER_RECORDS.offset) //  heap top
        .writeShort(2) //  absolute record count
        .writeShort(0) //  record count
        .writeShort(0) //  free
        .writeShort(0) //  garbage
        .writeShort(0) //  last insert offset
        .writeShort(0) //  direction
        .writeShort(0) //  direction count
        .writeLong(0) //  maxTrxId
        .writeShort(0) //  level
        .writeLong(0) //  index id
        .writeShort(0) //  segLeafPre
        .writeLong(0) //  segLeaf
        .writeShort(0) //  segTopPre
        .writeLong(0) //  segTop
        .array()
}

val initInfimumArray: ByteArray = run {
    Unpooled.buffer(ConstantSize.INFIMUM.size())
        //  record header
        .writeByte(1)
        .writeByte(0)
        .writeByte(RecordType.INFIMUM.value)
        .writeShort(ConstantSize.SUPREMUM.offset - ConstantSize.INFIMUM.offset)
        .writeBytes(Infimum.INFIMUM_BODY_ARRAY)
        .array()
}

val initSupremumArray: ByteArray = run {
    Unpooled.buffer(ConstantSize.SUPREMUM.size())
        //  record header
        .writeByte(1)
        .writeByte(0)
        .writeByte((1 shl 3) and RecordType.SUPREMUM.value)
        .writeShort(0)
        .writeBytes(Supremum.SUPREMUM_BODY_ARRAY)
        .array()
}

val initPageDirectoryArray: ByteArray = run {
    byteArrayOf(0, ConstantSize.SUPREMUM.offset().toByte(), 0, ConstantSize.INFIMUM.offset().toByte())
}


val initFileTrailerArray: ByteArray = run {
    Unpooled.buffer(ConstantSize.FILE_TRAILER.size()).writeInt(FileHeader.checkSum).writeInt(0).array()
}

val initPageArray: ByteArray = run {
    Unpooled.buffer(ConstantSize.PAGE.size()).apply {
        writeBytes(initFileHeaderArray)
        writeBytes(initPageHeaderArray)
        writeBytes(initInfimumArray)
        writeBytes(initSupremumArray)
        setBytes(ConstantSize.FILE_TRAILER.offset(), initFileTrailerArray)
        setBytes(ConstantSize.FILE_TRAILER.offset() - Short.SIZE_BYTES * 2, initPageDirectoryArray)
    }.array()
}
