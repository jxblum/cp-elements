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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.Filter;
import org.cp.elements.lang.FilteringTransformer;
import org.cp.elements.lang.NumberUtils;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.Transformer;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Unit Tests for {@link ArrayUtils}.
 *
 * @author John J. Blum
 * @see java.lang.Iterable
 * @see java.lang.reflect.Array
 * @see java.util.Arrays
 * @see java.util.Enumeration
 * @see java.util.Iterator
 * @see java.util.List
 * @see org.junit.Rule
 * @see org.junit.Test
 * @see org.junit.rules.ExpectedException
 * @see org.cp.elements.util.ArrayUtils
 * @since 1.0.0
 */
public class ArrayUtilsTests {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @SafeVarargs
  private final <T> void assertElements(T[] array, T... elements) {

    assertThat(array, is(notNullValue()));
    assertThat(array.length, is(equalTo(elements.length)));

    int index = 0;

    for (T element : elements) {
      assertThat(array[index++], is(equalTo(element)));
    }
  }

  @SuppressWarnings("all")
  private <T> void assertShuffled(T[] source, T[] target) {

    assertTrue("'source' array cannot be null or empty", source != null && source.length != 0);
    assertTrue("'target' array cannot be null or empty", target != null && target.length != 0);
    assertThat("'source' and 'target' array lengths must match", source.length, is(equalTo(target.length)));

    boolean shuffled = false;

    for (int index = 0; index < source.length && !shuffled; index++) {
      shuffled |= !source[index].equals(target[index]);
    }

    assertTrue(String.format("target array [%1$s] was not shuffled", Arrays.toString(target)), shuffled);
  }

  private <T extends Comparable<T>> void assertSorted(T[] array) {

    for (int index = 1; index < array.length; index++) {
      assertThat(array[index].compareTo(array[index - 1]), is(greaterThanOrEqualTo(0)));
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T[] copy(T[] array) {

    T[] arrayCopy = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);

    System.arraycopy(array, 0, arrayCopy, 0, array.length);

    return arrayCopy;
  }

  @SafeVarargs
  private final <T> T[] toArray(T... elements) {
    return elements;
  }

  @SafeVarargs
  private final <T> Enumeration<T> toEnumeration(T... elements) {

    return new Enumeration<T>() {

      int index = 0;

      @Override
      public boolean hasMoreElements() {
        return (index < elements.length);
      }

      @Override
      public T nextElement() {
        Assert.isTrue(hasMoreElements(), new NoSuchElementException("No more elements"));
        return elements[index++];
      }
    };
  }

  @SafeVarargs
  private final <T> Iterable<T> toIterable(T... elements) {
    return () -> toIterator(elements);
  }

  @SafeVarargs
  private final <T> Iterator<T> toIterator(T... elements) {

    return new Iterator<T>() {

      int index = 0;

      @Override
      public boolean hasNext() {
        return (index < elements.length);
      }

      @Override
      public T next() {
        Assert.isTrue(hasNext(), new NoSuchElementException("No more elements"));
        return elements[index++];
      }
    };
  }

  @Test
  public void appendToEmptyArray() {

    String[] array = {};
    String[] newArray = ArrayUtils.append("test", array);

    assertThat(newArray, is(notNullValue(String[].class)));
    assertThat(newArray, is(not(sameInstance(array))));
    assertElements(array);
    assertElements(newArray, "test");
  }

  @Test
  public void appendToSingleElementArray() {

    String[] array = { "test" };
    String[] newArray = ArrayUtils.append("testing", array);

    assertThat(newArray, is(notNullValue(String[].class)));
    assertThat(newArray, is(not(sameInstance(array))));
    assertElements(array, "test");
    assertElements(newArray, "test", "testing");
  }

  @Test
  public void appendToTwoElementArray() {

    String[] array = { "test", "testing" };
    String[] newArray = ArrayUtils.append("tested", array);

    assertThat(newArray, is(notNullValue(String[].class)));
    assertThat(newArray, is(not(sameInstance(array))));
    assertElements(array, "test", "testing");
    assertElements(newArray, "test", "testing", "tested");
  }

  @Test
  public void appendToNullArray() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.append("test", null);
  }

  @Test
  public void asArrayWithVarargs() {

    assertThat(ArrayUtils.asArray(true, false), is(equalTo(new Boolean[] { true, false })));
    assertThat(ArrayUtils.asArray(0, 1, 2), is(equalTo(new Integer[] { 0, 1, 2 })));
    assertThat(ArrayUtils.asArray("test", "testing", "tested"),
      is(equalTo(new String[] { "test", "testing", "tested" })));
  }

  @Test
  public void asArrayWithNoArgs() {
    assertThat(ArrayUtils.<Integer>asArray(), is(equalTo(new Integer[0])));
  }

  @Test
  public void asArrayWithNull() {
    assertThat(ArrayUtils.asArray((Object[]) null), is(nullValue()));
  }

  @Test
  public void asArrayWithEnumeration() {

    Enumeration<Integer> enumeration = toEnumeration(0, 1, 2);

    Integer[] numbers = ArrayUtils.asArray(enumeration, Integer.class);

    assertThat(numbers, is(notNullValue(Integer[].class)));
    assertThat(numbers.length, is(equalTo(3)));

    for (int index = 0; index < numbers.length; index++) {
      assertThat(numbers[index], is(equalTo(index)));
    }
  }

  @Test
  public void asArrayWithEmptyEnumeration() {

    Enumeration<String> enumeration = toEnumeration();

    String[] strings = ArrayUtils.asArray(enumeration, String.class);

    assertThat(strings, is(notNullValue(String[].class)));
    assertThat(strings.length, is(equalTo(0)));
  }

  @Test
  public void asArrayWithNullEnumeration() {

    Object[] array = ArrayUtils.asArray((Enumeration<Object>) null, Object.class);

    assertThat(array, is(notNullValue(Object[].class)));
    assertThat(array.length, is(equalTo(0)));
  }

  @Test
  public void asArrayWithIterable() {

    Iterable<Integer> iterable = toIterable(0, 1, 2);

    Integer[] numbers = ArrayUtils.asArray(iterable, Integer.class);

    assertThat(numbers, is(notNullValue(Integer[].class)));
    assertThat(numbers.length, is(equalTo(3)));

    for (int index = 0; index < numbers.length; index++) {
      assertThat(numbers[index], is(equalTo(index)));
    }
  }

  @Test
  public void asArrayWithEmptyIterable() {

    Iterable<String> iterable = toIterable();

    String[] strings = ArrayUtils.asArray(iterable, String.class);

    assertThat(strings, is(notNullValue(String[].class)));
    assertThat(strings.length, is(equalTo(0)));
  }

  @Test
  public void asArrayWithNullIterable() {

    Object[] array = ArrayUtils.asArray((Iterable<Object>) null, Object.class);

    assertThat(array, is(notNullValue(Object[].class)));
    assertThat(array.length, is(equalTo(0)));
  }

