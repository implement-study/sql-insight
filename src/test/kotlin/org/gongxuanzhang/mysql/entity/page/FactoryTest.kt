/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity.page

import com.alibaba.fastjson2.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class FactoryTest {

    @Test
    fun factoryTest() {
        val innoDbPage = innodbPage()
        assertEquals(FileHeaderFactory().swap(innoDbPage.fileHeader.toBytes()), innoDbPage.fileHeader)
        assertEquals(PageHeaderFactory().swap(innoDbPage.pageHeader.toBytes()), innoDbPage.pageHeader)
        assertEquals(InfimumFactory().swap(innoDbPage.infimum.toBytes()), innoDbPage.infimum)
        assertEquals(SupremumFactory().swap(innoDbPage.supremum.toBytes()), innoDbPage.supremum)
        assertEquals(FileTrailerFactory().swap(innoDbPage.fileTrailer.toBytes()), innoDbPage.fileTrailer)
        assertEquals(InnoDbPageFactory.getInstance().swap(innoDbPage.toBytes()), innoDbPage)
    }


    private fun innodbPage(): InnoDbPage {
        val page = InnoDbPage()
        val fileHeaderString =
            "{\"checkSum\":12345,\"flushLsn\":0,\"lsn\":0,\"next\":0,\"offset\":0,\"pageType\":17855,\"pre\":0,\"spaceId\":1}"
        val fileHeader = JSONObject.parseObject(fileHeaderString, FileHeader::class.java)
        page.fileHeader = fileHeader
        val pageHeaderString =
            "{\"absoluteRecordCount\":3,\"direction\":0,\"directionCount\":0,\"free\":0,\"garbage\":0,\"heapTop\":172,\"indexId\":0,\"lastInsertOffset\":172,\"level\":0,\"maxTransactionId\":0,\"recordCount\":1,\"segLeaf\":0,\"segTop\":0,\"slotCount\":2}"
        val pageHeader = JSONObject.parseObject(pageHeaderString, PageHeader::class.java)
        page.pageHeader = pageHeader
        val infimumRecordHeaderStr =
            "{\"NOwned\":1,\"delete\":false,\"heapNo\":0,\"minRec\":false,\"nextRecordOffset\":120,\"recordType\":2,\"source\":[1,0,2,0,120]}"
        val infimumRecordHeader = JSONObject.parseObject(infimumRecordHeaderStr, RecordHeader::class.java)
        val infimum = InfimumFactory().create()
        infimum.recordHeader = infimumRecordHeader
        page.infimum = infimum
        val supremumRecordHeader = JSONObject.parseObject(
            "{\"NOwned\":2,\"delete\":false,\"heapNo\":1,\"minRec\":false,\"nextRecordOffset\":0,\"recordType\":3,\"source\":[2,0,11,0,0]}",
            RecordHeader::class.java
        )
        val supremum = SupremumFactory().create()
        supremum.recordHeader = supremumRecordHeader
        page.supremum = supremum
        val userRecords = JSONObject.parseObject(
            "{\"source\":[0,0,24,0,107,-1,-1,-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,122,104,97,110,103,115,97,110,-25,-108,-73,0,0,0,18,97,98,99,100]}",
            UserRecords::class.java
        )
        page.userRecords = userRecords
        val pageDir = JSONObject.parseObject("{\"slots\":[94,107]}", PageDirectory::class.java)
        page.pageDirectory = pageDir
        page.freeSpace = 16200
        val fileTrailer = JSONObject.parseObject("{\"checkSum\":0,\"lsn\":0}", FileTrailer::class.java)
        page.fileTrailer = fileTrailer
        return page
    }
}
