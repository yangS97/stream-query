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
package org.dromara.streamquery.stream.core.stream;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.dromara.streamquery.stream.core.collection.Maps;
import org.dromara.streamquery.stream.core.optional.Opp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * @author VampireAchao Cizai_
 * @since 2022/7/19 14:14
 */
class SteamTest {

  private static final List<Author> authors = getAuthors();
  private static final Stream<Author> as = getAuthors().stream();


  @Test
  void testBuilder() {
    List<Integer> list = Steam.<Integer>builder().add(1).add(2).add(3).build().toList();
    Assertions.assertEquals(asList(1, 2, 3), list);
  }

  @Test
  void testOf() {
    //Steam.of()实际调用
    //return Opp.of(iterable)
    //        .map(Iterable::spliterator)
    //        .map(spliterator -> StreamSupport.stream(spliterator, parallel))
    //        .map(Steam::new)
    //        .orElseGet(Steam::empty);
    Assertions.assertEquals(3, Steam.of(asList(1, 2, 3), true).count());
    Assertions.assertEquals(3, Steam.of(1, 2, 3).count());
    Assertions.assertEquals(3, Steam.of(Stream.builder().add(1).add(2).add(3).build()).count());
    Assertions.assertEquals(0, Steam.of(Maps.of()).count());


    System.out.println("Steam.of(authors).count() = " + Steam.of(authors).count());

    Author author = Steam.of(authors).findLast(au -> au.getAge() > 14).orElse(new Author());
    System.out.println("author = " + author);
    System.out.println("==================================");
    Map<Integer, Author> zip = Steam.of(authors)
            .map(Author::getAge)
            .toZip(authors);
    System.out.println("zip = " + zip);

    Map<Integer, Author> zip02 = Steam.of(authors).toMap(au -> au.getAge(), Function.identity());
    System.out.println("zip02 = " + zip02);

  }

  @Test
  void testSplit() {
    //split()方法,即使入参是null,也不会报错,会返回一个空的stream对象
    List<String> list1 = Steam.split(null, ",").toList();
    System.out.println("list1 = " + list1);


    List<Integer> list = Steam.split("1,2,3", ",").map(Integer::valueOf).toList();
    Assertions.assertEquals(asList(1, 2, 3), list);
  }

  @Test
  void testIterator() {
    List<Integer> list = Steam.iterate(0, i -> i < 3, i -> ++i).toList();
    Assertions.assertEquals(asList(0, 1, 2), list);
  }

  @Test
  void testToCollection() {
    //Iterable::spliterator 是 Java 8 中的一个方法，它返回一个 Spliterator 对象，
    // 用于遍历 Iterable 实例中的元素。Spliterator 是一个可分割迭代器，可以将其分割成多个部分并在多个线程中并行处理。
    // 这个方法的作用是将 Iterable 实例转换为 Spliterator 实例，以便可以使用 Spliterator 的方法对其进行操作。
    // 例如，可以使用 tryAdvance 方法遍历 Spliterator 实例中的元素，或者使用 forEachRemaining 方法对其进行消费。
    List<Integer> list02 = asList(1, 2, 3);
    Steam<Integer> get = Opp.of(list02)
            .map(Iterable::spliterator)
            .map(spliterator -> StreamSupport.stream(spliterator, false))  //将一个 Spliterator 对象映射为一个 Stream 对象
            .map(Steam::new)
            .orElse(Steam.of(1));



    List<Integer> list = asList(1, 2, 3);
    List<String> toCollection = Steam.of(list).map(String::valueOf).toCollection(LinkedList::new);
    List<String> toCollection2 = Steam.of(list).map(String::valueOf).toCollection(ArrayList::new);
    Assertions.assertEquals(asList("1", "2", "3"), toCollection2);


    HashSet<Author> collection = Steam.of(authors).toCollection(HashSet::new);
    System.out.println("collection = " + collection);
  }

