/*
 * Copyright 2017-Present Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cp.elements.data.oql.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.cp.elements.data.oql.Oql;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.util.stream.StreamUtils;
import org.cp.elements.util.stream.Streamable;

/**
 * Abstract Data Type (ADT) modeling a collection of {@link Group Groups}.
 *
 * @author John Blum
 * @param <S> {@link Class type} of {@link Object} being grouped.
 * @see java.lang.Iterable
 * @see org.cp.elements.data.oql.support.Group
 * @see org.cp.elements.util.stream.Streamable
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public interface Groups<S> extends Iterable<Group<S, ?>>, Streamable<Group<S, ?>> {

  static <S> Groups<S> from(@NotNull Oql.GroupBy<S, ?> groupBy) {

    Assert.notNull(groupBy, "GroupBy is required");

    Grouping<S> grouping = groupBy.getGrouping();

    Map<Integer, Group<S, ?>> groups = new ConcurrentHashMap<>();

    return new Groups<>() {

      @Override
      public Grouping<S> getGrouping() {
        return grouping;
      }

      @Override
      public Group<S, ?> compute(S target) {
        return groups.computeIfAbsent(getGrouping().group(target), groupNumber -> Group.with(groupBy, groupNumber));
      }

      @Override
      @SuppressWarnings("all")
      public Iterator<Group<S, ?>> iterator() {
        return Collections.unmodifiableCollection(groups.values()).iterator();
      }
    };
  }

  /**
   * Gets the {@link Grouping} function used to determine the {@link Group} of an {@link Object}.
   *
   * @return the {@link Grouping} function used to determine the {@link Group} of an {@link Object}.
   * @see org.cp.elements.data.oql.support.Grouping
   */
  Grouping<S> getGrouping();

  /**
   * Computes the {@link Group} for the given {@link Object}.
   *
   * @param target {@link Object} to evaluate.
   * @return the {@link Group} for the given {@link Object}.
   * @see org.cp.elements.data.oql.support.Group
   */
  Group<S, ?> compute(S target);

  @Override
  default Stream<Group<S, ?>> stream() {
    return StreamUtils.stream(this);
  }
}
