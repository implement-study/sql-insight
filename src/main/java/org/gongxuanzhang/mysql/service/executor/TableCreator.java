package org.gongxuanzhang.mysql.service.executor;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.entity.ColumnInfo;
import org.gongxuanzhang.mysql.entity.ColumnType;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.tool.DbFactory;
import org.gongxuanzhang.mysql.tool.SqlUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 建表执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class TableCreator extends AbstractInfoExecutor<TableInfo> {

    public TableCreator(String sql) throws SqlParseException {
        super(sql);
    }

    public static void main(String[] args) {
        String ex = "(#a1)";
        ExpressionParser parser = new SpelExpressionParser();
        SpelExpression exp = (SpelExpression)parser.parseExpression(ex);
        EvaluationContext evaluationContext = exp.getEvaluationContext();
        evaluationContext.setVariable("a1",1);
        System.out.println(exp.getValue());
    }



    @Override
    public Result doExecute() {
        File gfrmFile;
        try {
            gfrmFile = DbFactory.getGfrmFile(this.getInfo());
            if (gfrmFile.exists()) {
                return Result.error("表" + this.getInfo().getTableName() + "已经存在");
            }
            if (!gfrmFile.createNewFile()) {
                return Result.error("创建表" + this.getInfo().getTableName() + "失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(gfrmFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(this.getInfo());
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            gfrmFile.delete();
            return Result.error(e.getMessage());
        }
    }

    @Override
    public TableInfo analysisInfo(String sql) throws SqlParseException {
        try {
            return createInfo(sql);
        } catch (SqlParseException e) {
            throw e;
        } catch (Exception e) {
            throw new SqlParseException("sql有问题");
        }
    }

    private TableInfo createInfo(String sql) throws SqlParseException {
        TableInfo tableInfo = new TableInfo();
        analysisTableName(sql, tableInfo);
        analysisProperties(sql, tableInfo);
        analysisColumns(sql, tableInfo);
        return tableInfo;
    }

    private void analysisColumns(String formatSql, TableInfo tableInfo) throws SqlParseException {
        String struct = formatSql.substring(formatSql.indexOf("(") + 1, formatSql.lastIndexOf(")"));
        String[] split = struct.split(",");
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        tableInfo.setPrimaryKey(new ArrayList<>());
        for (String columnStr : split) {
            if (!StringUtils.hasText(columnStr)) {
                throw new SqlParseException("表结构语法错误，查看'，'附近是否有问题");
            }
            columnInfoList.add(analysisColumnInfo(columnStr, tableInfo));
        }
        tableInfo.setColumnInfos(columnInfoList);
    }

    private ColumnInfo analysisColumnInfo(String columnStr, TableInfo tableInfo) throws SqlParseException {
        String[] keywords = columnStr.trim().split("\\s+");
        if (keywords.length < 2) {
            throw new SqlParseException(columnStr + "结构有问题");
        }
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setName(keywords[0]);
        for (ColumnType value : ColumnType.values()) {
            if (value.keyword.equals(keywords[1])) {
                columnInfo.setType(value);
            }
        }
        if (columnInfo.getType() == null) {
            throw new SqlParseException("类型[" + keywords[1] + "]无法解析");
        }
        for (int i = 2; i < keywords.length; i++) {
            String keyword = keywords[i];
            switch (keyword.toLowerCase()) {
                case "primary":
                    if (i == keyword.length() - 1 || !Objects.equals(keywords[i + 1], "key")) {
                        throw new SqlParseException(columnStr + "有问题");
                    }
                    tableInfo.getPrimaryKey().add(columnInfo.getName());
                    i++;
                    break;
                case "unique":
                    columnInfo.setUnique(true);
                    break;
                case "not":
                    if (i == keyword.length() - 1 || !Objects.equals(keywords[i + 1], "null")) {
                        throw new SqlParseException(columnStr + "有问题");
                    }
                    columnInfo.setNotNull(true);
                    i++;
                    break;
                case "auto_increment":
                    columnInfo.setAutoIncrement(true);
                    break;
                case "default":
                    if (i == keyword.length() - 1) {
                        throw new SqlParseException(columnStr + "有问题");
                    }
                    columnInfo.setDefaultValue(keywords[i + 1]);
                    i++;
                    break;
                case "comment":
                    if (i == keyword.length() - 1) {
                        throw new SqlParseException(columnStr + "有问题");
                    }
                    columnInfo.setComment(keywords[i + 1]);
                    i++;
                    break;
                default:
                    throw new SqlParseException("[" + keyword + "]无法解析");
            }
        }
        return columnInfo;
    }


    private void analysisTableName(String formatSql, TableInfo tableInfo) throws SqlParseException {
        int leftIndex = formatSql.indexOf("(");
        if (leftIndex == -1) {
            throw new SqlParseException("找不到表结构");
        }

        String tableName = formatSql.substring("create table ".length(), leftIndex).trim();
        if (!StringUtils.hasText(tableName)) {
            throw new SqlParseException("表名不能为空");
        }
        if (tableName.contains(".")) {
            String[] split = tableName.split("\\.");
            tableName = split[1];
            tableInfo.setDatabase(split[0]);
        }
        SqlUtils.checkVarName(tableName);
        tableInfo.setTableName(tableName);
    }

    private void analysisProperties(String formatSql, TableInfo tableInfo) throws SqlParseException {
        int rightIndex = formatSql.lastIndexOf(")");
        if (rightIndex == -1) {
            throw new SqlParseException("找不到表结构");
        }
        String suffix = formatSql.substring(rightIndex + 1).trim();
        StringBuilder suffixBuild = new StringBuilder();
        for (char c : suffix.toCharArray()) {
            if (c == '=') {
                suffixBuild.append(" ").append(c).append(" ");
            } else {
                suffixBuild.append(c);
            }
        }
        suffix = suffixBuild.toString();
        if (!StringUtils.hasText(suffix)) {
            return;
        }
        String[] split = suffix.split("\\s+");
        if (split.length % 3 != 0) {
            throw new SqlParseException(suffix + "有问题");
        }
        for (int i = 0; i < split.length / 3; i++) {
            String key = split[3 * i];
            String equal = split[3 * i + 1];
            String value = split[3 * i + 2];
            if (!Objects.equals(equal, "=")) {
                throw new SqlParseException(suffix + "有问题");
            }
            switch (key.toLowerCase()) {
                case "engine":
                    tableInfo.setEngine(value);
                    break;
                case "comment":
                    tableInfo.setComment(value);
                    break;
                default:
                    throw new SqlParseException(key + "不支持");
            }
        }
    }


}