  @Test
  void testToList() {
    List<Integer> list = asList(1, 2, 3);
    //return Opp.of(iterable)
    //        .map(Iterable::spliterator)
    //        .map(spliterator -> StreamSupport.stream(spliterator, parallel))
    //        .map(Steam::new)
    //        .orElseGet(Steam::empty);
    List<String> toList = Steam.of(list).map(String::valueOf).toList();
    Assertions.assertEquals(asList("1", "2", "3"), toList);
  }

  /**
   * 转换成不可修改的List
   */
  @Test
  void testToUnmodifiableList() {
    List<Integer> list = Steam.of(1, 2, 3).toUnmodifiableList();
    list.remove(0);
    Assertions.assertThrows(UnsupportedOperationException.class, () -> list.remove(0));
  }

  @Test
  void testToSet() {
    List<Integer> list = asList(1, 2, 3);
    Set<String> toSet = Steam.of(list).map(String::valueOf).toSet();
    Assertions.assertEquals(new HashSet<>(asList("1", "2", "3")), toSet);
  }

  @Test
  void testToUnmodifiableSet() {
    Set<Integer> set = Steam.of(1, 2, 3).toUnmodifiableSet();
    Assertions.assertThrows(UnsupportedOperationException.class, () -> set.remove(0));
  }

  @Test
  void testToZip() {
    List<Integer> orders = asList(1, 2, 3);
    List<String> list = asList("dromara", "hutool", "sweet");
    //如果是串行流, 实际是在调用toMap()方法
    Map<Integer, String> toZip = Steam.of(orders).toZip(list);
    Assertions.assertEquals(
        new HashMap<Integer, String>() {
          {
            put(1, "dromara");
            put(2, "hutool");
            put(3, "sweet");
          }
        },
        toZip);
  }

  /**
   * 拼接字符串, 方便添加符号,调整成我们想要的样子
   * 例如:  asList(1, 2, 3) -->   (1,2,3)
   */
  @Test
  void testJoin() {
    List<Integer> list = asList(1, 2, 3);
    String joining = Steam.of(list).join();
    Assertions.assertEquals("123", joining);
    Assertions.assertEquals("1,2,3", Steam.of(list).join(","));
    Assertions.assertEquals("(1,2,3)", Steam.of(list).join(",", "(", ")"));
  }

  /**
   * 集合转Map,类似于Collection的toMap()方法,但是对其底层进行了优化，
   * 防止了重复key的异常和当value为null时的NPE异常
   * 原生调用的弊端:   key重复 --> IllegalStateException: Duplicate key
   *                 value为null -->  NPE异常
   */
  @Test
  void testToMap() {
    List<Integer> list = asList(1, 2, 3);
    Map<Integer, String> map = Steam.of(list).toMap(Function.identity(), String::valueOf);
    System.out.println("map = " + map);

    //Function入参: t,k    返回出参 k,t    入参1,2,3  返回map<> "1" : 1
    Map<String, Integer> identityMap = Steam.of(list).toMap(String::valueOf);
    System.out.println("identityMap = " + identityMap);
    Assertions.assertEquals(
        new HashMap<String, Integer>() {
          {
            put("1", 1);
            put("2", 2);
            put("3", 3);
          }
        },
        identityMap);



    List<Integer> newList = asList(1, 3, 2); //Function入参: t,k    返回出参 k,t    入参1,2,3    返回map<>
    //toMap()方法里面有BinaryOperator, 会自动给升序排序 !!!
    Map<String, Integer> bothMap = Steam.of(newList).toMap(String::valueOf);
    Assertions.assertEquals(
            new HashMap<String, Integer>() {
              {
                put("1", 1);
                put("2", 2);
                put("3", 3);
              }
            },
            bothMap);
  }

  @Test
  void testToUnmodifiableMap() {
    Map<Integer, Integer> map1 =
        Steam.of(1, 2, 3).toUnmodifiableMap(Function.identity(), Function.identity());
    Assertions.assertThrows(UnsupportedOperationException.class, () -> map1.remove(1));
    Map<Integer, Integer> map2 =
        Steam.of(1, 2, 3)
            .toUnmodifiableMap(Function.identity(), Function.identity(), (t1, t2) -> t1);
    Assertions.assertThrows(UnsupportedOperationException.class, () -> map2.remove(1));
  }

