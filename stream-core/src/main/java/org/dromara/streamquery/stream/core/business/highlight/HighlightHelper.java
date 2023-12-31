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
package org.dromara.streamquery.stream.core.business.highlight;

import org.dromara.streamquery.stream.core.optional.Opp;
import org.dromara.streamquery.stream.core.stream.Steam;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * 高亮封装
 *
 * @author VampireAchao Cizai_
 */
public class HighlightHelper {

  private HighlightHelper() {
    /* Do not new me! */
  }

  /**
   * highlight.
   *
   * @param text a {@link java.lang.String} object
   * @param foundWords a {@link java.util.List} object
   * @param highlightOperator a {@link java.util.function.UnaryOperator} object
   * @return a {@link java.lang.String} object
   */
  public static String highlight(
      String text, List<FoundWord> foundWords, UnaryOperator<String> highlightOperator) {
    if (Opp.ofColl(foundWords).isEmpty() || Opp.ofStr(text).isEmpty()) {
      return text;
    }
    // 对区间进行左端点的排序
    List<int[]> sectionList =
        Steam.of(foundWords)
            .sorted(Comparator.comparing(FoundWord::getIndex))
            .map(foundWord -> new int[] {foundWord.getIndex(), foundWord.getEndIndex()})
            .toList();
    // 合并区间
    Deque<int[]> mergeDeque = new ArrayDeque<>();
    for (int[] section : sectionList) {
      // 如果区间没有交集也没有相邻则直接加入队尾
      if (mergeDeque.isEmpty() || mergeDeque.peekLast()[1] < section[0] - 1) {
        mergeDeque.offerLast(section);
      } else {
        mergeDeque.peekLast()[1] = Math.max(mergeDeque.peekLast()[1], section[1]);
      }
    }
    // 添加边界区间
    mergeDeque.offerFirst(new int[] {-1, -1});
    mergeDeque.offerLast(new int[] {text.length(), text.length() - 1});
    List<FoundWord> list = new ArrayList<>();
    int[] prev = Opp.of(mergeDeque.pollFirst()).get();
    while (!mergeDeque.isEmpty()) {
      int[] cur = mergeDeque.pollFirst();
      String gapWord = text.substring(prev[1] + 1, cur[0]);
      String curWord = text.substring(cur[0], cur[1] + 1);
      // 如果当前区间和上一个区间之间有字符则需要将这之间的字符加入到结果集中
      if (!Opp.ofStr(gapWord).isEmpty()) {
        list.add(new FoundWord(gapWord, prev[1] + 1));
      }
      // 如果当前区间的长度大于0则将做高亮处理后的结果加入到结果集中
      if (!Opp.ofStr(curWord).isEmpty()) {
        list.add(new FoundWord(curWord, highlightOperator.apply(curWord), cur[0]));
      }
      prev = cur;
    }
    return Steam.of(list).map(FoundWord::getWordAfterHighlight).join();
  }

  /**
   * 将给定的字符串text中的给定单词words进行高亮显示
   *
   * @param text 需要高亮封装的字符串
   * @param highlightOperator 高亮转换函数
   * @param words 需要高亮显示的单词
   * @return 高亮封装后的字符串
   */
  public static String highlight(
      String text, UnaryOperator<String> highlightOperator, Collection<String> words) {
    if (Opp.ofColl(words).isEmpty() || Opp.ofStr(text).isEmpty()) {
      return text;
    }
    List<FoundWord> foundWords = new ArrayList<>();
    for (String word : words) {
      int index = 0;
      while ((index = text.indexOf(word, index)) != -1) {
        foundWords.add(new FoundWord(word, index));
        index += word.length();
      }
    }
    return highlight(text, foundWords, highlightOperator);
  }
}
