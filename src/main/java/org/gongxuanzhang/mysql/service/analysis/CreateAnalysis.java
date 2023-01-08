package org.gongxuanzhang.mysql.service.analysis;

import org.gongxuanzhang.mysql.entity.ColumnInfo;
import org.gongxuanzhang.mysql.entity.ColumnType;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.executor.ddl.create.CreateDatabaseExecutor;
import org.gongxuanzhang.mysql.service.executor.ddl.create.CreateTableExecutor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;
import org.gongxuanzhang.mysql.service.token.TokenSupport;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.gongxuanzhang.mysql.service.token.TokenSupport.getMustString;
import static org.gongxuanzhang.mysql.service.token.TokenSupport.isTokenKind;
import static org.gongxuanzhang.mysql.service.token.TokenSupport.mustTokenKind;
import static org.gongxuanzhang.mysql.service.token.TokenSupport.varString;
import static org.gongxuanzhang.mysql.tool.ExceptionThrower.expectToken;
import static org.gongxuanzhang.mysql.tool.ExceptionThrower.ifNotThrow;
import static org.gongxuanzhang.mysql.tool.ExceptionThrower.throwSqlAnalysis;

/**
 * create 解析器
 * create table
 * create database
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CreateAnalysis implements TokenAnalysis {


    @Override
    public Executor analysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        if (sqlTokenList.size() < 3) {
            throw new SqlAnalysisException("无法解析");
        }
        int offset = 1;
        SqlToken sqlToken = sqlTokenList.get(offset);
        switch (sqlToken.getTokenKind()) {
            case TABLE:
                return createTable(sqlTokenList);
            case DATABASE:
                return createDataBase(sqlTokenList);
            default:
                throw new SqlAnalysisException("[create " + sqlToken.getValue() + "]有问题");
        }
    }

    private Executor createDataBase(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        if (sqlTokenList.size() < 3) {
            throw new SqlAnalysisException("无法解析database名称");
        } else if (sqlTokenList.size() > 3) {
            throwSqlAnalysis(sqlTokenList.get(3).getValue());
        }
        SqlToken nameToken = sqlTokenList.get(2);
        if (!isTokenKind(nameToken, TokenKind.VAR, TokenKind.LITERACY)) {
            throwSqlAnalysis(nameToken.getValue());
        }
        return new CreateDatabaseExecutor(nameToken.getValue());
    }

    private Executor createTable(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
        TableInfo process = new TableInfoAnalysis(sqlTokenList).process();
        return new CreateTableExecutor(process);
    }


    public static class TableInfoAnalysis {

        TableInfo info;

        private final List<SqlToken> sqlTokenList;

        private int offset;

        public TableInfoAnalysis(List<SqlToken> sqlTokenList) throws SqlAnalysisException {
            if (sqlTokenList.size() < 7) {
                throw new SqlAnalysisException("create table 解析失败");
            }
            this.sqlTokenList = sqlTokenList;
            this.offset = 2;
            this.info = new TableInfo();
        }

        public TableInfo process() throws SqlAnalysisException {
            analysisTableName();
            expectToken(this.sqlTokenList.get(offset), TokenKind.LEFT_PAREN);
            offset++;
            analysisCol();
            analysisExtra();
            if (CollectionUtils.isEmpty(this.info.getColumnInfos())) {
                throw new SqlAnalysisException("创建表应该至少有一列");
            }
            return this.info;
        }

        private void analysisExtra() throws SqlAnalysisException {
            if (end()) {
                return;
            }
            boolean isComment = isTokenKind(sqlTokenList.get(offset), TokenKind.COMMENT);
            ifNotThrow(isComment, sqlTokenList.get(offset).getValue() + "解析错误");
            boolean isEquals = isTokenKind(sqlTokenList.get(offset + 1), TokenKind.EQUALS);
            ifNotThrow(isEquals, sqlTokenList.get(offset + 1).getValue() + "解析错误");
            boolean legal = isTokenKind(sqlTokenList.get(offset + 2), TokenKind.LITERACY);
            ifNotThrow(legal, sqlTokenList.get(offset + 2).getValue() + "解析错误，可能是备注没有加单引号");
            this.info.setComment(getMustString(sqlTokenList.get(offset + 2)));
            offset += 3;
            if (!end()) {
                throwSqlAnalysis(this.sqlTokenList.get(offset).getValue());
            }
        }

        private void analysisCol() throws SqlAnalysisException {
            List<ColumnInfo> columnInfos = new ArrayList<>();
            this.info.setColumnInfos(columnInfos);
            while (!end() && !isTokenKind(sqlTokenList.get(offset), TokenKind.RIGHT_PAREN)) {
                ColumnInfo columnInfo = new ColumnInfo();
                String colName = varString(sqlTokenList.get(offset));
                columnInfo.setName(colName);
                offset++;
                columnInfo.setType(analysisType());
                fillExtra(columnInfo);
                columnInfos.add(columnInfo);
            }
            offset++;
        }

        private boolean end() {
            return this.offset >= this.sqlTokenList.size();
        }

        private void analysisTableName() throws SqlAnalysisException {
            SqlToken candidate = sqlTokenList.get(offset);
            mustTokenKind(candidate, TokenKind.VAR);
            if (!isTokenKind(sqlTokenList.get(offset + 1), TokenKind.DOT)) {
                this.info.setTableName(candidate.getValue());
                offset++;
                return;
            }
            if (!isTokenKind(sqlTokenList.get(offset + 2), TokenKind.VAR)) {
                throwSqlAnalysis(candidate.getValue() + ".");
            }
            this.info.setDatabase(candidate.getValue());
            this.info.setTableName(sqlTokenList.get(offset + 2).getValue());
            offset += 3;
        }

        private ColumnType analysisType() throws SqlAnalysisException {
            ColumnType result;
            switch (this.sqlTokenList.get(offset).getTokenKind()) {
                case INT:
                    result = ColumnType.INT;
                    break;
                case VARCHAR:
                    result = ColumnType.STRING;
                    break;
                case TIMESTAMP:
                    result = ColumnType.TIMESTAMP;
                    break;
                default:
                    throw new SqlAnalysisException("不支持的数据类型" + this.sqlTokenList.get(offset).getValue());
            }
            offset++;
            return result;
        }

        private void fillExtra(ColumnInfo columnInfo) throws SqlAnalysisException {
            while (true) {
                if (isTokenKind(this.sqlTokenList.get(offset), TokenKind.COMMA)) {
                    offset++;
                    break;
                }
                if (isTokenKind(this.sqlTokenList.get(offset), TokenKind.RIGHT_PAREN)) {
                    break;
                }
                SqlToken extraToken = this.sqlTokenList.get(offset);
                switch (extraToken.getTokenKind()) {
                    case PRIMARY:
                        expectToken(sqlTokenList.get(offset + 1), TokenKind.KEY);
                        offset++;
                        this.info.addPrimaryKey(columnInfo.getName());
                        break;
                    case AUTO_INCREMENT:
                        columnInfo.setAutoIncrement(true);
                        break;
                    case DEFAULT:
                        if (isTokenKind(sqlTokenList.get(offset + 1), TokenKind.NULL)) {
                            offset++;
                            break;
                        }
                        String defaultValue = TokenSupport.getMustString(this.sqlTokenList.get(offset + 1));
                        columnInfo.setDefaultValue(defaultValue);
                        offset++;
                        break;
                    case NOT:
                        if (isTokenKind(sqlTokenList.get(offset + 1), TokenKind.NULL)) {
                            columnInfo.setNotNull(true);
                            offset++;
                            break;
                        } else {
                            throw new SqlAnalysisException("not 无法解析");
                        }
                    case COMMENT:
                        String comment = TokenSupport.getMustString(this.sqlTokenList.get(offset + 1));
                        columnInfo.setComment(comment);
                        offset++;
                        break;
                    case UNIQUE:
                        columnInfo.setUnique(true);
                        break;
                    default:
                        throwSqlAnalysis(extraToken.getValue());
                }
                offset++;
            }
        }

    }
}