  /**
   * 通过给定分组依据进行分组
   * 分组也挺好用, 比如查询carDeductionRecord, 然后根据etcTypeId进行分组
   */
  @Test
  void testGroup() {
    List<Integer> list = asList(1, 2, 3);
    Map<String, List<Integer>> group = Steam.of(list).group(String::valueOf);
    Assertions.assertEquals(
        new HashMap<String, List<Integer>>() {
          {
            put("1", singletonList(1));
            put("2", singletonList(2));
            put("3", singletonList(3));
          }
        },
        group);

    Map<Integer, List<Author>> ageGroup = Steam.of(authors).group(Author::getAge);
    System.out.println("ageGroup = " + ageGroup);

  }

  /**
   * 根据给定判断条件分组:  分成true,false两组     可以筛选出来符合条件的, 和所有不符合条件的
   * group()是根据分组条件分组
   */
  @Test
  void testPartition() {
    // 是否为奇数
    Predicate<Integer> predicate = t -> (t & 1) == 1;

    Map<Boolean, List<Integer>> map =
        Steam.of(1, 1, 2, 3).partition(predicate, Collectors.toList());
    Assertions.assertEquals(3, map.get(Boolean.TRUE).size());
    Assertions.assertEquals(1, map.get(Boolean.FALSE).size());

    map = Steam.of(1, 1, 2, 3).partition(predicate);
    Assertions.assertEquals(3, map.get(Boolean.TRUE).size());
    Assertions.assertEquals(1, map.get(Boolean.FALSE).size());

    Map<Boolean, Set<Integer>> map2 = Steam.of(1, 1, 2, 3).partition(predicate, HashSet::new);
    Assertions.assertEquals(2, map2.get(Boolean.TRUE).size());
    Assertions.assertEquals(1, map2.get(Boolean.FALSE).size());


    Map<Boolean, List<Author>> partition = Steam.of(authors).partition(author -> author.getAge() > 32);
    System.out.println("partition = " + partition);
  }

  /**
   * 返回无限串行无序流 其中每一个元素都由给定的Supplier生成
   * 适用场景在一些生成常量流、随机元素等   应该是可以用于测试吧
   */
  @Test
  void testGenerate() {
    Random random = new SecureRandom();
    Steam<Integer> limit = Steam.generate(() -> random.nextInt(10)).limit(10);
    limit.forEach(System.out::println);
    //Assertions.assertEquals(Boolean.TRUE, limit.allMatch(v -> v >= 0 && v < 10));

    Random random1 = new Random();
    Steam.generate(()-> random1.nextInt(100)).limit(10).forEach(System.out::println);
  }

  /**
   * filterContains : 过滤同类型集合中某一操作相同值的数据 filter(Function, Object)
   * filterContains(Student::getAge, others)
   * Student::getAge  -->  对同类型数据的条件限定
   * others  -->  同类型数据
   *
   * 最后stream过滤出来满足条件的数据!
   */
  @Test
  void testFilterIter() {
    List<Student> list =
        asList(
            Student.builder().name("臧臧").age(23).build(),
            Student.builder().name("阿超").age(21).build());
    List<Student> others =
        asList(Student.builder().age(22).build(), Student.builder().age(23).build());
    List<Student> students = Steam.of(list).filterContains(Student::getAge, others).toList();
    Assertions.assertEquals(singletonList(Student.builder().name("臧臧").age(23).build()), students);
  }

  @Data
  @Builder
  private static class Student {
    private String name;
    private Integer age;
    private Long id;

    @Tolerate
    public Student() {
      // this is an accessible parameterless constructor.
    }
  }

