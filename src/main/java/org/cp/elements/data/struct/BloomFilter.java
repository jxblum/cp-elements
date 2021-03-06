/*
 * Copyright 2011-Present Author or Authors.
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

package org.cp.elements.data.struct;

import org.cp.elements.lang.Filter;

/**
 * The {@link BloomFilter} interface defines a contract for a probabilistic data structure
 * testing whether a data element is a member of a set.
 *
 * @author John Blum
 * @param <T> {@link Class type} of the elements contained by this {@link Filter}.
 * @see org.cp.elements.lang.Filter
 * @see <a href="https://en.wikipedia.org/wiki/Bloom_filter">Bloom Filter</a>
 * @since 1.0.0
 */
public interface BloomFilter<T> extends Filter<T> {

  /**
   * Adds the given element to the set of elements tracked by this {@link Filter}.
   *
   * @param element the element to add to the set of elements managed by this {@link Filter}.
   * @see #accept(Object)
   */
  void add(T element);

  /**
   * Returns the approximate, estimated number of data elements managed by this {@link BloomFilter}.
   *
   * @return the approximate, estimated number of data elements managed by this {@link BloomFilter}.
   */
  int size();

}
