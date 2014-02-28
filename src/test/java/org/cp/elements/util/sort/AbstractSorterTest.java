/*
 * Copyright (c) 2011-Present. Codeprimate, LLC and authors.  All Rights Reserved.
 * <p/>
 * This software is licensed under the Codeprimate End User License Agreement (EULA).
 * This software is proprietary and confidential in addition to an intellectual asset
 * of the aforementioned authors.
 * <p/>
 * By using the software, the end-user implicitly consents to and agrees to be in compliance
 * with all terms and conditions of the EULA.  Failure to comply with the EULA will result in
 * the maximum penalties permissible by law.
 * <p/>
 * In short, this software may not be reverse engineered, reproduced, copied, modified
 * or distributed without prior authorization of the aforementioned authors, permissible
 * and expressed only in writing.  The authors grant the end-user non-exclusive, non-negotiable
 * and non-transferable use of the software "as is" without expressed or implied WARRANTIES,
 * EXTENSIONS or CONDITIONS of any kind.
 * <p/>
 * For further information on the software license, the end user is encouraged to read
 * the EULA @ ...
 */

package org.cp.elements.util.sort;

import static org.cp.elements.test.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.cp.elements.test.AbstractMockingTestSuite;
import org.junit.After;
import org.junit.Test;

/**
 * The AbstractSorterTest class is a test suite of test cases testing the contract and functionality of the
 * AbstractSorter class.
 * <p/>
 * @author John J. Blum
 * @see org.cp.elements.test.AbstractMockingTestSuite
 * @see org.cp.elements.util.sort.AbstractSorter
 * @see org.junit.Test
 * @since 1.0.0
 */
public class AbstractSorterTest extends AbstractMockingTestSuite {

  @After
  public void tearDown() {
    AbstractSorter.ComparatorHolder.unset();
  }

  @Test
  public void testSetAndIsCustomComparatorAllowed() {
    AbstractSorter sorter = new TestSorter();

    assertEquals(AbstractSorter.DEFAULT_CUSTOM_COMPARATOR_ALLOWED, sorter.isCustomComparatorAllowed());

    sorter.setCustomComparatorAllowed(false);

    assertFalse(sorter.isCustomComparatorAllowed());

    sorter.setCustomComparatorAllowed(true);

    assertTrue(sorter.isCustomComparatorAllowed());
  }

  @Test
  public void testSetAndGetOrderBy() {
    AbstractSorter sorter = new TestSorter();
    Comparator mockOrderBy = mockContext.mock(Comparator.class);

    sorter.setOrderBy(mockOrderBy);

    assertSame(mockOrderBy, sorter.getOrderBy());

    sorter.setOrderBy(null);

    assertSame(AbstractSorter.ComparableComparator.INSTANCE, sorter.getOrderBy());
  }

  @Test
  public void testSetAndGetOrderByOfCallingThread() {
    AbstractSorter sorter = new TestSorter();

    Comparator mockSorterOrderBy = mockContext.mock(Comparator.class, "testSetAndGetOrderByOfCallingThread.Sorter");
    Comparator mockThreadOrderBy = mockContext.mock(Comparator.class, "testSetAndGetOrderByOfCallingThread.Thread");

    assertSame(AbstractSorter.ComparableComparator.INSTANCE, sorter.getOrderBy());

    sorter.setOrderBy(mockSorterOrderBy);

    assertSame(mockSorterOrderBy, sorter.getOrderBy());

    AbstractSorter.ComparatorHolder.set(mockThreadOrderBy);

    assertSame(mockThreadOrderBy, sorter.getOrderBy());

    sorter.setOrderBy(null);

    assertSame(mockThreadOrderBy, sorter.getOrderBy());

    AbstractSorter.ComparatorHolder.unset();

    assertSame(AbstractSorter.ComparableComparator.INSTANCE, sorter.getOrderBy());
  }

  @Test
  public void testSort() {
    TestSorter sorter = new TestSorter();
    String[] elements = { "tested", "test", "testing" };

    assertSame(elements, sorter.sort(elements));
    assertTrue(sorter.isSorted());
  }

  @Test
  public void testSwap() {
    List<String> elements = new ArrayList<String>(Arrays.asList("zero", "one", "two", "three"));

    assertEquals("one", elements.get(1));
    assertEquals("two", elements.get(2));

    new TestSorter().swap(elements, 1, 2);

    assertEquals("two", elements.get(1));
    assertEquals("one", elements.get(2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testComparableComparatorCompare() {
    assertEquals(0, AbstractSorter.ComparableComparator.INSTANCE.compare("test", "test"));
    assertPositive(AbstractSorter.ComparableComparator.INSTANCE.compare("testing", "test"));
    assertNegative(AbstractSorter.ComparableComparator.INSTANCE.compare("test", "tested"));
  }

  @Test
  public void testSortableArrayListGetSetAndSize() {
    String[] elements = { "test", "testing", "tested" };
    AbstractSorter.SortableArrayList<String> list = new AbstractSorter.SortableArrayList<String>(elements);

    assertEquals(elements.length, list.size());
    assertEquals("test", list.get(0));
    assertEquals("testing", list.get(1));
    assertEquals("tested", list.get(2));
    assertEquals("testing", list.set(1, "tester"));
    assertEquals("tester", list.get(1));
    assertEquals(elements.length, list.size());
  }

  @SuppressWarnings("unused")
  protected static final class TestSorter extends AbstractSorter {

    private boolean sorted = false;

    public boolean isSorted() {
      return sorted;
    }

    @Override
    public <E> List<E> sort(final List<E> elements) {
      sorted = true;
      return elements;
    }
  }

}
