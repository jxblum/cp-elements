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
package org.cp.elements.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import org.cp.elements.security.model.User;

/**
 * Unit Tests for {@link FunctionUtils}
 *
 * @author John Blum
 * @see java.util.function.Consumer
 * @see java.util.function.Function
 * @see java.util.function.Predicate
 * @see java.util.function.Supplier
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @see org.cp.elements.function.FunctionUtils
 * @since 1.0.0
 */
public class FunctionUtilsUnitTests {

  @SuppressWarnings("unchecked")
  private <T> Predicate<T> mockPredicate(boolean testReturnValue) {

    Predicate<T> mockPredicate = mock(Predicate.class);

    doReturn(testReturnValue).when(mockPredicate).test(any());

    return mockPredicate;
  }

  @Test
  public void asSupplierWithNonNullReturnValue() {

    Supplier<?> supplier = FunctionUtils.asSupplier("test");

    assertThat(supplier).isNotNull();
    assertThat(supplier.get()).isEqualTo("test");
  }

  @Test
  public void asSupplierWithNullReturnValue() {

    Supplier<?> supplier = FunctionUtils.asSupplier(null);

    assertThat(supplier).isNotNull();
    assertThat(supplier.get()).isNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeFunctionsIsCorrect() {

    Function<Integer, Integer> sum = value -> value + value;
    Function<Integer, Integer> multiply = value -> value * value;

    Function<Integer, Integer> function = FunctionUtils.compose(sum, multiply);

    assertThat(function).isNotNull();
    assertThat(function.apply(2)).isEqualTo(16);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeFunctionsContainingNullsIsNullSafe() {

    Function<String, String> duplicate = stringValue -> stringValue + " " + stringValue;

    Function<String, String> function =
      FunctionUtils.compose(null, duplicate, null, null, duplicate, duplicate, null);

    assertThat(function).isNotNull();
    assertThat(function.apply("TEST")).isEqualTo("TEST TEST TEST TEST TEST TEST TEST TEST");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeFunctionsOfDifferentValuesIsCorrect() {

    Function<String, Integer> toInteger = Integer::parseInt;

    Function<Integer, Par> toEnum = Par::valueOf;

    Function<String, Par> function = FunctionUtils.compose(toInteger, toEnum);

    assertThat(function).isNotNull();
    assertThat(function.apply("4")).isEqualTo(Par.FOUR);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeNullFunctionsIsNullSate() {

    Function<Object, Object> function = FunctionUtils.compose((Function<?, ?>[]) null);

    assertThat(function).isNotNull();
    assertThat(function.apply("TEST")).isEqualTo("TEST");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composePredicatesWithAnd() {

    Predicate<Object> mockPredicateOne = mockPredicate(true);
    Predicate<Object> mockPredicateTwo = mockPredicate(true);

    Predicate<Object> composition =
      FunctionUtils.composeAnd(mockPredicateOne, null, mockPredicateTwo, null, null);

    assertThat(composition).isNotNull();
    assertThat(composition).isNotSameAs(mockPredicateOne);
    assertThat(composition).isNotSameAs(mockPredicateTwo);
    assertThat(composition.test("mock")).isTrue();

    verify(mockPredicateOne, times(1)).test(eq("mock"));
    verify(mockPredicateTwo, times(1)).test(eq("mock"));
    verifyNoMoreInteractions(mockPredicateOne, mockPredicateTwo);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composePredicatesWithAndShortCircuits() {

    Predicate<Object> mockPredicateOne = mockPredicate(false);
    Predicate<Object> mockPredicateTwo = mockPredicate(true);

    Predicate<Object> composition =
      FunctionUtils.composeAnd(null, null, mockPredicateOne, null, mockPredicateTwo, null);

    assertThat(composition).isNotNull();
    assertThat(composition).isNotSameAs(mockPredicateOne);
    assertThat(composition).isNotSameAs(mockPredicateTwo);
    assertThat(composition.test("mock")).isFalse();

    verify(mockPredicateOne, times(1)).test(eq("mock"));
    verify(mockPredicateTwo, never()).test(any());
    verifyNoMoreInteractions(mockPredicateOne);
    verifyNoInteractions(mockPredicateTwo);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeSinglePredicateWithAnd() {

    Predicate<Object> mockPredicate = mockPredicate(false);
    Predicate<Object> composition = FunctionUtils.composeAnd(null, null, mockPredicate);

    assertThat(composition).isNotNull();
    assertThat(composition).isNotSameAs(mockPredicate);
    assertThat(composition.test("mock")).isFalse();

    verify(mockPredicate, times(1)).test(eq("mock"));
    verifyNoMoreInteractions(mockPredicate);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeNoPredicatesWithAnd() {

    Predicate<Object> predicate = FunctionUtils.composeAnd();

    assertThat(predicate).isNotNull();
    assertThat(predicate.test("mock")).isTrue();
  }

  @Test
  public void composeNullPredicatesWithAndIsNullSafe() {

    Predicate<Object> predicate = FunctionUtils.composeAnd((Predicate<Object>[]) null);

    assertThat(predicate).isNotNull();
    assertThat(predicate.test("mock")).isTrue();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composePredicatesWithOr() {

    Predicate<Object> mockPredicateOne = mockPredicate(false);
    Predicate<Object> mockPredicateTwo = mockPredicate(true);

    Predicate<Object> composition =
      FunctionUtils.composeOr(mockPredicateOne, null, mockPredicateTwo, null, null);

    assertThat(composition).isNotNull();
    assertThat(composition).isNotSameAs(mockPredicateOne);
    assertThat(composition).isNotSameAs(mockPredicateTwo);
    assertThat(composition.test("mock")).isTrue();

    verify(mockPredicateOne, times(1)).test(eq("mock"));
    verify(mockPredicateTwo, times(1)).test(eq("mock"));
    verifyNoMoreInteractions(mockPredicateOne, mockPredicateTwo);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composePredicatesWithOrShortCircuits() {

    Predicate<Object> mockPredicateOne = mockPredicate(true);
    Predicate<Object> mockPredicateTwo = mockPredicate(false);

    Predicate<Object> composition =
      FunctionUtils.composeOr(mockPredicateOne, null, mockPredicateTwo, null, null);

    assertThat(composition).isNotNull();
    assertThat(composition).isNotSameAs(mockPredicateOne);
    assertThat(composition).isNotSameAs(mockPredicateTwo);
    assertThat(composition.test("mock")).isTrue();

    verify(mockPredicateOne, times(1)).test(eq("mock"));
    verify(mockPredicateTwo, never()).test(any());
    verifyNoMoreInteractions(mockPredicateOne);
    verifyNoInteractions(mockPredicateTwo);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeSinglePredicatesWithOr() {

    Predicate<Object> mockPredicate = mockPredicate(true);
    Predicate<Object> composition = FunctionUtils.composeOr(null, mockPredicate, null);

    assertThat(composition).isNotNull();
    assertThat(composition).isNotSameAs(mockPredicate);
    assertThat(composition.test("mock")).isTrue();

    verify(mockPredicate, times(1)).test(eq("mock"));
    verifyNoMoreInteractions(mockPredicate);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void composeNoPredicatesWithOr() {

    Predicate<Object> predicate = FunctionUtils.composeOr();

    assertThat(predicate).isNotNull();
    assertThat(predicate.test("mock")).isFalse();
  }

  @Test
  public void composeNullPredicatesWithOrIsNullSafe3() {

    Predicate<Object> predicate = FunctionUtils.composeOr((Predicate<Object>[]) null);

    assertThat(predicate).isNotNull();
    assertThat(predicate.test("mock")).isFalse();
  }

  @Test
  public void noopConsumerIsCorrect() {

    User<?> mockUser = mock(User.class);

    Consumer<User<?>> userConsumer = FunctionUtils.noopConsumer();

    assertThat(userConsumer).isNotNull();

    userConsumer.accept(mockUser);

    verifyNoInteractions(mockUser);
  }

  @Test
  public void noopSupplierIsCorrect() {

    Supplier<User<?>> supplier = FunctionUtils.nullReturningSupplier();

    assertThat(supplier).isNotNull();
    assertThat(supplier.get()).isNull();
  }

  @Test
  public void nullSafeConsumerWithNonNullConsumer() {

    Consumer<?> mockConsumer = mock(Consumer.class);

    assertThat(FunctionUtils.nullSafeConsumer(mockConsumer)).isSameAs(mockConsumer);

    verifyNoInteractions(mockConsumer);
  }

  @Test
  public void nullSafeConsumerWithNullConsumer() {

    Consumer<?> consumer = FunctionUtils.nullSafeConsumer(null);

    assertThat(consumer).isNotNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nullSafeFunctionWithNonNullFunction() {

    Function<Object, Object> mockFunction = mock(Function.class);

    assertThat(FunctionUtils.nullSafeFunction(mockFunction)).isSameAs(mockFunction);

    verifyNoInteractions(mockFunction);
  }

  @Test
  public void nullSafeFunctionWithNullFunction() {

    Function<Object, Object> function = FunctionUtils.nullSafeFunction(null);

    assertThat(function).isNotNull();
    assertThat(function.apply("test")).isEqualTo("test");
  }

  @Test
  public void nullSafePredicateMatchAllWithNonNullPredicate() {

    Predicate<?> mockPredicate = mock(Predicate.class);

    assertThat(FunctionUtils.nullSafePredicateMatchingAll(mockPredicate)).isSameAs(mockPredicate);

    verifyNoInteractions(mockPredicate);
  }

  @Test
  public void nullSafePredicateMatchAllWithNullPredicate() {

    Predicate<Object> predicate = FunctionUtils.nullSafePredicateMatchingAll(null);

    assertThat(predicate).isNotNull();
    assertThat(predicate.test("test")).isTrue();
    assertThat(predicate.test("nil")).isTrue();
    assertThat(predicate.test("mock")).isTrue();
    assertThat(predicate.test(null)).isTrue();
  }

  @Test
  public void nullSafePredicateMatchNoneWithNonNullPredicate() {

    Predicate<?> mockPredicate = mock(Predicate.class);

    assertThat(FunctionUtils.nullSafePredicateMatchingNone(mockPredicate)).isSameAs(mockPredicate);

    verifyNoInteractions(mockPredicate);
  }

  @Test
  public void nullSafePredicateMatchNoneWithNullPredicate() {

    Predicate<Object> predicate = FunctionUtils.nullSafePredicateMatchingNone(null);

    assertThat(predicate).isNotNull();
    assertThat(predicate.test("test")).isFalse();
    assertThat(predicate.test("nil")).isFalse();
    assertThat(predicate.test("mock")).isFalse();
    assertThat(predicate.test(null)).isFalse();
  }

  @Test
  public void nullSafeSupplierWithNonNullSupplier() {

    Supplier<?> mockSupplier = mock(Supplier.class);

    assertThat(FunctionUtils.nullSafeSupplier(mockSupplier)).isSameAs(mockSupplier);

    verifyNoInteractions(mockSupplier);
  }

  @Test
  public void nullSafeSupplierWithNullSupplier() {

    Supplier<?> supplier = FunctionUtils.nullSafeSupplier(null);

    assertThat(supplier).isNotNull();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void toConsumerFromFunction() {

    Function<Object, Object> mockFunction = mock(Function.class);

    Consumer<Object> consumer = FunctionUtils.toConsumer(mockFunction);

    assertThat(consumer).isNotNull();

    consumer.accept("TEST");

    verify(mockFunction, times(1)).apply(eq("TEST"));
    verifyNoMoreInteractions(mockFunction);
  }

  @Test
  public void toConsumerFromNullFunction() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> FunctionUtils.toConsumer(null))
      .withMessage("Function is required")
      .withNoCause();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void toFunctionFromConsumer() {

    Consumer<Object> mockConsumer = mock(Consumer.class);

    Function<Object, Object> function = FunctionUtils.toFunction(mockConsumer);

    assertThat(function).isNotNull();
    assertThat(function.apply("TEST")).isEqualTo("TEST");

    verify(mockConsumer, times(1)).accept(eq("TEST"));
    verifyNoMoreInteractions(mockConsumer);
  }

  @Test
  public void toFunctionFromNullConsumer() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> FunctionUtils.toFunction((Consumer<?>) null))
      .withMessage("Consumer is required")
      .withNoCause();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void toFunctionFromPredicate() {

    Predicate<Object> mockPredicate = mock(Predicate.class);

    doReturn(true).when(mockPredicate).test(any());

    Function<Object, Boolean> function = FunctionUtils.toFunction(mockPredicate);

    assertThat(function).isNotNull();
    assertThat(function.apply("test")).isTrue();

    verify(mockPredicate, times(1)).test(eq("test"));
    verifyNoMoreInteractions(mockPredicate);
  }

  @Test
  @SuppressWarnings("all")
  public void toFunctionFromNullPredicate() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> FunctionUtils.toFunction((Predicate<?>) null))
      .withMessage("Predicate is required")
      .withNoCause();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void toFunctionFromSupplier() {

    Supplier<Object> mockSupplier = mock(Supplier.class);

    doReturn("TEST").when(mockSupplier).get();

    Function<Object, Object> function = FunctionUtils.toFunction(mockSupplier);

    assertThat(function).isNotNull();
    assertThat(function.apply(null)).isEqualTo("TEST");

    verify(mockSupplier, times(1)).get();
    verifyNoMoreInteractions(mockSupplier);
  }

  @Test
  public void toFunctionFromNullSupplier() {

    assertThatIllegalArgumentException()
      .isThrownBy(() ->FunctionUtils.toFunction((Supplier<?>) null))
      .withMessage("Supplier is required")
      .withNoCause();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void toPredicateFromFunction() {

    Function<Object, Boolean> mockFunction = mock(Function.class);

    doReturn(true).when(mockFunction).apply(any());

    Predicate<Object> predicate = FunctionUtils.toPredicate(mockFunction);

    assertThat(predicate).isNotNull();
    assertThat(predicate.test("test")).isTrue();
    assertThat(predicate.negate().test("mock")).isFalse();

    verify(mockFunction, times(1)).apply(eq("test"));
    verify(mockFunction, times(1)).apply(eq("mock"));
    verifyNoMoreInteractions(mockFunction);
  }

  @Test
  public void toPredicateFromNullFunction() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> FunctionUtils.toPredicate(null))
      .withMessage("Function is required")
      .withNoCause();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void toSupplierFromFunction() {

    Function<Object, Object> mockFunction = mock(Function.class);

    doReturn("TEST").when(mockFunction).apply(any());

    Supplier<Object> supplier = FunctionUtils.toSupplier(mockFunction);

    assertThat(supplier).isNotNull();
    assertThat(supplier.get()).isEqualTo("TEST");

    verify(mockFunction, times(1)).apply(isNull());
    verifyNoMoreInteractions(mockFunction);
  }

  @Test
  public void toSupplierFromNullFunction() {

    assertThatIllegalArgumentException()
      .isThrownBy(() ->FunctionUtils.toSupplier(null))
      .withMessage("Function is required")
      .withNoCause();
  }

  enum Par {

    THREE(3), FOUR(4), FIVE(5);

    static Par valueOf(int parValue) {

      return Arrays.stream(Par.values())
        .filter(par -> par.getValue() == parValue)
        .findFirst()
        .orElse(null);
    }

    final int value;

    Par(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }
}
