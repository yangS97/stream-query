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
package org.dromara.streamquery.stream.plugin.mybatisplus.engine.annotation;

import org.dromara.streamquery.stream.plugin.mybatisplus.engine.configuration.StreamConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启sql注入
 *
 * @author VampireAchao Cizai_
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@Import({StreamConfigurationSelector.class})
public @interface EnableMybatisPlusPlugin {

  /**
   * Alias for {@link #basePackages()}
   *
   * @return base packages
   */
  String[] value() default {};

  /**
   * Base packages
   *
   * @return base packages
   */
  String[] basePackages() default {};

  /**
   * Alias for {@link #basePackages()}. scan base package classes
   *
   * @return base package classes for scanning
   */
  Class<?>[] basePackageClasses() default {};

  /**
   * Specify classes
   *
   * @return classes
   */
  Class<?>[] classes() default {};

  /**
   * Base on {@link #basePackages()}. scan annotation classes
   *
   * @return annotation class for scanning
   */
  Class<? extends Annotation> annotation() default Annotation.class;

  /**
   * Base on {@link #basePackages()}. scan interface classes
   *
   * @return interface class for scanning
   */
  Class<?> interfaceClass() default Class.class;
}