  /**
   * 返回与指定函数将元素作为参数执行的结果组成的流，操作带下标:  可以用来微调数据
   * 形参:  mapper – 指定的函数
   * 返回值: 返回叠加操作后的流
   */
  @Test
  void testMapIdx() {
    List<String> list = asList("dromara", "hutool", "sweet");
    List<String> mapIndex = Steam.of(list).parallel().mapIdx((e, i) -> i + 1 + "." + e).toList();
    Assertions.assertEquals(asList("1.dromara", "2.hutool", "3.sweet"), mapIndex);
  }

  /**
   * 扩散流操作，可能影响流元素个数，将原有流元素执行mapper操作，返回多个流所有元素组成的流，  ??
   * 操作带一个方法，调用该方法可增加元素 这是一个无状态中间操作
   */
  @Test
  void testMapMulti() {
    List<Integer> list = asList(1, 2, 3);
    List<Integer> mapMulti =
        Steam.of(list)
            .<Integer>mapMulti(
                (e, buffer) -> {
                  if (e % 2 == 0) {
                    buffer.accept(e);
                  }
                  buffer.accept(e);
                })
            .toList();
    Assertions.assertEquals(asList(1, 2, 2, 3), mapMulti);
  }

  /**
   * 返回一个具有去重特征的流 非并行流(顺序流)下对于重复元素，  串行流就是顺序流吗?
   * 保留遇到顺序中最先出现的元素，并行流情况下不能保证具体保留哪一个 这是一个有状态中间操作
   */
  @Test
  void testDistinct() {
    List<Integer> list = asList(1, 2, 2, 3);
    List<Integer> distinctBy = Steam.of(list).distinct(String::valueOf).toList();
    Assertions.assertEquals(asList(1, 2, 3), distinctBy);
  }

  /**
   * 对流里面的每一个元素执行一个操作，操作带下标，并行流时下标永远为-1 这是一个终端操作
   *
   * 这个貌似很有用嘛  可以对流里面每一个元素进行处理?
   */
  @Test
  void testForeachIdx() {
    List<String> list = asList("dromara", "hutool", "sweet");
    Steam.Builder<String> builder = Steam.builder();
    Steam.of(list).parallel().forEachIdx((e, i) -> builder.accept(i + 1 + "." + e));
    Assertions.assertEquals(asList("1.dromara", "2.hutool", "3.sweet"), builder.build().toList());
  }

  /**
   * 对流里面的每一个元素按照顺序执行一个操作，操作带下标，并行流时下标永远为-1 这是一个终端操作
   *
   * 跟forEachIdx的区别是   forEachIdx: 无序处理,    forEachOrderedIdx : 有序处理
   */
  @Test
  void testForEachOrderedIdx() {
    List<String> list = asList("dromara", "hutool", "sweet");
    Steam.Builder<String> builder = Steam.builder();
    Steam.of(list).forEachOrderedIdx((e, i) -> builder.accept(i + 1 + "." + e));
    Assertions.assertEquals(asList("1.dromara", "2.hutool", "3.sweet"), builder.build().toList());
  }

  /**
   * 扩散流操作，可能影响流元素个数，将原有流元素执行mapper操作，
   * 返回多个流所有元素组成的流，操作带下标，并行流时下标永远为-1 这是一个无状态中间操作   ???
   */
  @Test
  void testFlatIdx() {
    List<String> list = asList("dromara", "hutool", "sweet");
    List<String> mapIndex =
        Steam.of(list).parallel().flatIdx((e, i) -> Steam.of(i + 1 + "." + e)).toList();
    Assertions.assertEquals(asList("1.dromara", "2.hutool", "3.sweet"), mapIndex);
  }

  /**
   * 扩散流操作，可能影响流元素个数，将原有流元素执行mapper操作，返回多个流所有元素组成的流 这是一个无状态中间操作
   * 例如，将users里所有user的id和parentId组合在一起，形成一个新的流:
   *  Steam<Long> ids = Steam.of(users).flatMap(user -> Steam.of(user.getId(), user.getParentId()));
   */
  @Test
  void testFlatIter() {
    List<Integer> list = asList(1, 2, 3);
    // flat实际调用:  return flatMap(w -> Opp.of(w).map(mapper).map(Steam::of).orElseGet(Steam::empty));
    List<Integer> flatMapIter = Steam.of(list).<Integer>flat(e -> null).toList();
    Assertions.assertEquals(Collections.emptyList(), flatMapIter);
  }

