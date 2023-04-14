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
package org.dromara.streamquery.stream.plugin.mybatisplus.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserInfo
 *
 * @author VampireAchao Cizai_
 * @since 2022/5/21
 */
@Data
public class UserInfo implements ParentScan {

  private static final long serialVersionUID = -7219188882388819210L;

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  private String name;
  private Integer age;
  private String email;
  @Version
  private Integer version;

  @TableLogic(value = "'2001-01-01 00:00:00'", delval = "NOW()")
  private LocalDateTime gmtDeleted;
}
