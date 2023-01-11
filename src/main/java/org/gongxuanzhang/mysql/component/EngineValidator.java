package org.gongxuanzhang.mysql.component;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.annotation.Engine;
import org.gongxuanzhang.mysql.storage.StorageEngine;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 引擎校验
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Component
@Slf4j
public class EngineValidator implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass;
        if (AopUtils.isAopProxy(bean)) {
            beanClass = AopUtils.getTargetClass(bean);
        } else {
            beanClass = bean.getClass();
        }
        if (beanClass.isAnnotationPresent(Engine.class) && !StorageEngine.class.isAssignableFrom(beanClass)) {
            String message = "beanName:%s,加了@Engine注解，但是没有实现StorageEngine接口，无法在引擎列表中实现";
            throw new RuntimeException(String.format(message, beanName));
        }
        return bean;
    }
}
