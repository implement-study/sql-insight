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

package org.gongxuanzhang.mysql.storage.innodb;

import org.gongxuanzhang.mysql.core.InnoDbPageSelector;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.SelectRow;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.entity.page.InnoDbPageFactory;
import org.gongxuanzhang.mysql.entity.page.InnodbPageInfoVisitor;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.SelectEngine;

import java.util.List;

/**
 * innodb 的 查询引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnoDbSelect implements SelectEngine {


    @Override
    public Result select(SingleSelectInfo info) throws MySQLException {
        InnoDbPageSelector selector = InnoDbPageSelector.open(info);
        byte[] rootPageBuffer = selector.getRootPage();
        InnoDbPageFactory factory = InnoDbPageFactory.getInstance();
        InnoDbPage rootPage = factory.swap(rootPageBuffer);
        InnodbPageInfoVisitor rootInfo = new InnodbPageInfoVisitor(rootPage);
        if (rootInfo.isDataPage()) {
            return singlePage(rootInfo, info);
        }
        throw new UnsupportedOperationException("这是目录页，需要分裂了");
    }

    private Result singlePage(InnodbPageInfoVisitor pageInfoVisitor, SingleSelectInfo info) {
        List<SelectRow> selectRows = pageInfoVisitor.showRows();
        for (SelectRow selectRow : selectRows) {
            System.out.println(selectRow.showMap());
        }
        return Result.success();

    }
}