  @Test
  void testFilter() {
    List<Integer> list = asList(1, 2, 3);
    //实际调用: return filter(e -> Objects.equals(Opp.of(e).map(mapper).get(), value));
    //写法更优雅些?  更灵活些吧
    List<Integer> filterIndex = Steam.of(list).filter(String::valueOf, "1").toList();
    Assertions.assertEquals(singletonList(1), filterIndex);

    List<Integer> list1 = Steam.of(list).filter(s -> s.toString().equals("1")).toList();
    System.out.println("list1 = " + list1);

    List<Integer> list2 = Steam.of(list).filter(String::valueOf, "1").toList();
  }

  /**
   * 过滤元素，返回与指定断言匹配的元素组成的流，断言带下标
   *
   * 提供更灵活的使用吧
   */
  @Test
  void testFilterIdx() {
    List<String> list = asList("dromara", "hutool", "sweet");
    List<String> filterIndex = Steam.of(list, true).filterIdx((e, i) -> i < 2).toList();
    Assertions.assertEquals(asList("dromara", "hutool"), filterIndex);
  }

  /**
   * 过滤掉为null的元素
   * 挺实用
   */
  @Test
  void testNonNull() {
    List<Integer> list = asList(1, null, 2, 3);
    List<Integer> nonNull = Steam.of(list).nonNull().toList();
    Assertions.assertEquals(asList(1, 2, 3), nonNull);
    System.out.println("nonNull = " + nonNull);
  }

  /**
   * nonNull  这个小方法还是挺实用的
   * 过滤指定条件为空的流中元素
   */
  @Test
  void testNonNullMapping() {
    List<Student> original =
        asList(
            Student.builder().name("臧臧").age(23).build(),
            Student.builder().name("阿超").age(21).build());
    List<Student> nonNull =
        Steam.of(original)
            .push(Student.builder() /*.name(null)*/.age(21).build())   //与给定元素组成的流合并成新的流, 在末尾
            .unshift(Student.builder().name("阿郎") /*.age(null)*/.build())  //与给定元素组成的流合并成新的流,在头部
            .nonNull(Student::getName)  //nonNull  方法可以带参数, 指定过滤对应条件的
            .nonNull(Student::getAge)
            .toList();
    Assertions.assertEquals(original, nonNull);
    System.out.println("nonNull = " + nonNull);
  }

  /**
   * 给定元素组成的流与当前流合并, 并置于尾部，成为新的流
   * eg : 123.unshift 23 --> 12323
   *
   *
   * return Steam.concat(this.stream, Stream.of(obj));
   */
  @Test
  void testPush() {
    List<Integer> list = asList(1, 2);
    //return Steam.concat(this.stream, Stream.of(obj));
    List<Integer> push = Steam.of(list).push(3).toList();
    Assertions.assertEquals(asList(1, 2, 3), push);

    List<Integer> lastPust = Steam.of(push).push(2, 1).toList();
    Assertions.assertEquals(asList(1, 2, 3, 2, 1), lastPust);
  }

  @Test
  void testParallel() {
    Assertions.assertTrue(Steam.of(1, 2, 3).parallel(true).isParallel());
    Assertions.assertFalse(Steam.of(1, 2, 3).parallel(false).isParallel());
  }

  /**
   * 给定元素组成的流与当前流合并, 并置于头部，成为新的流
   * eg : 123.unshift 23  -->  23123
   *
   */
  @Test
  void testUnshift() {
    List<Integer> list = asList(2, 3);
    List<Integer> unshift = Steam.of(list).unshift(1).toList();
    Assertions.assertEquals(asList(1, 2, 3), unshift);

    List<Integer> lastUnshift = Steam.of(unshift).unshift(3, 2).toList();
    Assertions.assertEquals(asList(3, 2, 1, 2, 3), lastUnshift);
  }

