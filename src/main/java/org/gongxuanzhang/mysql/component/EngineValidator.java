
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
