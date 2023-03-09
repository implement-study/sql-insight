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


import java.util.List;

/**
 *
 * InnoDb 页结构
 * 默认16K 暂时不支持修改
 *
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class InnoDbPage {

    /**
     * 文件头 38字节
     **/
    private FileHeader fileHeader;

    /**
     * 页头 56字节
     **/
    private PageHaader pageHaader;

    /**
     * 下确界，13字节
     **/
    private Infimum infimum;

    /**
     * 上确界，13字节
     **/
    private Supremum supremum;

    /**
     * 用户记录  不确定字节
     **/
    private List<UserRecord> userRecords;


    /**
     *  空闲空间
     **/
    private FreeSpace freeSpace;


    /**
     * 页目录
     **/
    private PageDirectory pageDirectory;

    /**
     * 文件尾 8字节
     **/
    private FileTrailer fileTrailer;



    public InnoDbPage(byte[] bytes){

    }




}
