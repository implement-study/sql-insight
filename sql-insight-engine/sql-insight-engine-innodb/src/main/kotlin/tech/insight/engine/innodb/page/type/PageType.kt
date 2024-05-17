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
package tech.insight.engine.innodb.page.type

import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.page.InnodbUserRecord


/**
 * innodb page type,different page type has different action.
 * @author gxz gongxuanzhang@foxmail.com
 */
interface PageType : Comparator<InnodbUserRecord> {

    val value: Short

    val page: InnoDbPage


    /**
     * locate the page that have the record.
     * Maybe the user record does not exist. Return the page where record should be
     *
     *
     * @param userRecord user record
     */
    fun locatePage(userRecord: InnodbUserRecord): InnoDbPage

    /**
     * convert the record in the page to user record.
     * @param offsetInPage the record offset in page
     */
    fun convertUserRecord(offsetInPage: Int): InnodbUserRecord

    /**
     * get the first index node to parent node insert
     * link [InnoDbPage.pageIndex]
     */
    fun pageIndex(): InnodbUserRecord

    /**
     * if page is root page that to say page don't have parent page,split page will create two pages.
     *
     * @param leftPage
     * @param rightPage left and right page don't have offset
     */
    fun rootUpgrade(leftPage: InnoDbPage, rightPage: InnoDbPage)


    companion object {
        fun valueOf(value: Int, innoDbPage: InnoDbPage): PageType {
            return when (value.toShort()) {
                DataPage.FIL_PAGE_INDEX_VALUE -> DataPage(innoDbPage)
                IndexPage.FIL_PAGE_INODE -> IndexPage(innoDbPage)
                UndoPage.FIL_PAGE_TYPE_UNDO_LOG -> UndoPage(innoDbPage)
                else -> throw IllegalArgumentException("page type error value[ $value ]")
            }
        }
    }
}
