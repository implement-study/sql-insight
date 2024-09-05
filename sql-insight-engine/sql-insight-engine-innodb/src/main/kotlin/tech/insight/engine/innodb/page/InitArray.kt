package tech.insight.engine.innodb.page

import tech.insight.buffer.byteArray
import tech.insight.buffer.byteBuf
import tech.insight.engine.innodb.page.compact.RecordType
import tech.insight.engine.innodb.page.type.DataPage


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/


val initNormalRecordHeader: () -> ByteArray = {
    byteBuf(ConstantSize.RECORD_HEADER.size).apply {
        writeByte(0)
        writeByte(0)
        writeByte(RecordType.NORMAL.value)
        writeShort(0)
    }.array()
}

val initPageRecordHeader: () -> ByteArray = {
    byteBuf(ConstantSize.RECORD_HEADER.size).apply {
        writeByte(0)
        writeByte(0)
        writeByte(RecordType.PAGE.value)
        writeShort(0)
    }.array()
}

val initInfimumRecordHeader: () -> ByteArray = {
    byteBuf(ConstantSize.RECORD_HEADER.size).apply {
        writeByte(1)
        writeByte(0)
        writeByte(RecordType.INFIMUM.value)
        writeShort(Supremum.OFFSET_IN_PAGE - Infimum.OFFSET_IN_PAGE)
    }.array()
}

val initSupremumRecordHeader: () -> ByteArray = {
    byteBuf(ConstantSize.RECORD_HEADER.size).apply {
        writeByte(1)
        writeByte(0)
        writeByte((1 shl 3) and RecordType.SUPREMUM.value)
        writeShort(0)
    }.array()
}

val initUnknownRecordHeader: () -> ByteArray = {
    TODO()
}


val initFileHeaderArray: () -> ByteArray = {
    byteBuf(ConstantSize.FILE_HEADER.size)
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

val initPageHeaderArray: () -> ByteArray = {
    byteBuf(ConstantSize.PAGE_HEADER.size)
        .writeShort(2) //  slot count
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

val initInfimumArray: () -> ByteArray = {
    byteBuf(ConstantSize.INFIMUM.size)
        .writeBytes(initInfimumRecordHeader.invoke())
        .writeBytes(Infimum.INFIMUM_BODY_ARRAY)
        .array()
}

val initSupremumArray: () -> ByteArray = {
    byteBuf(ConstantSize.SUPREMUM.size)
        .writeBytes(initSupremumRecordHeader.invoke())
        .writeBytes(Supremum.SUPREMUM_BODY_ARRAY)
        .array()
}

val initPageDirectoryArray: () -> ByteArray = {
    Supremum.OFFSET_IN_PAGE.toShort().byteArray() + Infimum.OFFSET_IN_PAGE.toShort().byteArray()
}


val initFileTrailerArray: () -> ByteArray = {
    byteBuf(ConstantSize.FILE_TRAILER.size).writeInt(FileHeader.checkSum).writeInt(0).array()
}

val initPageArray: () -> ByteArray = {
    byteBuf(ConstantSize.PAGE.size).apply {
        writeBytes(initFileHeaderArray.invoke())
        writeBytes(initPageHeaderArray.invoke())
        writeBytes(initInfimumArray.invoke())
        writeBytes(initSupremumArray.invoke())
        setBytes(ConstantSize.FILE_TRAILER.offset, initFileTrailerArray.invoke())
        setBytes(ConstantSize.FILE_TRAILER.offset - Short.SIZE_BYTES * 2, initPageDirectoryArray.invoke())
    }.array()
}
