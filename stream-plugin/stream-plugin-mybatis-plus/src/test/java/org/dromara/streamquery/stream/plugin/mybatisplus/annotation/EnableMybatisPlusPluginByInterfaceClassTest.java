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
package org.dromara.streamquery.stream.plugin.mybatisplus.annotation;

import org.dromara.streamquery.stream.plugin.mybatisplus.annotation.pojo.po.RoleInfo;
import org.dromara.streamquery.stream.plugin.mybatisplus.annotation.pojo.po.UserInfo;
import org.dromara.streamquery.stream.plugin.mybatisplus.annotation.pojo.po.UserRole;
import org.dromara.streamquery.stream.plugin.mybatisplus.annotation.pojo.po.inner.AddressInfo;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.annotation.EnableMybatisPlusPlugin;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.configuration.StreamScannerConfigurer;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.mapper.IGenerateMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * EnableMybatisPlusPluginByInterfaceClassTest
 *
 * @author <a href = "kamtohung@gmail.com">KamTo Hung</a>
 */
@EnableAutoConfiguration
@EnableMybatisPlusPlugin(
    basePackages = "org.dromara.streamquery.stream.plugin.mybatisplus.annotation.pojo.po",
    interfaceClass = IGenerateMapper.class)
public class EnableMybatisPlusPluginByInterfaceClassTest
    extends AbstractMybatisPlusTestApplication {

  @Test
  void testScanByInterfaceClass() {
    StreamScannerConfigurer bean = context.getBean(StreamScannerConfigurer.class);
    assertNotNull(bean);
    assertNotNull(bean.getEntityClasses());
    assertFalse(bean.getEntityClasses().contains(RoleInfo.class));
    assertTrue(bean.getEntityClasses().contains(UserInfo.class));
    assertFalse(bean.getEntityClasses().contains(UserRole.class));
    assertFalse(bean.getEntityClasses().contains(AddressInfo.class));
    assertFalse(bean.getEntityClasses().contains(AddressInfo.InnerAddressInfo.class));
  }
}
