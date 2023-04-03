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
package org.dromara.streamquery.stream.core.collection;

import org.dromara.streamquery.stream.core.stream.Steam;
import org.dromara.streamquery.stream.core.stream.collector.Collective;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @author VampireAchao
 * @since 2022/10/21 16:48
 */
class MapsTest {

    @Test
    void testOf() {
        Assertions.assertEquals("value", Maps.of("key", "value").get("key"));
        Assertions.assertEquals("value1", Maps.of("key", "value", "key1", "value1").get("key1"));
        Assertions.assertEquals("value2", Maps.of("key", "value", "key1", "value1", "key2", "value2").get("key2"));
    }

    @Test
    void testEntry() {
        final Map.Entry<String, String> entry = Maps.entry("key", "value");
        Assertions.assertEquals("key", entry.getKey());
        Assertions.assertEquals("value", entry.getValue());
    }

    @Test
    void testOneToManyToOne() {
        final Map<String, List<String>> map = Maps.oneToManyToOne(
                new HashMap<String, List<String>>() {{
                    put("key", Arrays.asList("value", null));
                }},
                new HashMap<String, String>() {{
                    put("value", "Good");
                }},
                Steam::nonNull).collect(Collective.entryToMap());
        Assertions.assertEquals(1, map.get("key").size());
        Assertions.assertEquals("Good", map.get("key").get(0));
    }

    @Test
    void testOneToOneToOne() {
        final Map<String, String> map = Maps.oneToOneToOne(
                new HashMap<String, String>() {{
                    put("key", "value");
                }},
                new HashMap<String, String>() {{
                    put("value", "Good");
                }},
                String::toUpperCase
        ).collect(Collective.entryToMap());
        Assertions.assertEquals("GOOD", map.get("key"));
        final Map<String, String> treeMap = Steam.of(Maps.entry("GOOD", ""), Maps.entry("BAD", ""))
                .collect(Collective.entryToMap(LinkedHashMap::new));
        Assertions.assertEquals("GOOD", Steam.of(treeMap.keySet()).findFirst().orElse(null));
    }

    @Test
    void testOneToOneToMany() {
        final Map<String, List<String>> map = Maps.oneToOneToMany(
                new HashMap<String, String>() {{
                    put("key", "value");
                }},
                new HashMap<String, List<String>>() {{
                    put("value", Arrays.asList("Good", "Bad"));
                }},
                Steam::nonNull
        ).collect(Collective.entryToMap());
        Assertions.assertEquals(2, map.get("key").size());
        Assertions.assertEquals("Good", map.get("key").get(0));
        Assertions.assertEquals("Bad", map.get("key").get(1));
    }
}
