package org.gongxuanzhang.mysql.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 没有功能的注解
 * 标记了此注解表示内容需要上下文，不能直接调用
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface DependOnContext {
}
