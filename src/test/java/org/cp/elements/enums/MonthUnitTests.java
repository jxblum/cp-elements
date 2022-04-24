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
package org.cp.elements.enums;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import org.cp.elements.lang.StringUtils;
import org.junit.Test;

/**
 * Unit Tests for {@link Month}.
 *
 * @author John J. Blum
 * @see org.junit.Test
 * @see java.time.Month
 * @see java.util.Calendar
 * @see org.cp.elements.enums.Month
 * @since 1.0.0
 */
public class MonthUnitTests {

  private static final int[] CALENDAR_MONTHS = {
    Calendar.JANUARY,
    Calendar.FEBRUARY,
    Calendar.MARCH,
    Calendar.APRIL,
    Calendar.MAY,
    Calendar.JUNE,
    Calendar.JULY,
    Calendar.AUGUST,
    Calendar.SEPTEMBER,
    Calendar.OCTOBER,
    Calendar.NOVEMBER,
    Calendar.DECEMBER
  };

  @Test
  public void valueOfReturnsMonthEnum() {

    AtomicInteger index = new AtomicInteger(0);

    Arrays.stream(Month.values()).forEach(month -> {
      assertThat(Month.valueOf(month.name())).isEqualTo(month);
      assertThat(month.ordinal()).isEqualTo(index.get());
      assertThat(month.getAbbreviation()).isEqualTo(StringUtils.capitalize(month.name().toLowerCase().substring(0, 3)));
      assertThat(month.getCalendarMonth()).isEqualTo(CALENDAR_MONTHS[index.get()]);
      assertThat(month.getJavaTimeMonth()).isEqualTo(java.time.Month.values()[index.get()]);
      assertThat(month.getName()).isEqualTo(StringUtils.capitalize(month.name().toLowerCase()));
      assertThat(month.getPosition()).isEqualTo(index.incrementAndGet());
    });
  }

  @Test
  public void valueOfAbbreviationReturnsMonthEnum() {

    Arrays.stream(Month.values()).forEach(month ->
      assertThat(Month.valueOfAbbreviation(month.getAbbreviation())).isEqualTo(month));
  }

  @Test
  public void valueOfAbbreviationIgnoresCaseAndReturnsMonthEnum() {

    assertThat(Month.valueOfAbbreviation("jan")).isEqualTo(Month.JANUARY);
    assertThat(Month.valueOfAbbreviation("May")).isEqualTo(Month.MAY);
    assertThat(Month.valueOfAbbreviation("AUG")).isEqualTo(Month.AUGUST);
  }

  @Test
  public void valueOfInvalidAbbreviationReturnsNull() {

    assertThat(Month.valueOfAbbreviation("")).isNull();
    assertThat(Month.valueOfAbbreviation("  ")).isNull();
    assertThat(Month.valueOfAbbreviation("nil")).isNull();
    assertThat(Month.valueOfAbbreviation("null")).isNull();
    assertThat(Month.valueOfAbbreviation("Maybe")).isNull();
    assertThat(Month.valueOfAbbreviation("June")).isNull();
    assertThat(Month.valueOfAbbreviation("Sept")).isNull();
  }

  @Test
  public void valueOfNullAbbreviationIsNullSafeReturnsNull() {
    assertThat(Month.valueOfAbbreviation(null)).isNull();
  }

  @Test
  public void valueOfCalendarMonthReturnsMonthEnum() {

    AtomicInteger index = new AtomicInteger(0);

    Arrays.stream(Month.values()).forEach(month -> {
      assertThat(Month.valueOfCalendarMonth(month.getCalendarMonth())).isEqualTo(month);
      assertThat(Month.valueOfCalendarMonth(CALENDAR_MONTHS[index.getAndIncrement()])).isEqualTo(month);
    });

    assertThat(index.get()).isEqualTo(12);
  }

  @Test
  public void valueOfInvalidCalendarMonthReturnsNull() {

    assertThat(Month.valueOfCalendarMonth(-11)).isNull();
    assertThat(Month.valueOfCalendarMonth(12)).isNull();
    assertThat(Month.valueOfCalendarMonth(13)).isNull();
  }

  @Test
  public void valueOfJavaTimeMonthReturnsMonthEnum() {

    AtomicInteger index = new AtomicInteger(0);

    Arrays.stream(Month.values()).forEach(month -> {
      assertThat(Month.valueOfJavaTimeMonth(month.getJavaTimeMonth())).isEqualTo(month);
      assertThat(Month.valueOfJavaTimeMonth(java.time.Month.values()[index.getAndIncrement()])).isEqualTo(month);
    });

    assertThat(index.get()).isEqualTo(12);
  }

  @Test
  public void valueOfNullJavaTimeMonthIsNullSafeAndReturnsNull() {
    assertThat(Month.valueOfJavaTimeMonth(null)).isNull();
  }

  @Test
  public void valueOfNameReturnsMonth() {

    Arrays.stream(Month.values()).forEach(month ->
      assertThat(Month.valueOfName(month.getName())).isEqualTo(month));
  }

  @Test
  public void valueOfNameIgnoresCaseAndReturnsMonthEnum() {

    assertThat(Month.valueOfName("january")).isEqualTo(Month.JANUARY);
    assertThat(Month.valueOfName("May")).isEqualTo(Month.MAY);
    assertThat(Month.valueOfName("AUGUST")).isEqualTo(Month.AUGUST);
  }

  @Test
  public void valueOfInvalidNamesReturnsNull() {

    assertThat(Month.valueOfName("")).isNull();
    assertThat(Month.valueOfName("  ")).isNull();
    assertThat(Month.valueOfName("nil")).isNull();
    assertThat(Month.valueOfName("null")).isNull();
    assertThat(Month.valueOfName("Jun")).isNull();
    assertThat(Month.valueOfName("Julie")).isNull();
    assertThat(Month.valueOfName("Octobre")).isNull();
    assertThat(Month.valueOfName("Daycember")).isNull();
  }

  @Test
  public void valueOfNullNameIsNullSafeReturnsNull() {
    assertThat(Month.valueOfName(null)).isNull();
  }

  @Test
  public void valueOfPositionReturnsMonthEnum() {

    AtomicInteger position = new AtomicInteger(0);

    Arrays.stream(Month.values()).forEach(month ->
      assertThat(Month.valueOfPosition(position.incrementAndGet())).isEqualTo(month));

    assertThat(position.get()).isEqualTo(12);
  }

  @Test
  public void valueOfInvalidPositionReturnsNull() {

    assertThat(Month.valueOfPosition(0)).isNull();
    assertThat(Month.valueOfPosition(-7)).isNull();
    assertThat(Month.valueOfPosition(13)).isNull();
  }
}