  /**
   * at :  获取流中指定下标的元素，如果是负数，则从最后一个开始数起
   *
   * 实用小工具 ?
   */
  @Test
  void testAt() {
    List<Integer> list = asList(1, 2, 3);
    Assertions.assertEquals(1, Steam.of(list).at(0).orElse(null));
    Assertions.assertEquals(2, Steam.of(list).at(1).orElse(null));
    Assertions.assertEquals(1, Steam.of(list).at(-3).orElse(null));
    Assertions.assertEquals(3, Steam.of(list).at(-1).orElse(null));
    Assertions.assertFalse(Steam.of(list).at(-4).isPresent());
  }

  /**
   *  对流进行操作, 添加或者删除元素
   */
  @Test
  void testSplice() {
    List<Integer> list = asList(1, 2, 3);
    Assertions.assertEquals(asList(1, 2, 2, 3), Steam.of(list).splice(1, 0, 2).toList());
    Assertions.assertEquals(asList(1, 2, 3, 3), Steam.of(list).splice(3, 1, 3).toList());
    Assertions.assertEquals(asList(1, 2, 4), Steam.of(list).splice(2, 1, 4).toList());
    Assertions.assertEquals(asList(1, 2), Steam.of(list).splice(2, 1).toList());
    Assertions.assertEquals(asList(1, 2, 3), Steam.of(list).splice(2, 0).toList());
    Assertions.assertEquals(asList(1, 2), Steam.of(list).splice(-1, 1).toList());
    Assertions.assertEquals(asList(1, 2, 3), Steam.of(list).splice(-2, 2, 2, 3).toList());
  }

  /**
   * 获取与给定断言匹配的第一个元素,带断言函数入参!
   * 功能更强大, 更灵活
   */
  @Test
  void testFindFirst() {
    List<Integer> list = asList(1, 2, 3);
    Optional<Integer> find = Steam.of(list).findFirst(Objects::nonNull);
    Assertions.assertEquals(1, find.orElse(null));

    Integer i = Steam.of(list).findFirst(num -> num > 2).orElse(-1);
    System.out.println("i = " + i);
  }

  /**
   * 获取索引, 作用不大吧?
   */
  @Test
  void testFindFirstIdx() {
    List<Integer> list = asList(null, 2, 3);
    Integer idx = Steam.of(list).findFirstIdx(Objects::nonNull);
    Assertions.assertEquals(1, idx);
    Assertions.assertNotEquals(-1, Steam.of(list).parallel().findFirstIdx(Objects::nonNull));


    //获取年龄大于18的第一个作者的索引
    Integer firstIdx = Steam.of(authors).findFirstIdx(author -> author.getAge() > 18);
    System.out.println("firstIdx = " + firstIdx);
  }

  @Test
  void testFindLast() {
    List<Integer> list = asList(1, null, 3);
    Optional<Integer> find = Steam.of(list).findLast(Objects::nonNull);
    Assertions.assertEquals(3, find.orElse(null));
    Assertions.assertEquals(3, Steam.of(list).findLast().orElse(null));
  }

  @Test
  void testFindLastIdx() {
    List<Integer> list = asList(1, null, 3);
    //return this.mapIdx((e, i) -> Maps.entry(i, e))
    //        .filter(e -> predicate.test(e.getValue()))
    //        .findLast()
    //        .map(Map.Entry::getKey)
    //        .orElse(NOT_FOUND_INDEX);
    Integer idx = Steam.of(list).findLastIdx(Objects::nonNull);
    Assertions.assertEquals(2, idx);
    Assertions.assertNotEquals(-1, Steam.of(list).parallel().findLastIdx(Objects::nonNull));
  }

  /**
   * 反向自然排序,   1,3,2 --> 3,2,1
   */
  @Test
  void testReverse() {
    List<Integer> list = asList(1, 3, 2);
    List<Integer> reverse = Steam.of(list).reverseSorted(Comparator.naturalOrder()).toList();
    Assertions.assertEquals(asList(3, 2, 1), reverse);
  }

