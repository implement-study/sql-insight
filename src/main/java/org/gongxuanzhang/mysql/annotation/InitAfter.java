package org.gongxuanzhang.mysql.annotation;


import org.gongxuanzhang.mysql.core.manager.MySQLManager;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 初始化在目标之后
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InitAfter {

    /**
     * 先初始化 after  在初始化自己
     **/
    Class<? extends MySQLManager<?>> value();

}
