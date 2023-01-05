package org.gongxuanzhang.mysql.service.analysis;

import org.gongxuanzhang.mysql.exception.SqlAnalysisException;
import org.gongxuanzhang.mysql.service.analysis.ast.SqlAstNode;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.TokenKind;

import java.util.List;

/**
 * 语法分析
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlAnalysis {


    private int offset;

    private int length;

    private final List<SqlToken> tokenStream;


    public SqlAnalysis(List<SqlToken> tokenStream) {
        this.tokenStream = tokenStream;
        this.offset = 0;
        this.length = tokenStream.size();
    }

    /**
     * 语法分析
     **/
    public SqlAstNode analysis() throws SqlAnalysisException {
        // todo
            //  解析括号--> 解析sql ---> 解析头--->
        if (checkKind(TokenKind.LEFT_PAREN)) {
            offset++;
            SqlAstNode node = analysisNode();
        }
        return null;
    }

    private SqlAstNode analysisNode(){
        return null;
    }


    private boolean checkKind(TokenKind tokenKind) {
        return tokenStream.get(this.offset).getTokenKind() == tokenKind;
    }



    private boolean finish() {
        return this.offset >= this.length;
    }


}
