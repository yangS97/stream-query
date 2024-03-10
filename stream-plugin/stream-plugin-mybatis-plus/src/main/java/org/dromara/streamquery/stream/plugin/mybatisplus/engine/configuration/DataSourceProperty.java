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

import javax.sql.DataSource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.dynamicdatasource.datasource.creator.DatasourceInitProperties;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.dynamicdatasource.datasource.creator.atomikos.AtomikosConfig;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.dynamicdatasource.datasource.creator.beecp.BeeCpConfig;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.dynamicdatasource.datasource.creator.dbcp.Dbcp2Config;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.dynamicdatasource.datasource.creator.druid.DruidConfig;
import org.dromara.streamquery.stream.plugin.mybatisplus.engine.dynamicdatasource.datasource.creator.hikaricp.HikariCpConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author TaoYu
 * @since 1.2.0
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperty {

  /** 连接池名称(只是一个名称标识)</br> 默认是配置文件上的名称 */
  private String poolName;
  /** 连接池类型，如果不设置自动查找 Druid > HikariCp */
  private Class<? extends DataSource> type;
  /** JDBC driver */
  private String driverClassName;
  /** JDBC url 地址 */
  private String url;
  /** JDBC 用户名 */
  private String username;
  /** JDBC 密码 */
  private String password;
  /** jndi数据源名称(设置即表示启用) */
  private String jndiName;
  /** 是否启用seata */
  private Boolean seata = true;
  /** 是否启用p6spy */
  private Boolean p6spy = true;
  /** lazy init datasource */
  private Boolean lazy;
  /** 初始化 */
  private DatasourceInitProperties init = new DatasourceInitProperties();
  /** Druid参数配置 */
  private DruidConfig druid = new DruidConfig();
  /** HikariCp参数配置 */
  private HikariCpConfig hikari = new HikariCpConfig();
  /** BeeCp参数配置 */
  private BeeCpConfig beecp = new BeeCpConfig();
  /** DBCP2参数配置 */
  private Dbcp2Config dbcp2 = new Dbcp2Config();
  /** atomikos参数配置 */
  private AtomikosConfig atomikos = new AtomikosConfig();

  /** 解密公匙(如果未设置默认使用全局的) */
  private String publicKey;
}
