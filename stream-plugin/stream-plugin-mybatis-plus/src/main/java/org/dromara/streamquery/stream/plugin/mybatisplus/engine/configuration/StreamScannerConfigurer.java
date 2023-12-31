/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dromara.streamquery.stream.plugin.mybatisplus.engine.configuration;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * <pre>
 * stream scanner configurer
 * from {@link StreamScannerRegistrar}
 * </pre>
 *
 * @author KamToHung
 * @since 1.5.0
 */
public class StreamScannerConfigurer implements BeanFactoryPostProcessor {

  /** entity class list */
  private final Set<Class<?>> entityClassList = new HashSet<>();
  /** base package */
  private Set<String> basePackages;
  /** specify classes */
  private Set<Class<?>> classes;
  /** annotation */
  private Class<? extends Annotation> annotation;
  /** scan interface */
  private Class<?> interfaceClass;
  /** if basePackages is empty. */
  private boolean emptyBasePackages;

  public void setBasePackages(Set<String> basePackages) {
    this.basePackages = basePackages;
  }

  public void setClasses(Set<Class<?>> classes) {
    this.classes = classes;
  }

  public void setAnnotation(Class<? extends Annotation> annotation) {
    this.annotation = annotation;
  }

  public void setInterfaceClass(Class<?> interfaceClass) {
    this.interfaceClass = interfaceClass;
  }

  public void setEmptyBasePackages(boolean emptyBasePackages) {
    this.emptyBasePackages = emptyBasePackages;
  }

  private void registerEntityClasses(Collection<Class<?>> entityClasses) {
    if (!CollectionUtils.isEmpty(entityClasses)) {
      this.entityClassList.addAll(entityClasses);
    }
  }

  public Collection<Class<?>> getEntityClasses() {
    return entityClassList;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    defaultScanConfig();
    // 指定类
    registerEntityClasses(this.classes);
    StreamClassPathScanner scanner = new StreamClassPathScanner(false);
    scanner.setAnnotation(this.annotation);
    scanner.setInterfaceClass(this.interfaceClass);
    scanner.registerFilters();
    Set<Class<?>> classSet = scanner.scan(this.basePackages);
    registerEntityClasses(classSet);
  }

  private void defaultScanConfig() {
    // default scan @TableName
    if (emptyBasePackages && annotation == null && interfaceClass == null) {
      annotation = TableName.class;
    }
  }
}