  @Test
  public void asArrayWithIterator() {

    Iterator<Integer> iterator = toIterator(0, 1, 2);

    Integer[] numbers = ArrayUtils.asArray(iterator, Integer.class);

    assertThat(numbers, is(notNullValue(Integer[].class)));
    assertThat(numbers.length, is(equalTo(3)));

    for (int index = 0; index < numbers.length; index++) {
      assertThat(numbers[index], is(equalTo(index)));
    }
  }

  @Test
  public void asArrayWithEmptyIterator() {

    Iterator<String> iterator = toIterator();

    String[] strings = ArrayUtils.asArray(iterator, String.class);

    assertThat(strings, is(notNullValue(String[].class)));
    assertThat(strings.length, is(equalTo(0)));
  }

  @Test
  public void asArrayWithNullIterator() {

    Object[] array = ArrayUtils.asArray((Iterator<Object>) null, Object.class);

    assertThat(array, is(notNullValue(Object[].class)));
    assertThat(array.length, is(equalTo(0)));
  }

  @Test
  public void asEnumerationFromArray() {

    Object[] array = { "test", "testing", "tested" };

    Enumeration<Object> enumeration = ArrayUtils.asEnumeration(array);

    assertThat(enumeration, is(notNullValue(Enumeration.class)));

    for (Object element : array) {
      assertThat(enumeration.hasMoreElements(), is(true));
      assertThat(enumeration.nextElement(), is(equalTo(element)));
    }

    assertThat(enumeration.hasMoreElements(), is(false));

    exception.expect(NoSuchElementException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage(is(equalTo("No more elements")));

    enumeration.nextElement();
  }

  @Test
  public void asEnumerationFromEmptyArray() {

    Enumeration<Object> enumeration = ArrayUtils.asEnumeration();

    assertThat(enumeration, is(notNullValue(Enumeration.class)));
    assertThat(enumeration.hasMoreElements(), is(false));

    exception.expect(NoSuchElementException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage(is(equalTo("No more elements")));

    enumeration.nextElement();
  }

  @Test
  public void asEnumerationFromNullArray() {

    Enumeration<Object> enumeration = ArrayUtils.asEnumeration((Object[]) null);

    assertThat(enumeration, is(notNullValue(Enumeration.class)));
    assertThat(enumeration.hasMoreElements(), is(false));
  }

  @Test
  public void asEnumerationFromSingleElementArray() {

    Enumeration<String> enumeration = ArrayUtils.asEnumeration("test");

    assertThat(enumeration, is(notNullValue(Enumeration.class)));
    assertThat(enumeration.hasMoreElements(), is(true));
    assertThat(enumeration.nextElement(), is(equalTo("test")));
    assertThat(enumeration.hasMoreElements(), is(false));
  }

  @Test
  public void asIterableFromArray() {

    Object[] array = { "test", "testing", "tested" };

    Iterable<Object> arrayIterable = ArrayUtils.asIterable(array);

    assertThat(arrayIterable, is(notNullValue(Iterable.class)));

    int index = 0;

    for (Object element : arrayIterable) {
      assertEquals(array[index++], element);
    }

    assertEquals(array.length, index);
  }

  @Test
  public void asIterableFromEmptyArray() {

    Iterable<?> iterable = ArrayUtils.asIterable();

    assertThat(iterable, is(notNullValue(Iterable.class)));
    assertThat(iterable.iterator().hasNext(), is(false));
  }

  @Test
  public void asIterableFromNullArray() {

    Iterable<?> iterable = ArrayUtils.asIterable((Object[]) null);

    assertThat(iterable, is(notNullValue(Iterable.class)));
    assertThat(iterable.iterator().hasNext(), is(false));
  }

  @Test
  public void asIterableFromSingleElementArray() {

    Iterable<String> iterable = ArrayUtils.asIterable("test");

    assertThat(iterable, is(notNullValue(Iterable.class)));
    assertThat(iterable.iterator().hasNext(), is(true));
    assertThat(iterable.iterator().next(), is(equalTo("test")));
  }

  @Test
  public void asIteratorFromArray() {

    Object[] array = { "test", "testing", "tested" };

    Iterator<Object> arrayIterator = ArrayUtils.asIterator(array);

    assertThat(arrayIterator, is(notNullValue(Iterator.class)));
    assertThat(arrayIterator.hasNext(), is(true));

    for (Object element : array) {
      assertThat(arrayIterator.hasNext(), is(true));
      assertThat(arrayIterator.next(), is(equalTo(element)));
    }

    assertThat(arrayIterator.hasNext(), is(false));

    exception.expect(NoSuchElementException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("No more elements");

    arrayIterator.next();
  }

  @Test
  public void asIteratorFromEmptyArray() {

    Iterator<?> arrayIterator = ArrayUtils.asIterator();

    assertThat(arrayIterator, is(notNullValue(Iterator.class)));
    assertThat(arrayIterator.hasNext(), is(false));

    exception.expect(NoSuchElementException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("No more elements");

    arrayIterator.next();
  }

  @Test
  public void asIteratorFromNullArray() {

    Iterator<?> arrayIterator = ArrayUtils.asIterator((Object[]) null);

    assertThat(arrayIterator, is(notNullValue(Iterator.class)));
    assertThat(arrayIterator.hasNext(), is(false));
  }

  @Test
  public void asIteratorFromSingleElementArray() {

    Iterator<String> arrayIterator = ArrayUtils.asIterator("test");

    assertThat(arrayIterator, is(notNullValue(Iterator.class)));
    assertThat(arrayIterator.hasNext(), is(true));
    assertThat(arrayIterator.next(), is(equalTo("test")));
    assertThat(arrayIterator.hasNext(), is(false));
  }

  @Test
  public void countReturnsArrayLength() {

    Object[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    assertThat(ArrayUtils.count(array), is(equalTo(array.length)));
  }

  @Test
  public void countReturnsEmptyArrayLength() {
    assertThat(ArrayUtils.count(new Object[0]), is(equalTo(0)));
  }

  @Test
  public void countReturnsInitializedArrayLength() {
    assertThat(ArrayUtils.count(new Object[10]), is(equalTo(10)));
  }

  @Test
  public void countReturnsZeroForNullArray() {
    assertThat(ArrayUtils.count(null), is(equalTo(0)));
  }

  @Test
  public void countWithFilter() {

    Integer[] array = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    assertThat(ArrayUtils.count(array, (Filter<Integer>) NumberUtils::isEven), is(equalTo(4L)));
    assertThat(ArrayUtils.count(array, (Filter<Integer>) NumberUtils::isOdd), is(equalTo(5L)));
    assertThat(ArrayUtils.count(array, (Filter<Integer>) NumberUtils::isNegative), is(equalTo(0L)));
    assertThat(ArrayUtils.count(array, (Filter<Integer>) NumberUtils::isPositive), is(equalTo((long) array.length)));
  }

  @Test
  public void countWithFilterReturnsZero() {
    assertThat(ArrayUtils.count(new Object[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, (number) -> false),
      is(equalTo(0L)));
  }

  @Test
  public void countWithFilterAndNullArrayReturnsZero() {
    assertThat(ArrayUtils.count(null, (number) -> true), is(equalTo(0L)));
  }

  @Test
  public void countWithNullFilter() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage(is(equalTo("Filter is required")));

    ArrayUtils.count(new Object[0], null);
  }

  @Test
  public void deepCopyWithArray() {

    Object[] array = { "test", "testing", "tested" };
    Object[] arrayCopy = ArrayUtils.deepCopy(array);

    assertThat(arrayCopy, is(notNullValue()));
    assertThat(arrayCopy, is(not(sameInstance(array))));
    assertThat(arrayCopy.length, is(equalTo(array.length)));

    for (int index = 0, size = arrayCopy.length; index < size; index++) {
      assertThat(arrayCopy[index], is(equalTo(array[index])));
      assertThat(arrayCopy[index], is(not(sameInstance(array[index]))));
    }
  }

  @Test
  public void deepCopyWithArrayContainingNonCloneableElementsThrowsUnsupportedOperationException() {

    Object[] array = { "test", Person.newPerson("Jon", "Doe") };

    exception.expect(UnsupportedOperationException.class);
    exception.expectCause(is(instanceOf(CloneNotSupportedException.class)));
    exception.expectMessage(new BaseMatcher<String>() {

      @Override
      public boolean matches(Object obj) {
        return String.valueOf(obj).contains("[clone] is not supported for object of type [Person]");
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Expected UnsupportedOperationException wrapping CloneNotSupportedException.");
      }
    });

    ArrayUtils.deepCopy(array);
  }

  @Test
  public void deepCopyWithArrayHandlesNulls() {

    Object[] array = { null, "one", null, "three", null };
    Object[] arrayCopy = ArrayUtils.deepCopy(array);

    assertThat(arrayCopy, is(notNullValue()));
    assertThat(arrayCopy.length, is(equalTo(array.length)));

    for (int index = 0, size = arrayCopy.length; index < size; index++) {
      assertThat(arrayCopy[index], is(equalTo(array[index])));
    }
  }

  @Test
  public void deepCopyWithNullArrayThrowsIllegalArgumentException() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.deepCopy(null);
  }

  @Test
  public void deepCopyWithArrayAndCopyFunction() {

    Person[] people = { Person.newPerson("Jon", "Doe"), Person.newPerson("Jane", "Doe") };

    Object[] peopleCopy = ArrayUtils.deepCopy(people,
      person -> Person.newPerson(person.getFirstName(), person.getLastName()));

    assertThat(peopleCopy, is(notNullValue()));
    assertThat(peopleCopy, is(not(sameInstance(people))));
    assertThat(peopleCopy.length, is(equalTo(people.length)));

    for (int index = 0, size = people.length; index < size; index++) {
      assertThat(peopleCopy[index], is(equalTo(people[index])));
      assertThat(peopleCopy[index], is(not(sameInstance(people[index]))));
    }
  }

  @Test
  public void deepCopyWithNullArrayAndCopyFunctionThrowsIllegalArgumentException() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.deepCopy(null, Function.identity());
  }

  @Test
  public void deepCopyWithArrayAndNullCopyFunctionThrowsIllegalArgumentException() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Copy Function is required");

    ArrayUtils.deepCopy(ArrayUtils.emptyArray(), null);
  }

  @Test
  public void defaultIfEmptyWithNonNullNonEmptyArrayReturnsArray() {

    Object[] array = { "test" };
    Object[] defaultArray = { "tested" };

    assertThat(ArrayUtils.defaultIfEmpty(array,defaultArray), is(sameInstance(array)));
  }

  @Test
  public void defaultIfEmptyArrayWithEmptyArrayReturnsDefault() {

    Object[] defaultArray = { "test" };

    assertThat(ArrayUtils.defaultIfEmpty(new Object[0], defaultArray), is(sameInstance(defaultArray)));
  }

  @Test
  public void defaultIfEmptyArrayWithNullArrayReturnsDefault() {

    Object[] defaultArray = { "test" };

    assertThat(ArrayUtils.defaultIfEmpty(null, defaultArray), is(sameInstance(defaultArray)));
  }

  @Test
  public void defaultIfEmptyWithNullArrayAndNullDefaultArrayReturnsNull() {
    assertThat(ArrayUtils.defaultIfEmpty(null, null), is(nullValue()));
  }

  @Test
  public void emptyArrayIsClonedProperly() {

    Object[] emptyArray = ArrayUtils.emptyArray();

    assertThat(emptyArray, is(notNullValue(Object[].class)));
    assertThat(emptyArray.length, is(equalTo(0)));

    Object[] anotherEmptyArray = ArrayUtils.emptyArray();

    assertThat(anotherEmptyArray, is(notNullValue(Object[].class)));
    assertThat(anotherEmptyArray.length, is(equalTo(0)));
    assertThat(anotherEmptyArray, is(not(sameInstance(emptyArray))));
  }

  @Test
  public void filter() {

    Filter<Integer> evenNumberFilter = NumberUtils::isEven;
    Filter<Integer> oddNumberFilter = NumberUtils::isOdd;

    Integer[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    Integer[] evenNumbers = ArrayUtils.filter(numbers, evenNumberFilter);
    Integer[] oddNumbers = ArrayUtils.filter(numbers, oddNumberFilter);

    assertThat(evenNumbers, is(notNullValue(Integer[].class)));
    assertThat(evenNumbers.length, is(equalTo(4)));
    assertThat(evenNumbers, is(not(sameInstance(numbers))));
    assertElements(evenNumbers, 2, 4, 6, 8);
    assertThat(oddNumbers, is(notNullValue(Integer[].class)));
    assertThat(oddNumbers.length, is(equalTo(5)));
    assertThat(oddNumbers, is(not(sameInstance(numbers))));
    assertThat(oddNumbers, is(not(sameInstance(evenNumbers))));
    assertElements(oddNumbers, 1, 3, 5, 7, 9);
  }

  @Test
  public void filterAllAndNothing() {

    Filter<Integer> negativeNumberFilter = NumberUtils::isNegative;
    Filter<Integer> positiveNumberFilter = NumberUtils::isPositive;

    Integer[] numbers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    Integer[] negativeNumbers = ArrayUtils.filter(numbers, negativeNumberFilter);
    Integer[] positiveNumbers = ArrayUtils.filter(numbers, positiveNumberFilter);

    assertThat(negativeNumbers, is(notNullValue(Integer[].class)));
    assertThat(negativeNumbers.length, is(equalTo(0)));
    assertThat(negativeNumbers, is(not(sameInstance(numbers))));
    assertElements(negativeNumbers);
    assertThat(positiveNumbers, is(notNullValue(Integer[].class)));
    assertThat(positiveNumbers.length, is(equalTo(9)));
    assertThat(positiveNumbers, is(not(sameInstance(numbers))));
    assertThat(positiveNumbers, is(not(sameInstance(negativeNumbers))));
    assertElements(positiveNumbers, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  @Test
  public void filterEmptyArray() {

    String[] strings = {};
    String[] actualStrings = ArrayUtils.filter(strings, (element) -> true);

    assertThat(actualStrings, is(notNullValue(String[].class)));
    assertThat(actualStrings, is(not(sameInstance(strings))));
    assertThat(actualStrings.length, is(equalTo(strings.length)));
  }

  @Test
  public void filterNullArray() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.filter(null, (element) -> true);
  }

  @Test
  public void filterWithNullFilter() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Filter is required");

    ArrayUtils.filter(new Object[0], null);
  }

  @Test
  public void filterAndTransform() {

    FilteringTransformer<String> filteringTransformer = new FilteringTransformer<String>() {

      @Override
      public boolean accept(String value) {
        return !(value == null || value.trim().isEmpty());
      }

      @Override
      public String transform(String value) {
        return value.toUpperCase();
      }
    };

    String[] array = { null, "test", "", "testing", "  ", "tested" };
    String[] actualArray = ArrayUtils.filterAndTransform(array, filteringTransformer);

    assertThat(actualArray, is(notNullValue(String[].class)));
    assertThat(actualArray, is(not(sameInstance(array))));
    assertThat(actualArray.length, is(equalTo(3)));
    assertElements(actualArray, "TEST", "TESTING", "TESTED");
    assertElements(array, null, "test", "", "testing", "  ", "tested");
  }

  @Test
  public void filterArrayAndTransformEmptyArray() {

    FilteringTransformer<String> filteringTransformer = new FilteringTransformer<String>() {

      @Override
      public boolean accept(String value) {
        return false;
      }

      @Override
      public String transform(String value) {
        return value.toUpperCase();
      }
    };

    String[] array = { "test", "testing", "tested" };
    String[] actualArray = ArrayUtils.filterAndTransform(array, filteringTransformer);

    assertThat(actualArray, is(notNullValue(String[].class)));
    assertThat(actualArray, is(not(sameInstance(array))));
    assertThat(actualArray.length, is(equalTo(0)));
    assertElements(array, "test", "testing", "tested");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void filterAndTransformEmptyArray() {

    FilteringTransformer<String> mockFilteringTransformer = mock(FilteringTransformer.class);

    String[] array = {};
    String[] actualArray = ArrayUtils.filterAndTransform(array, mockFilteringTransformer);

    assertThat(actualArray, is(notNullValue(String[].class)));
    assertThat(actualArray, is(not(sameInstance(array))));
    assertThat(actualArray.length, is(equalTo(0)));

    verifyNoInteractions(mockFilteringTransformer);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void filterAndTransformNullArray() {

    FilteringTransformer<Object> mockFilteringTransformer = mock(FilteringTransformer.class);

    try {
      exception.expect(IllegalArgumentException.class);
      exception.expectCause(is(nullValue(Throwable.class)));
      exception.expectMessage("Array is required");

      ArrayUtils.filterAndTransform(null, mockFilteringTransformer);
    }
    finally {
      verifyNoInteractions(mockFilteringTransformer);
    }
  }

  @Test
  public void filterAndTransformWithNullFilteringTransformer() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Filter is required");

    ArrayUtils.filterAndTransform(new Object[0], null);
  }

  @Test
  public void find() {

    Person cookieDoe = Person.newPerson("Cookie", "Doe");
    Person froDoe = Person.newPerson("Fro", "Doe");
    Person janeDoe = Person.newPerson("Jane", "Doe");
    Person joeDoe = Person.newPerson("Joe", "Doe");
    Person jonDoe = Person.newPerson("Jon", "Doe");
    Person pieDoe = Person.newPerson("Pie", "Doe");
    Person playDoe = Person.newPerson("Play", "Doe");
    Person jackHandy = Person.newPerson("Jack", "Handy");
    Person sandyHandy = Person.newPerson("Sandy", "Handy");

    Person[] people = { jackHandy, jonDoe, joeDoe, janeDoe, sandyHandy, pieDoe, froDoe, cookieDoe, playDoe };

    Filter<Person> doeFilter = (person) -> "Doe".equalsIgnoreCase(person.getLastName());

    Person actualPerson = ArrayUtils.findOne(people, doeFilter);

    assertThat(actualPerson, is(equalTo(jonDoe)));
  }

  @Test
  public void findWithNonMatchingFilter() {

    Person cookieDoe = Person.newPerson("Cookie", "Doe");
    Person froDoe = Person.newPerson("Fro", "Doe");
    Person janeDoe = Person.newPerson("Jane", "Doe");
    Person joeDoe = Person.newPerson("Joe", "Doe");
    Person jonDoe = Person.newPerson("Jon", "Doe");
    Person pieDoe = Person.newPerson("Pie", "Doe");
    Person playDoe = Person.newPerson("Play", "Doe");
    Person jackHandy = Person.newPerson("Jack", "Handy");
    Person sandyHandy = Person.newPerson("Sandy", "Handy");

    Person[] people = { jackHandy, jonDoe, joeDoe, janeDoe, sandyHandy, pieDoe, froDoe, cookieDoe, playDoe };

    Filter<Person> doeFilter = (person) -> ("Hoe".equalsIgnoreCase(person.getFirstName())
      && "Doe".equalsIgnoreCase(person.getLastName()));

    Person actualPerson = ArrayUtils.findOne(people, doeFilter);

    assertThat(actualPerson, is(nullValue(Person.class)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void findWithEmptyArray() {

    Filter<Object> mockFilter = mock(Filter.class);

    assertThat(ArrayUtils.findOne(toArray(), mockFilter), is(nullValue()));

    verifyNoInteractions(mockFilter);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void findWithNullArray() {

    Filter<Object> mockFilter = mock(Filter.class);

    assertThat(ArrayUtils.findOne(null, mockFilter), is(nullValue()));

    verifyNoInteractions(mockFilter);
  }

  @Test
  public void findWithNullFilter() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Filter is required");

    ArrayUtils.findOne(toArray(), null);
  }

  @Test
  public void findAll() {

    Integer[] numbers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    Filter<Integer> evenNumberFilter = NumberUtils::isEven;

    List<Integer> actualNumbers = ArrayUtils.findAll(numbers, evenNumberFilter);

    assertThat(actualNumbers, is(notNullValue(List.class)));
    assertThat(actualNumbers.size(), is(equalTo(5)));
    assertElements(actualNumbers.toArray(), 0, 2, 4, 6, 8);
  }

  @Test
  public void findAllFromEmptyArray() {

    List<?> matches = ArrayUtils.findAll(toArray(), (element) -> true);

    assertThat(matches, is(notNullValue(List.class)));
    assertThat(matches.isEmpty(), is(true));
  }

  @Test
  public void findAllFromNullArray() {

    List<?> matches = ArrayUtils.findAll(null, (element) -> true);

    assertThat(matches, is(notNullValue(List.class)));
    assertThat(matches.isEmpty(), is(true));
  }

  @Test
  public void findAllWithNonMatchingFilter() {

    Integer[] numbers = { 0, 2, 4, 8, 16, 32, 64 };

    Filter<Integer> oddNumberFilter = NumberUtils::isOdd;

    List<Integer> actualNumbers = ArrayUtils.findAll(numbers, oddNumberFilter);

    assertThat(actualNumbers, is(notNullValue(List.class)));
    assertThat(actualNumbers.isEmpty(), is(true));
  }

  @Test
  public void findAllWithNullFilter() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Filter is required");

    ArrayUtils.findAll(toArray(), null);
  }

  @Test
  public void getElementAtValidIndexInArray() {

    Object[] array = { "test", "testing", "tested" };

    assertThat(ArrayUtils.getElementAt(array, 1), is(equalTo("testing")));
    assertThat(ArrayUtils.getElementAt(array, 0), is(equalTo("test")));
    assertThat(ArrayUtils.getElementAt(array, 2), is(equalTo("tested")));
  }

  @Test
  public void getElementAtInvalidIndexInArray() {
    assertThat(ArrayUtils.getElementAt(new Object[] { "test" }, 1), is(nullValue()));
  }

  @Test
  public void getElementAtInvalidIndexFromEmptyArray() {
    assertThat(ArrayUtils.getElementAt(new Object[0], 0), is(nullValue()));
  }

  @Test
  public void getElementAtInvalidIndexFromNullArray() {
    assertThat(ArrayUtils.getElementAt(null, 0), is(nullValue()));
  }

  @Test
  public void getElementAtValidIndexInArayWithDefaultValueReturnsElementAtIndex() {

    Object[] array = { "test", "testing", "tested" };

    assertThat(ArrayUtils.getElementAt(array, 1, "default"), is(equalTo("testing")));
    assertThat(ArrayUtils.getElementAt(array, 0, "default"), is(equalTo("test")));
    assertThat(ArrayUtils.getElementAt(array, 2, "default"), is(equalTo("tested")));
  }

  @Test
  public void getElementAtInvalidIndexInArrayWithDefaultVAlueReturnsDefaultValue() {
    assertThat(ArrayUtils.getElementAt(new Object[] { "test" }, 1, "default"),
      is(equalTo("default")));
  }

  @Test
  public void getElementAtInvalidIndexFromEmptyArrayWithDefaultValueReturnsDefaultValue() {
    assertThat(ArrayUtils.getElementAt(new Object[0], 0, "default"), is(equalTo("default")));
  }

  @Test
  public void getElementAtInvalidIndexFromNullArrayWithDefaultValueReturnsDefaultValue() {
    assertThat(ArrayUtils.getElementAt(null, 0, "default"), is(equalTo("default")));
  }

  @Test
  public void getFirstFromArray() {
    assertThat(ArrayUtils.getFirst("test", "testing", "tested"), is(equalTo("test")));
  }

  @Test
  public void getFirstFromEmptyArray() {
    assertThat(ArrayUtils.getFirst(), is(nullValue()));
  }

  @Test
  public void getFirstFromNullArray() {
    assertThat(ArrayUtils.getFirst((Object[]) null), is(nullValue()));
  }

  @Test
  public void getFirstFromSingleElementArray() {
    assertThat(ArrayUtils.getFirst("test"), is(equalTo("test")));
  }

  @Test
  public void getFirstFromNonNullNonEmptyArrayWithDefaultValueReturnsFirstElement() {
    assertThat(ArrayUtils.getFirst(new Object[] { "test", "testing", "tested" }, "default"),
      is(equalTo("test")));
  }

  @Test
  public void getFirstFromEmptyArrayWithDefaultValueReturnsDefaultValue() {
    assertThat(ArrayUtils.getFirst(new Object[0], "default"), is(equalTo("default")));
  }

  @Test
  public void getFirstFromNullArrayWithDefaultValueReturnsDefaultValue() {
    assertThat(ArrayUtils.getFirst(null, "default"), is(equalTo("default")));
  }

  @Test
  public void getFirstFromNullArrayWithNullDefaultValueReturnsNull() {
    assertThat(ArrayUtils.getFirst(null, null), is(nullValue()));
  }

  @Test
  public void getLastFromArray() {
    assertThat(ArrayUtils.getLast("test", "testing", "tested"), is(equalTo("tested")));
  }

  @Test
  public void getLastFromEmptyArray() {
    assertThat(ArrayUtils.getLast(), is(nullValue()));
  }

  @Test
  public void getLastFromNullArray() {
    assertThat(ArrayUtils.getLast((Object[]) null), is(nullValue()));
  }

  @Test
  public void getLastFromSingleElementArray() {
    assertThat(ArrayUtils.getLast("test"), is(equalTo("test")));
  }

  @Test
  public void getLastFromNonNullNonEmptyArrayWithDefaultValueReturnsLastElement() {
    assertThat(ArrayUtils.getLast(new Object[] { "test", "testing", "tested" }, "default"),
      is(equalTo("tested")));
  }

  @Test
  public void getLastFromEmptyArrayWithDefaultValueReturnsDefaultValue() {
    assertThat(ArrayUtils.getLast(new Object[0], "default"), is(equalTo("default")));
  }

  @Test
  public void getLastFromNullArrayWithDefaultValueReturnsDefaultValue() {
    assertThat(ArrayUtils.getLast(null, "default"), is(equalTo("default")));
  }

  @Test
  public void getLastFromNullArrayWithNullDefaultValueReturnsNull() {
    assertThat(ArrayUtils.getLast(null, null), is(nullValue()));
  }

  @Test
  public void indexOfElementInArrayReturnsIndex() {

    Object[] array = { "test", "testing", "tested"};

    assertThat(ArrayUtils.indexOf(array, "test"), is(equalTo(0)));
    assertThat(ArrayUtils.indexOf(array, "testing"), is(equalTo(1)));
    assertThat(ArrayUtils.indexOf(array, "tested"), is(equalTo(2)));
  }

  @Test
  public void indexOfNonExistingElementInArrayReturnsMinusOne() {
    assertThat(ArrayUtils.indexOf(new Object[] { "test", "testing", "tested" }, "mock"),
      is(equalTo(-1)));
  }

  @Test
  public void indexOfNonNullElementInNullArrayReturnsMinusOne() {
    assertThat(ArrayUtils.indexOf(null, "test"), is(equalTo(-1)));
  }

  @Test
  public void indexOfNullElementInArrayReturnsMinusOne() {
    assertThat(ArrayUtils.indexOf(new Object[] { "test", "testing", "tested" }, null),
      is(equalTo(-1)));
  }

  @Test
  public void indexOfRepeatingElementInArrayReturnsFirstIndexFound() {
    assertThat(ArrayUtils.indexOf(new Object[] { "test", "testing", "testing" }, "testing"),
      is(equalTo(1)));
  }

  @Test
  public void indexOfRepeatingNullElementInArrayReturnsFirstIndexFound() {
    assertThat(ArrayUtils.indexOf(new Object[] { "test", null, null, "testing", "testing", null }, null),
      is(equalTo(1)));
  }

  @Test
  public void insertIntoArray() {

    assertElements(ArrayUtils.insert("one", toArray("two", "three"), 0), "one", "two", "three");
    assertElements(ArrayUtils.insert("two", toArray("one", "three"), 1), "one", "two", "three");
    assertElements(ArrayUtils.insert("three", toArray("one", "two"), 2), "one", "two", "three");
  }

  @Test
  public void insertIntoEmptyArray() {
    assertElements(ArrayUtils.insert("one", toArray(), 0), "one");
  }

  @Test
  public void insertIntoNullArray() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.insert("test", null, 0);
  }

  @Test
  public void insertIntoSingleElementArray() {

    assertElements(ArrayUtils.insert("one", toArray("two"), 0), "one", "two");
    assertElements(ArrayUtils.insert("two", toArray("one"), 1), "one", "two");
  }

  @Test
  public void insertNullElementIntoArray() {

    assertElements(ArrayUtils.insert(null, toArray("one", "two"), 0), null, "one", "two");
    assertElements(ArrayUtils.insert(null, toArray("one", "two"), 1), "one", null, "two");
    assertElements(ArrayUtils.insert(null, toArray("one", "two"), 2), "one", "two", null);
  }

  @Test
  public void insertIntoArrayWithNegativeArrayIndex() {

    exception.expect(ArrayIndexOutOfBoundsException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("[-1] is not a valid index [0, 2] in the array");

    ArrayUtils.insert("one", toArray("zero", "two"), -1);
  }

  @Test
  public void insertIntoArrayWithOverflowArrayIndex() {

    exception.expect(ArrayIndexOutOfBoundsException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("[3] is not a valid index [0, 2] in the array");

    ArrayUtils.insert("three", toArray("one", "two"), 3);
  }

  @Test
  public void isEmptyWithEmptyArray() {

    assertTrue(ArrayUtils.isEmpty(null));
    assertTrue(ArrayUtils.isEmpty(new Object[0]));
    assertTrue(ArrayUtils.isEmpty(new Object[] {}));
  }

  @Test
  public void isEmptyWithNonEmptyArray() {

    assertFalse(ArrayUtils.isEmpty(new Object[10]));
    assertFalse(ArrayUtils.isEmpty(new Object[] { null }));
    assertFalse(ArrayUtils.isEmpty(new Object[] { "test" }));
    assertFalse(ArrayUtils.isEmpty(new Object[] { "test", "testing", "tested" }));
  }

  @Test
  public void isNotEmptyWithEmptyArray() {

    assertFalse(ArrayUtils.isNotEmpty(null));
    assertFalse(ArrayUtils.isNotEmpty(new Object[0]));
    assertFalse(ArrayUtils.isNotEmpty(new Object[] {}));
  }

  @Test
  public void isNotEmptyWithNonEmptyArray() {

    assertTrue(ArrayUtils.isNotEmpty(new Object[1]));
    assertTrue(ArrayUtils.isNotEmpty(new Object[] { null }));
    assertTrue(ArrayUtils.isNotEmpty(new Object[] { "test" }));
    assertTrue(ArrayUtils.isNotEmpty(new Object[] { "test", "testing", "tested" }));
  }

  @Test
  public void isSizeOneWithNullArrayIsFalse() {
    assertThat(ArrayUtils.isSizeOne((Object[]) null), is(false));
  }

  @Test
  public void isSizeOneWithEmptyArrayIsFalse() {
    assertThat(ArrayUtils.isSizeOne(), is(false));
  }

  @Test
  public void isSizeOneWithArrayHavingOneElementIsTrue() {
    assertThat(ArrayUtils.isSizeOne(1), is(true));
  }

  @Test
  public void isSizeOneWithArrayHavingTwoElementsIsFalse() {
    assertThat(ArrayUtils.isSizeOne(1, 2), is(false));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void attemptRemovalFromArrayIterator() {

    String[] array = { "one", "two" };

    Iterator<String> arrayIterator = ArrayUtils.asIterator(array);

    assertThat(arrayIterator, is(notNullValue(Iterator.class)));
    assertThat(arrayIterator.hasNext(), is(true));
    assertThat(arrayIterator.next(), is(equalTo("one")));
    assertThat(arrayIterator.hasNext(), is(true));
    assertElements(array, "one", "two");

    try {
      arrayIterator.remove();
    }
    finally {
      assertElements(array, "one", "two");
      assertThat(arrayIterator.hasNext(), is(true));
      assertThat(arrayIterator.next(), is(equalTo("two")));
      assertThat(arrayIterator.hasNext(), is(false));
    }
  }

  @Test
  public void nullSafeArrayWithArray() {

    String[] expectedArray = { "test", "testing", "tested" };
    String[] actualArray = ArrayUtils.nullSafeArray(expectedArray);

    assertThat(actualArray, is(sameInstance(expectedArray)));
  }

  @Test
  public void nullSafeArrayWithEmptyArray() {

    Character[] expectedArray = {};
    Character[] actualArray = ArrayUtils.nullSafeArray(expectedArray);

    assertThat(actualArray, is(sameInstance(expectedArray)));
  }

  @Test
  @SuppressWarnings("all")
  public void nullSafeArrayWithNullArray() {

    Object[] actualArray = ArrayUtils.nullSafeArray(null, Integer.class);

    assertThat(actualArray, is(notNullValue(Object[].class)));
    assertThat(actualArray.getClass().getComponentType(), is(equalTo(Integer.class)));
    assertThat(actualArray.length, is(equalTo(0)));
  }

  @Test
  public void nullSafeArrayWithNullArrayAndUnspecifiedComponentType() {

    Object actualArray = ArrayUtils.nullSafeArray(null);

    assertThat(actualArray, is(notNullValue()));
    assertThat(actualArray.getClass().isArray(), is(true));
    assertThat(actualArray.getClass().getComponentType(), is(equalTo(Object.class)));
    assertThat(((Object[]) actualArray).length, is(equalTo(0)));
  }

  @Test
  public void nullSafeArrayWithNullArrayUsedInAForEachLoop() {

    int sum = 0;

    //for (Integer number : ArrayUtils.<Integer>nullSafeArray(null)) { throws ClassCastException!
    for (Integer number : ArrayUtils.<Integer>nullSafeArray(null, Integer.class)) {
      sum += number;
    }

    assertThat(sum, is(equalTo(0)));
  }

  @Test
  public void componentTypeOfNonNullArray() {

    assertThat(ArrayUtils.componentType(new Integer[0]), is(equalTo(Integer.class)));
    assertThat(ArrayUtils.componentType(new String[0]), is(equalTo(String.class)));
    assertThat(ArrayUtils.componentType(new Object[0]), is(equalTo(Object.class)));
  }

  @Test
  public void componentTypeOfNullArray() {
    assertThat(ArrayUtils.componentType(null), is(equalTo(Object.class)));
  }

  @Test
  public void nullSafeLengthReturnsArrayLength() {

    Object[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    assertThat(ArrayUtils.nullSafeLength(array), is(equalTo(array.length)));
  }

  @Test
  public void nullSafeLengthReturnsEmptyArrayLength() {
    assertThat(ArrayUtils.nullSafeLength(new Object[0]), is(equalTo(0)));
  }

  @Test
  public void nullSafeLengthReturnsInitializedArrayLength() {
    assertThat(ArrayUtils.nullSafeLength(new Object[10]), is(equalTo(10)));
  }

  @Test
  public void nullSafeLengthReturnsZeroForNullArray() {
    assertThat(ArrayUtils.nullSafeLength(null), is(equalTo(0)));
  }

  @Test
  public void prependToEmptyArray() {

    String[] array = {};
    String[] actualArray = ArrayUtils.prepend("test", array);

    assertThat(actualArray, is(notNullValue(String[].class)));
    assertThat(actualArray, is(not(sameInstance(array))));
    assertElements(actualArray, "test");
    assertElements(array);
  }

  @Test
  public void prependToSingleElementArray() {

    String[] array = { "tested" };
    String[] actualArray = ArrayUtils.prepend("testing", array);

    assertThat(actualArray, is(notNullValue(String[].class)));
    assertThat(actualArray, is(not(sameInstance(array))));
    assertElements(actualArray, "testing", "tested");
    assertElements(array, "tested");
  }

  @Test
  public void prependToTwoElementArray() {

    String[] array = { "testing", "tested" };
    String[] actualArray = ArrayUtils.prepend("test", array);

    assertThat(actualArray, is(notNullValue(String[].class)));
    assertThat(actualArray, is(not(sameInstance(array))));
    assertElements(actualArray, "test", "testing", "tested");
    assertElements(array, "testing", "tested");
  }

  @Test
  public void prependToNullArray() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.prepend("test", null);
  }

  @Test
  public void removeFromArray() {

    Object[] array = { 1, 2, 3, 4 };
    Object[] newArray = ArrayUtils.remove(array, 2);

    assertThat(newArray, is(notNullValue(Object[].class)));
    assertThat(newArray, is(not(sameInstance(array))));
    assertThat(newArray.length, is(equalTo(3)));
    assertThat(Arrays.asList(newArray).containsAll(Arrays.asList(1, 2, 4)), is(true));
    assertThat(array.length, is(equalTo(4)));
  }

  @Test
  public void removeFirstElementFromArray() {

    Object[] array = { 1, 2, 3, 4 };
    Object[] newArray = ArrayUtils.remove(array, 0);

    assertThat(newArray, is(notNullValue(Object[].class)));
    assertThat(newArray, is(not(sameInstance(array))));
    assertThat(newArray.length, is(equalTo(3)));
    assertThat(Arrays.asList(newArray).containsAll(Arrays.asList(2, 3, 4)), is(true));
    assertThat(array.length, is(equalTo(4)));
  }

  @Test
  public void removeLastElementFromArray() {

    Object[] array = { 1, 2, 3, 4 };
    Object[] newArray = ArrayUtils.remove(array, 3);

    assertThat(newArray, is(notNullValue(Object[].class)));
    assertThat(newArray, is(not(sameInstance(array))));
    assertThat(newArray.length, is(equalTo(3)));
    assertThat(Arrays.asList(newArray).containsAll(Arrays.asList(1, 2, 3)), is(true));
    assertThat(array.length, is(equalTo(4)));
  }

  @Test
  public void removeOnlyElementFromArray() {

    Object[] array = { 1 };
    Object[] newArray = ArrayUtils.remove(array, 0);

    assertThat(newArray, is(notNullValue(Object[].class)));
    assertThat(newArray, is(not(sameInstance(array))));
    assertThat(newArray.length, is(equalTo(0)));
    assertThat(array.length, is(equalTo(1)));
  }

  @Test
  public void removeIllegalIndexFromArrayThrowsArrayIndexOutOfBoundsException() {

    exception.expect(ArrayIndexOutOfBoundsException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("[4] is not a valid index [0, 4] in the array");

    ArrayUtils.remove(new Object[] { 1, 2, 3, 4 }, 4);
  }

  @Test
  public void removeFromNullArrayThrowsIllegalArgumentException() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.remove(null, 0);
  }

  @Test
  public void shallowCopyWithArrayContainingObjectTypes() {

    Object[] array = { Person.newPerson("Jon", "Doe"), Person.newPerson("Jane", "Doe") };
    Object[] arrayCopy = ArrayUtils.shallowCopy(array);

    assertThat(arrayCopy, is(notNullValue()));
    assertThat(arrayCopy, is(not(sameInstance(array))));
    assertThat(arrayCopy.length, is(equalTo(array.length)));

    for (int index = 0, size = array.length; index < size; index++) {
      assertThat(arrayCopy[index], is(equalTo(array[index])));
      assertThat(arrayCopy[index], is(sameInstance(array[index])));
    }
  }

  @Test
  public void shallowCopyWithArrayContainingPrimitiveTypes() {

    Object[] array = { "test", "testing", "tested" };
    Object[] arrayCopy = ArrayUtils.shallowCopy(array);

    assertThat(arrayCopy, is(notNullValue()));
    assertThat(arrayCopy, is(not(sameInstance(array))));
    assertThat(arrayCopy.length, is(equalTo(array.length)));

    for (int index = 0, size = array.length; index < size; index++) {
      assertThat(arrayCopy[index], is(equalTo(array[index])));
      assertThat(arrayCopy[index], is(sameInstance(array[index])));
    }
  }

  @Test
  public void shallowCopyWithNullArray() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.shallowCopy(null);
  }

  @Test
  public void shuffle() {

    Integer[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    Integer[] shuffledArray = ArrayUtils.shuffle(copy(array));

    assertThat(shuffledArray, is(notNullValue(Integer[].class)));
    assertThat(shuffledArray, is(not(sameInstance(array))));
    assertShuffled(array, shuffledArray);

    Integer[] shuffledArrayAgain = ArrayUtils.shuffle(copy(shuffledArray));

    assertThat(shuffledArrayAgain, is(notNullValue(Integer[].class)));
    assertThat(shuffledArrayAgain, is(not(sameInstance(shuffledArray))));
    assertShuffled(shuffledArray, shuffledArrayAgain);
    assertShuffled(array, shuffledArrayAgain);
  }

  @Test
  public void shuffleEmptyArray() {

    Object[] emptyArray = {};
    Object[] shuffledEmptyArray = ArrayUtils.shuffle(emptyArray);

    assertThat(shuffledEmptyArray, is(sameInstance(emptyArray)));
  }

  @Test
  public void shuffleNullArray() {
    assertThat(ArrayUtils.shuffle(null), is(nullValue(Object[].class)));
  }

  @Test
  public void shuffleSingleElementArray() {

    Object[] singleElementArray = { "test" };
    Object[] shuffledSingleElementArray = ArrayUtils.shuffle(singleElementArray);

    assertThat(shuffledSingleElementArray, is(sameInstance(singleElementArray)));
    assertElements(shuffledSingleElementArray, "test");
  }

  @Test
  public void sortComparableObjectArray() {

    Integer[] numbers = { 2, 4, 3, 1 };
    Integer[] sortedNumbers = ArrayUtils.sort(numbers);

    assertThat(numbers, is(sameInstance(numbers)));
    assertSorted(sortedNumbers);
  }

  @Test
  public void sortNonComparableObjectArray() {

    Person[] people = {
      Person.newPerson("Jack", "Handy"),
      Person.newPerson("Jon", "Doe"),
      Person.newPerson("Agent", "Smith"),
      Person.newPerson("Pie", "Doe")
    };

    Person[] sortedPeople = ArrayUtils.sort(people, (personOne, personTwo) ->
      ComparatorResultBuilder.<String>create()
        .doCompare(personOne.getLastName(), personTwo.getLastName())
        .doCompare(personOne.getFirstName(), personTwo.getFirstName())
        .getResult()
    );

    assertThat(sortedPeople, is(sameInstance(people)));
    assertThat(Arrays.asList(sortedPeople), is(equalTo(Arrays.asList(Person.newPerson("Jon", "Doe"),
      Person.newPerson("Pie", "Doe"), Person.newPerson("Jack", "Handy"), Person.newPerson("Agent", "Smith")))));
  }

  @Test
  public void subArray() {

    Integer[] array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    Integer[] subArray = ArrayUtils.subArray(array, 1, 2, 4, 8);

    assertThat(subArray, is(not(sameInstance(array))));
    assertElements(subArray, 1, 2, 4, 8);
  }

  @Test
  public void subArrayWithEmptyArray() {

    Object[] subArray = ArrayUtils.subArray(new Object[0]);

    assertThat(subArray, is(notNullValue(Object[].class)));
    assertThat(subArray.length, is(equalTo(0)));
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void subArrayWithEmptyArrayAndIndices() {
    ArrayUtils.subArray(new Object[0], 0);
  }

  @Test
  public void subArrayWithNonEmptyArrayAndNoIndices() {

    String[] array = { "test", "testing", "tested" };
    String[] subArray = ArrayUtils.subArray(array);

    assertThat(subArray, is(not(sameInstance(array))));
    assertThat(subArray, is(notNullValue(String[].class)));
    assertThat(subArray.length, is(equalTo(0)));
  }

  @Test
  public void subArrayWithSingleElementArrayAndValidIndex() {

    String[] array = { "test" };
    String[] subArray = ArrayUtils.subArray(array, 0);

    assertThat(subArray, is(not(sameInstance(array))));
    assertElements(subArray, "test");
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void subArrayWithSingleElementArrayAndInvalidIndex() {
    ArrayUtils.subArray(new Integer[] { 0 }, 1);
  }

  @Test(expected = NullPointerException.class)
  public void subArrayWithNullArray() {
    ArrayUtils.subArray(null, 1);
  }

  @Test
  public void swapArrayElements() {

    Integer[] array = { 0, 1, 2 };
    Integer[] actualArray = ArrayUtils.swap(array, 0, 2);

    assertThat(actualArray, is(sameInstance(array)));
    assertElements(actualArray, 2, 1, 0);
  }

  @Test
  public void swapWithSingleElementArray() {

    String[] array = { "test" };
    String[] actualArray = ArrayUtils.swap(array, 0, 0);

    assertThat(actualArray, is(sameInstance(array)));
    assertElements(actualArray, "test");
  }

  @Test
  public void swapWithTwoElementArray() {

    Integer[] array = { 0, 1 };
    Integer[] actualArray = ArrayUtils.swap(array, 0, 1);

    assertThat(actualArray, is(sameInstance(array)));
    assertElements(actualArray, 1, 0);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void swapWithIllegalFirstIndex() {

    Integer[] array = { 0, 1, 2 };

    try {
      ArrayUtils.swap(array, -1, 1);
    }
    finally {
      assertElements(array, 0, 1, 2);
    }
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void swapWithIllegalSecondIndex() {

    Integer[] array = { 0, 1, 2 };

    try {
      ArrayUtils.swap(array, 1, 3);
    }
    finally {
      assertElements(array, 0, 1, 2);
    }
  }

  @Test(expected = NullPointerException.class)
  public void swapWithNullArray() {
    ArrayUtils.swap(null, 0, 10);
  }

  @Test
  public void toStringArrayWithObjectArray() {

    String[] stringArray = ArrayUtils.toStringArray(Boolean.TRUE, 'x', 2, 3.14159d, "test");

    assertThat(stringArray, is(notNullValue(String[].class)));
    assertThat(stringArray.length, is(equalTo(5)));
    assertThat(Arrays.asList(stringArray), contains("true", "x", "2", "3.14159", "test"));
  }

  @Test
  public void toStringArrayWithStringArray() {

    String[] stringArray = ArrayUtils.toStringArray("test", "testing", "tested");

    assertThat(stringArray, is(notNullValue(String[].class)));
    assertThat(stringArray.length, is(equalTo(3)));
    assertThat(Arrays.asList(stringArray), contains("test", "testing", "tested"));
  }

  @Test
  public void toStringArrayWithArrayHavingNullElements() {

    String[] stringArray = ArrayUtils.toStringArray('x', null, "test", null, "nil");

    assertThat(stringArray, is(notNullValue(String[].class)));
    assertThat(stringArray.length, is(equalTo(5)));
    assertThat(Arrays.asList(stringArray), contains("x", "null", "test", "null", "nil"));
  }

  @Test
  public void toStringArrayWithNullArrayReturnsEmptyStringArray() {

    String[] stringArray = ArrayUtils.toStringArray((Object[]) null);

    assertThat(stringArray, is(notNullValue(String[].class)));
    assertThat(stringArray, is(emptyArray()));
  }

  @Test
  public void transformArray() {

    Transformer<String> transformer = String::toUpperCase;

    String[] array = { "test", "testing", "tested" };
    String[] actualArray = ArrayUtils.transform(array, transformer);

    assertThat(actualArray, is(sameInstance(array)));
    assertElements(actualArray, "TEST", "TESTING", "TESTED");
  }

  @Test
  public void transformEmptyArray() {

    Object[] array = {};
    Object[] transformedArray = ArrayUtils.transform(array, (value) -> null);

    assertThat(transformedArray, is(sameInstance(array)));
  }

  @Test
  public void transformNullArray() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Array is required");

    ArrayUtils.transform(null, (value) -> "test");
  }

  @Test
  public void transformWithNullTransformer() {

    exception.expect(IllegalArgumentException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("Transformer is required");

    ArrayUtils.transform(new Object[0], null);
  }

  @Data
  @RequiredArgsConstructor(staticName = "newPerson")
  static class Person {

    @NonNull final String firstName;
    @NonNull final String lastName;

    @Override
    public boolean equals(Object obj) {

      if (this == obj) {
        return true;
      }

      if (!(obj instanceof Person)) {
        return false;
      }

      Person that = (Person) obj;

      return ObjectUtils.equals(this.getFirstName(), that.getFirstName())
        && ObjectUtils.equals(this.getLastName(), that.getLastName());
    }

    @Override
    public int hashCode() {

      int hashValue = 17;

      hashValue = 37 * hashValue + ObjectUtils.hashCode(getFirstName());
      hashValue = 37 * hashValue + ObjectUtils.hashCode(getLastName());

      return hashValue;
    }

    @Override
    public String toString() {
      return getFirstName().concat(" ").concat(getLastName());
    }
  }
}
