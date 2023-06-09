/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.entity.page;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnodbPageInfoVisitor {

    private final InnoDbPage page;

    public InnodbPageInfoVisitor(InnoDbPage page){
        this.page = page;
    }


    /**
     * 是否是索引页
     **/
    public boolean isIndexPage() {
        return page.getFileHeader().getPageType() == PageType.FIL_PAGE_INODE.getValue();
    }

    /**
     * 是否是数据页
     **/
    public boolean isDataPage() {
        return page.getFileHeader().getPageType() == PageType.FIL_PAGE_INDEX.getValue();
    }


    public InnoDbPage getPage() {
        return page;
    }
}
