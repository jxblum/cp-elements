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

package org.cp.elements.util;

import static org.cp.elements.util.CollectionUtils.asIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cp.elements.lang.Assert;

/**
 * The DepthFirstIterator class is an Iterator implementation and facade for an Iterator of Iterators iterating elements
 * in column order, from top to bottom, left to right.
 *
 * @author John J. Blum
 * @see java.util.Iterator
 * @see org.cp.elements.util.BreadthFirstIterator
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class DepthFirstIterator<T> implements Iterator<T> {

  private final AtomicBoolean nextCalled = new AtomicBoolean(false);

  private final List<Iterator<T>> iteratorList = new ArrayList<>();

  private int currentIteratorIndex = 0;

  /**
   * Constructs an instance of the DepthFirstIterator class initialized with Iterator of Iterators to collectively
   * traverse the Iterators in depth-first order, or top to bottom, left to right.
   *
   * @param iterators the Iterator of Iterators to encapsulate in a depth-first traversal.
   * @throws java.lang.NullPointerException if the Iterator of Iterators references is null.
   * @see java.util.Iterator
   */
  public DepthFirstIterator(final Iterator<Iterator<T>> iterators) {
    Assert.notNull(iterators, "The Iterator of Iterators must not be null!");

    for (Iterator<T> iterator : asIterable(iterators)) {
      if (iterator != null) {
        iteratorList.add(iterator);
      }
    }
  }

  /**
   * Determines whether another element is available in the iteration.
   *
   * @return a boolean value indicating whether another element is available in the iteration.
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    while (!(iteratorList.isEmpty() || iteratorList.get(currentIteratorIndex).hasNext())) {
      Assert.isFalse(iteratorList.remove(currentIteratorIndex).hasNext(), "removing a non-empty Iterator");
      currentIteratorIndex = (iteratorList.isEmpty() ? 0 : (currentIteratorIndex % iteratorList.size()));
    }

    return !iteratorList.isEmpty();
  }

  /**
   * Gets the next element in the iteration.
   *
   * @return the next element in the iteration.
   * @throws java.util.NoSuchElementException if the iteration has exhausted all elements.
   * @see java.util.Iterator#next()
   * @see #hasNext()
   */
  @Override
  public T next() {
    Assert.isTrue(hasNext(), new NoSuchElementException("The iteration has no more elements!"));
    T nextValue = iteratorList.get(currentIteratorIndex).next();
    currentIteratorIndex = (++currentIteratorIndex % iteratorList.size());
    nextCalled.set(true);
    return nextValue;
  }

  /**
   * Removes the current element in the iteration.
   *
   * @throws java.lang.IllegalStateException if the next method has not yet been called, or the remove method
   * has already been called after the last call to the next method.
   * @see java.util.Iterator#remove()
   */
  @Override
  public void remove() {
    Assert.state(nextCalled.compareAndSet(true, false), "next was not called before remove");
    int iteratorIndexForRemove = (currentIteratorIndex - 1);
    iteratorIndexForRemove = (iteratorIndexForRemove < 0 ? iteratorList.size() - 1 : iteratorIndexForRemove);
    iteratorList.get(iteratorIndexForRemove).remove();
  }
}
