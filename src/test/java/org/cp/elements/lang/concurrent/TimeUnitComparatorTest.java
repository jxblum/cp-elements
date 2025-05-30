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
package org.cp.elements.lang.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link TimeUnitComparator}.
 *
 * @author John J. Blum
 * @see java.util.concurrent.TimeUnit
 * @see org.cp.elements.lang.concurrent.TimeUnitComparator
 * @see org.junit.jupiter.api.Test
 * @since 1.0.0
 */
class TimeUnitComparatorTest {

  @Test
  void compareTimeUnitsAllEqualTo() {

    assertThat(TimeUnitComparator.INSTANCE.compare(null, null)).isZero();
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.DAYS, TimeUnit.DAYS)).isZero();
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.HOURS, TimeUnit.HOURS)).isZero();
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MINUTES, TimeUnit.MINUTES)).isZero();
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.SECONDS, TimeUnit.SECONDS)).isZero();
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS)).isZero();
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MICROSECONDS, TimeUnit.MICROSECONDS)).isZero();
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.NANOSECONDS, TimeUnit.NANOSECONDS)).isZero();
  }

  @Test
  void compareTimeUnitsAllGreaterThan() {

    assertThat(TimeUnitComparator.INSTANCE.compare(null, TimeUnit.DAYS)).isGreaterThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.DAYS, TimeUnit.HOURS)).isGreaterThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.HOURS, TimeUnit.MINUTES)).isGreaterThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MINUTES, TimeUnit.SECONDS)).isGreaterThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.SECONDS, TimeUnit.MILLISECONDS)).isGreaterThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS)).isGreaterThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS)).isGreaterThan(0);
  }

  @Test
  void compareTimeUnitsAllLessThan() {

    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS)).isLessThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS)).isLessThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MILLISECONDS, TimeUnit.SECONDS)).isLessThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.SECONDS, TimeUnit.MINUTES)).isLessThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.MINUTES, TimeUnit.HOURS)).isLessThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.HOURS, TimeUnit.DAYS)).isLessThan(0);
    assertThat(TimeUnitComparator.INSTANCE.compare(TimeUnit.DAYS, null)).isLessThan(0);
  }
}
