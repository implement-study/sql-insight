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

package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.entity.page.InnoDbPageFactory;
import org.gongxuanzhang.mysql.entity.page.InnodbPageInfoVisitor;
import org.gongxuanzhang.mysql.exception.LambdaExceptionRuntimeWrapper;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.PageReader;
import org.gongxuanzhang.mysql.tool.PageUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * innodb页选择器，针对某一张表的选择器，每张表唯一实例
 * <p>
 * InnoDbPageSelector.open(); 获得实例
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnoDbPageSelector implements PageSelector, Refreshable {

    private final static Map<String, InnoDbPageSelector> INSTANCE_CACHE = new ConcurrentHashMap<>();

    private final File dataFile;

    private final InnoDbPageFactory innoDbPageFactory = InnoDbPageFactory.getInstance();

    private InnoDbPageSelector(TableInfo tableInfo) throws MySQLException {
        this.dataFile = tableInfo.dataFile();
    }


    public static InnoDbPageSelector open(TableInfoBox tableInfoBox) throws MySQLException {
        return open(tableInfoBox.getTableInfo());
    }

    /**
     * 获得一个 inno db selector
     *
     * @param tableInfo
     **/
    public static InnoDbPageSelector open(TableInfo tableInfo) throws MySQLException {
        String tableName = tableInfo.getTableName();
        try {
            return INSTANCE_CACHE.computeIfAbsent(tableName, k -> {
                try {
                    return new InnoDbPageSelector(tableInfo);
                } catch (MySQLException e) {
                    throw new LambdaExceptionRuntimeWrapper(e);
                }
            });
        } catch (LambdaExceptionRuntimeWrapper e) {
            e.wrapMySQLException();
        }
        throw new MySQLException("选择页出现错误");
    }

    @Override
    public byte[] getRootPage() throws MySQLException {
        byte[] rootPage = ConstantSize.PAGE.emptyBuff();
        int length = PageReader.read(this.dataFile, rootPage);
        if (length != rootPage.length) {
            throw new MySQLException("根页读取错误");
        }
        return rootPage;
    }

    @Override
    public byte[] getLastPage() throws MySQLException {
        byte[] rootPageBuffer = getRootPage();
        InnoDbPage rootPage = this.innoDbPageFactory.swap(rootPageBuffer);
        InnodbPageInfoVisitor rootInfo = new InnodbPageInfoVisitor(rootPage);
        if (rootInfo.isDataPage()) {
            return rootPageBuffer;
        }
        //  todo  这里如果是目录  需要继续找
        return null;

    }


    @Override
    public void addNewPage(byte[] newPage) throws MySQLException {
        InnoDbPage swap = this.innoDbPageFactory.swap(newPage);
        insertInnoDbPage(swap);
    }

    @Override
    public byte[] getNextPage(InnoDbPage page) throws MySQLException {
        int next = page.getFileHeader().getNext();
        if (next == 0) {
            return null;
        }
        byte[] pageBuffer = ConstantSize.PAGE.emptyBuff();
        PageReader.read(this.dataFile, pageBuffer, next);
        return pageBuffer;
    }


    private void insertInnoDbPage(InnoDbPage swap) {
        //  todo
        System.out.println("insert innodb page .. ");
    }

    /**
     * 刷新表示所有页调整
     * 同时写回
     **/
    @Override
    public void refresh() throws MySQLException {

    }

    /**
     * 根据一个数据查看目标页
     * 返回的页一定有足够的位置
     **/
    public InnoDbPage selectPage(InsertRow row) throws MySQLException {
        if (ConstantSize.INIT_PAGE_FREE_SPACE.getSize() < row.length()) {
            throw new MySQLException(String.format("暂不支持如此大的用户记录[%s]", row.length()));
        }
        byte[] rootPage = getRootPage();
        InnoDbPage root = innoDbPageFactory.swap(rootPage);
        InnodbPageInfoVisitor rootInfo = new InnodbPageInfoVisitor(root);
        if (rootInfo.isIndexPage()) {
            return findOrCreatePage(root, row);
        }
        if (!rootInfo.isDataPage()) {
            throw new MySQLException("页状态错误");
        }
        if (root.isEnough(row.length())) {
            return root;
        }
        PageUtils.rootDataPageToIndex(root);
        return selectPage(row);
    }



    private InnoDbPage findOrCreatePage(InnoDbPage root, InsertRow row) {

        System.out.println("");
        return null;
    }


}