  /**
   * toZip是两个list流组合成一个map流返回
   * zip是两个list流组合成一个list流返回
   */
  @Test
  void testZip() {
    List<Integer> orders = asList(1, 2, 3);
    List<String> list = asList("dromara", "hutool", "sweet");
    List<String> zip = Steam.of(orders).zip(list, (e1, e2) -> e1 + "." + e2).toList();
    Assertions.assertEquals(asList("1.dromara", "2.hutool", "3.sweet"), zip);
  }

  /**
   * 按指定长度切分为几个流
   */
  @Test
  void testListSplit() {
    List<Integer> list = asList(1, 2, 3, 4, 5);
    List<List<Integer>> lists = Steam.of(list).split(2).map(Steam::toList).toList();
    Assertions.assertEquals(asList(asList(1, 2), asList(3, 4), singletonList(5)), lists);
  }

  /**
   * splitList 实际调用
   * return split(batchSize).map(Steam::toList);
   *
   * 等于对split方法的进一步封装   感觉实用性也不强
   */
  @Test
  void testSplitList() {
    List<Integer> list = asList(1, 2, 3, 4, 5);
    List<List<Integer>> lists = Steam.of(list).splitList(2).toList();
    Assertions.assertEquals(asList(asList(1, 2), asList(3, 4), singletonList(5)), lists);


    List<List<Author>> authorsList = Steam.of(authors).splitList(2).toList();
    System.out.println("authorsList = " + authorsList);

  }

  /**
   * log应该不怎么用的上吧   实用性不强
   */
  @Test
  void testLog() {
    List<Integer> list = asList(0, 1, 2);
    Assertions.assertEquals(asList(1, 2, 3), Steam.of(list).map(i -> i + 1).log().toList());

    List<Integer> logList = Steam.of(list).map(i -> i + 1).log().toList();
    System.out.println("logList = " + logList);
  }



  private static List<Author> getAuthors() {
    //数据初始化
    Author author = new Author(1L, "蒙多", 33, "一个从菜刀中明悟哲理的祖安人", null);
    Author author2 = new Author(2L, "亚拉索", 15, "狂风也追逐不上他的思考速度", null);
    Author author3 = new Author(3L, "易", 14, "是这个世界在限制他的思维", null);
    Author author4 = new Author(3L, "易", 14, "是这个世界在限制他的思维", null);

    //书籍列表
    List<Book> books1 = new ArrayList<>();
    List<Book> books2 = new ArrayList<>();
    List<Book> books3 = new ArrayList<>();

    books1.add(new Book(1L, "刀的两侧是光明与黑暗", "哲学,爱情", 88, "用一把刀划分了爱恨"));
    books1.add(new Book(2L, "一个人不能死在同一把刀下", "个人成长,爱情", 99, "讲述如何从失败中明悟真理"));

    books2.add(new Book(3L, "那风吹不到的地方", "哲学", 85, "带你用思维去领略世界的尽头"));
    books2.add(new Book(3L, "那风吹不到的地方", "哲学", 85, "带你用思维去领略世界的尽头"));
    books2.add(new Book(4L, "吹或不吹", "爱情,个人传记", 56, "一个哲学家的恋爱观注定很难把他所在的时代理解"));

    books3.add(new Book(5L, "你的剑就是我的剑", "爱情", 56, "无法想象一个武者能对他的伴侣这么的宽容"));
    books3.add(new Book(6L, "风与剑", "个人传记", 100, "两个哲学家灵魂和肉体的碰撞会激起怎么样的火花呢？"));
    books3.add(new Book(6L, "风与剑", "个人传记", 100, "两个哲学家灵魂和肉体的碰撞会激起怎么样的火花呢？"));

    author.setBooks(books1);
    author2.setBooks(books2);
    author3.setBooks(books3);
    author4.setBooks(books3);

    List<Author> authorList = new ArrayList<>(Arrays.asList(author, author2, author3, author4));
    return authorList;
  }

}
