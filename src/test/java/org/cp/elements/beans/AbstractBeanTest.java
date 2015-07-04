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

package org.cp.elements.beans;

import static org.cp.elements.test.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import org.cp.elements.beans.event.ChangeEvent;
import org.cp.elements.beans.event.ChangeListener;
import org.cp.elements.lang.DateTimeUtils;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.RelationalOperator;
import org.cp.elements.lang.StringUtils;
import org.cp.elements.lang.Visitor;
import org.cp.elements.test.TestUtils;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * The AbstractBeanTest class is a test suite of test cases testing the contract and functionality
 * of the AbstractBean class.
 *
 * @author John J. Blum
 * @see org.cp.elements.beans.AbstractBean
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @since 1.0.0
 */
public class AbstractBeanTest {

  protected void assertEvent(final ChangeEvent event, final Object expectedSource, final Calendar expectedChangeDateTime) {
    assertNotNull("The actual 'ChangeEvent' must not be null!", event);
    assertSame(expectedSource, event.getSource());
    assertEquals(DateTimeUtils.truncate(expectedChangeDateTime),
      DateTimeUtils.truncate(event.getChangeDateTime()));
  }

  protected void assertEvent(final PropertyChangeEvent event,
                             final Object expectedSource,
                             final String expectedPropertyName)
  {
    assertNotNull("The actual 'PropertyChangeEvent' must not be null!", event);
    assertSame(expectedSource, event.getSource());
    assertEquals(expectedPropertyName, event.getPropertyName());
  }

  protected void assertEvent(final PropertyChangeEvent event,
                             final Object expectedSource,
                             final String expectedPropertyName,
                             final Object expectedOldValue,
                             final Object expectedNewValue)
  {
    assertNotNull("The actual 'PropertyChangeEvent' must not be null!", event);
    assertSame(expectedSource, event.getSource());
    assertNullEquals(expectedPropertyName, event.getPropertyName());
    assertNullEquals(expectedOldValue, event.getOldValue());
    assertNullEquals(expectedNewValue, event.getNewValue());
  }

  @SuppressWarnings("unused")
  protected void log(final PropertyChangeEvent event) {
    System.out.printf("{ source = %1$s, propertyName = %2$s, oldValue = %3$s, newValue = %4$s }%n",
      event.getSource(), event.getPropertyName(), event.getOldValue(), event.getNewValue());
  }

  @Test
  public void createAbstractBean() {
    TestBean<Long> bean = new TestBean<Long>();

    assertNotNull(bean);
    assertNull(bean.getId());
  }

  @Test
  public void createAbstractBeanWithId() {
    TestBean<Long> bean = new TestBean<Long>(1l);

    assertNotNull(bean);
    assertEquals(1l, bean.getId().longValue());
  }

  @Test
  public void setAndGetId() {
    TestBean<Long> bean = new TestBean<Long>();

    assertNull(bean.getId());

    bean.setId(1l);

    assertEquals(1l, bean.getId().longValue());

    bean.setId(0l);

    assertEquals(0l, bean.getId().longValue());

    bean.setId(-1l);

    assertEquals(-1l, bean.getId().longValue());

    bean.setId(null);

    assertNull(bean.getId());
  }

  @Test
  public void setAndGetCreatedProperties() {
    Calendar expectedCreatedDateTime = TestUtils.createCalendar(2015, Calendar.FEBRUARY, 10);

    TestBean<Long> bean = new TestBean<Long>();

    assertNull(bean.getCreatedBy());
    assertNull(bean.getCreatedDateTime());
    assertNull(bean.getCreatingProcess());

    bean.setCreatedBy("jonBloom");
    bean.setCreatedDateTime(expectedCreatedDateTime);
    bean.setCreatingProcess("java");

    assertEquals("jonBloom", bean.getCreatedBy());
    assertEquals(expectedCreatedDateTime, bean.getCreatedDateTime());
    assertNotSame(expectedCreatedDateTime, bean.getCreatedDateTime());
    assertEquals("java", bean.getCreatingProcess());
  }

  @Test
  public void immutableCreatedDateTime() {
    Calendar expectedCreatedDateTime = TestUtils.createCalendar(2015, Calendar.FEBRUARY, 10);
    Calendar clonedCreatedDateTime = (Calendar) expectedCreatedDateTime.clone();

    assertNotSame(expectedCreatedDateTime, clonedCreatedDateTime);

    TestBean<Long> bean = new TestBean<Long>();

    assertNull(bean.getCreatedDateTime());

    bean.setCreatedDateTime(clonedCreatedDateTime);

    clonedCreatedDateTime.set(Calendar.YEAR, 2014);
    clonedCreatedDateTime.set(Calendar.DAY_OF_MONTH, 11);

    Calendar actualCreatedDateTime = bean.getCreatedDateTime();

    assertNotSame(clonedCreatedDateTime, actualCreatedDateTime);
    assertNotEquals(clonedCreatedDateTime, actualCreatedDateTime);
    assertNotSame(expectedCreatedDateTime, actualCreatedDateTime);
    assertEquals(expectedCreatedDateTime, actualCreatedDateTime);

    actualCreatedDateTime.set(Calendar.MONTH, Calendar.NOVEMBER);
    actualCreatedDateTime.set(Calendar.DAY_OF_MONTH, 11);

    assertNotSame(actualCreatedDateTime, bean.getCreatedDateTime());
    assertNotEquals(actualCreatedDateTime, bean.getCreatedDateTime());
    assertNotSame(expectedCreatedDateTime, bean.getCreatedDateTime());
    assertEquals(expectedCreatedDateTime, bean.getCreatedDateTime());
  }

  @Test
  public void setAndIsEventDispatchEnabled() {
    TestBean<Long> bean = new TestBean<Long>();

    assertEquals(AbstractBean.DEFAULT_EVENT_DISPATCH_ENABLED, bean.isEventDispatchEnabled());

    bean.setEventDispatchEnabled(false);

    assertFalse(bean.isEventDispatchEnabled());

    bean.setEventDispatchEnabled(true);

    assertTrue(bean.isEventDispatchEnabled());
  }

  @Test
  public void isModified() {
    ValueHolder<String> obj = new ValueHolder<String>();

    assertFalse(obj.isModified());
    assertFalse(obj.isModified("id"));
    assertFalse(obj.isModified("value"));

    obj.setValue("test");

    assertTrue(obj.isModified());
    assertFalse(obj.isModified("id"));
    assertTrue(obj.isModified("value"));

    obj.setId(1l);
    obj.setValue(null);

    assertTrue(obj.isModified());
    assertTrue(obj.isModified("id"));
    assertFalse(obj.isModified("value"));

    obj.setId(null);

    assertFalse(obj.isModified());
    assertFalse(obj.isModified("id"));
    assertFalse(obj.isModified("value"));
  }

  @Test
  public void setAndGetModifiedProperties() {
    Calendar expectedModifiedDateTime = TestUtils.createCalendar(2015, Calendar.FEBRUARY, 11);
    Calendar expectedUpdatedModifiedDateTime = TestUtils.createCalendar(2015, Calendar.FEBRUARY, 12);

    TestBean<Long> bean = new TestBean<Long>();

    assertNull(bean.getModifiedBy());
    assertNull(bean.getModifiedDateTime());
    assertNull(bean.getModifyingProcess());
    assertNull(bean.getLastModifiedBy());
    assertNull(bean.getLastModifiedDateTime());
    assertNull(bean.getLastModifyingProcess());

    bean.setModifiedBy("sarahBloom");
    bean.setModifiedDateTime(expectedModifiedDateTime);
    bean.setModifyingProcess("myApp");

    assertEquals("sarahBloom", bean.getModifiedBy());
    assertEquals(expectedModifiedDateTime, bean.getModifiedDateTime());
    assertNotSame(expectedModifiedDateTime, bean.getModifiedDateTime());
    assertEquals("myApp", bean.getModifyingProcess());
    assertEquals("sarahBloom", bean.getLastModifiedBy());
    assertEquals(expectedModifiedDateTime, bean.getLastModifiedDateTime());
    assertNotSame(expectedModifiedDateTime, bean.getLastModifiedDateTime());
    assertEquals("myApp", bean.getLastModifyingProcess());

    bean.setModifiedBy("leBloom");
    bean.setModifiedDateTime(expectedUpdatedModifiedDateTime);
    bean.setModifyingProcess("testApp");

    assertEquals("leBloom", bean.getModifiedBy());
    assertEquals(expectedUpdatedModifiedDateTime, bean.getModifiedDateTime());
    assertNotSame(expectedUpdatedModifiedDateTime, bean.getModifiedDateTime());
    assertEquals("testApp", bean.getModifyingProcess());
    assertEquals("sarahBloom", bean.getLastModifiedBy());
    assertEquals(expectedModifiedDateTime, bean.getLastModifiedDateTime());
    assertNotSame(expectedModifiedDateTime, bean.getLastModifiedDateTime());
    assertEquals("myApp", bean.getLastModifyingProcess());
  }

  @Test
  public void immutableModifiedDateTime() {
    Calendar expectedModifiedDateTime = TestUtils.createCalendar(2015, Calendar.FEBRUARY, 10);
    Calendar clonedModifiedDateTime = (Calendar) expectedModifiedDateTime.clone();

    assertNotSame(expectedModifiedDateTime, clonedModifiedDateTime);

    TestBean<Long> bean = new TestBean<Long>();

    assertNull(bean.getModifiedDateTime());

    bean.setModifiedDateTime(clonedModifiedDateTime);

    clonedModifiedDateTime.set(Calendar.YEAR, 2014);
    clonedModifiedDateTime.set(Calendar.DAY_OF_MONTH, 11);

    Calendar actualModifiedDateTime = bean.getModifiedDateTime();

    assertNotSame(clonedModifiedDateTime, actualModifiedDateTime);
    assertNotEquals(clonedModifiedDateTime, actualModifiedDateTime);
    assertNotSame(expectedModifiedDateTime, actualModifiedDateTime);
    assertEquals(expectedModifiedDateTime, actualModifiedDateTime);

    actualModifiedDateTime.set(Calendar.MONTH, Calendar.NOVEMBER);
    actualModifiedDateTime.set(Calendar.DAY_OF_MONTH, 11);

    assertNotSame(actualModifiedDateTime, bean.getModifiedDateTime());
    assertNotEquals(actualModifiedDateTime, bean.getModifiedDateTime());
    assertNotSame(expectedModifiedDateTime, bean.getModifiedDateTime());
    assertEquals(expectedModifiedDateTime, bean.getModifiedDateTime());

    Calendar actualLastModifiedDateTime = bean.getLastModifiedDateTime();

    assertNotSame(expectedModifiedDateTime, actualLastModifiedDateTime);
    assertEquals(expectedModifiedDateTime, actualLastModifiedDateTime);

    actualLastModifiedDateTime.set(Calendar.YEAR, 2011);
    actualLastModifiedDateTime.set(Calendar.MONTH, Calendar.OCTOBER);
    actualLastModifiedDateTime.set(Calendar.DAY_OF_MONTH, 3);

    assertNotSame(actualLastModifiedDateTime, bean.getLastModifiedDateTime());
    assertNotEquals(actualLastModifiedDateTime, bean.getLastModifiedDateTime());
    assertNotSame(expectedModifiedDateTime, bean.getLastModifiedDateTime());
    assertEquals(expectedModifiedDateTime, bean.getLastModifiedDateTime());
  }

  @Test
  public void isNew() {
    TestBean<Long> bean = new TestBean<Long>();

    assertTrue(bean.isNew());

    bean.setId(1l);

    assertFalse(bean.isNew());

    bean.setId(null);

    assertTrue(bean.isNew());
  }

  @Test
  public void propertyNameToFieldNameMapping() {
    TestBean<Long> bean = new TestBean<Long>();

    assertEquals("lastName", bean.getFieldName("lastName"));
    assertTrue(bean.mapPropertyNameToFieldName("lastName", "surname"));
    assertEquals("surname", bean.getFieldName("lastName"));
    assertEquals("surname", bean.getFieldName("surname"));
    assertEquals("givenName", bean.getFieldName("givenName"));
    assertTrue(bean.mapPropertyNameToFieldName("givenName", "firstName"));
    assertEquals("firstName", bean.getFieldName("givenName"));
    assertEquals("firstName", bean.getFieldName("firstName"));
    assertEquals("dateOfBirth", bean.getFieldName("dateOfBirth"));
    assertTrue(bean.mapPropertyNameToFieldName("dateOfBirth", "birthDate"));
    assertEquals("birthDate", bean.getFieldName("dateOfBirth"));
    assertEquals("surname", bean.getFieldName("lastName"));
    assertFalse(bean.mapPropertyNameToFieldName("lastName", "  "));
    assertEquals("lastName", bean.getFieldName("lastName"));
    assertEquals("firstName", bean.getFieldName("givenName"));
    assertFalse(bean.mapPropertyNameToFieldName("givenName", StringUtils.EMPTY_STRING));
    assertEquals("givenName", bean.getFieldName("givenName"));
    assertEquals("birthDate", bean.getFieldName("dateOfBirth"));
    assertFalse(bean.mapPropertyNameToFieldName("dateOfBirth", null));
    assertEquals("dateOfBirth", bean.getFieldName("dateOfBirth"));
  }

  @Test
  public void accept() {
    TestBean<Long> bean = new TestBean<Long>();
    Visitor mockVisitor = mock(Visitor.class, "testAccept.MockVisitor");

    bean.accept(mockVisitor);

    verify(mockVisitor, times(1)).visit(same(bean));
  }

  @Test
  public void addRemoveChangeListener() {
    final AtomicReference<ChangeEvent> changeEventReference = new AtomicReference<ChangeEvent>(null);

    ChangeListener mockChangeListener = mock(ChangeListener.class, "testAddRemoveChangeListener.MockChangeListener");

    doAnswer(new Answer<Void>() {
      @Override public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        changeEventReference.compareAndSet(null, invocationOnMock.getArgumentAt(0, ChangeEvent.class));
        return null;
      }
    }).when(mockChangeListener).stateChanged(any(ChangeEvent.class));

    ValueHolder<Object> valueHolder = new ValueHolder<Object>();

    assertNull(changeEventReference.get());
    assertNull(valueHolder.getValue());

    valueHolder.setValue("test");

    assertEquals("test", valueHolder.getValue());
    assertNull(changeEventReference.get());

    valueHolder.addChangeListener(mockChangeListener);
    valueHolder.setValue("testing");

    assertEquals("testing", valueHolder.getValue());
    assertEvent(changeEventReference.get(), valueHolder, Calendar.getInstance());

    changeEventReference.set(null);
    valueHolder.removeChangeListener(mockChangeListener);
    valueHolder.setValue("tested");

    assertEquals("tested", valueHolder.getValue());
    assertNull(changeEventReference.get());

    verify(mockChangeListener, times(1)).stateChanged(any(ChangeEvent.class));
  }

  @Test
  public void addRemovePropertyChangeListener() {
    final AtomicReference<PropertyChangeEvent> propertyChangeEventReference =
      new AtomicReference<PropertyChangeEvent>(null);

    PropertyChangeListener mockPropertyChangeListener = mock(PropertyChangeListener.class,
      "testAddRemovePropertyChangeListener.MockPropertyChangeListener");

    doAnswer(new Answer<Void>() {
      @Override public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        propertyChangeEventReference.compareAndSet(null, invocationOnMock.getArgumentAt(0, PropertyChangeEvent.class));
        return null;
      }
    }).when(mockPropertyChangeListener).propertyChange(any(PropertyChangeEvent.class));

    ValueHolder<Object> valueHolder = new ValueHolder<Object>();

    assertNull(propertyChangeEventReference.get());
    assertNull(valueHolder.getId());
    assertNull(valueHolder.getValue());

    valueHolder.addPropertyChangeListener(mockPropertyChangeListener);
    valueHolder.setValue("test");

    assertEquals("test", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", null, "test");

    propertyChangeEventReference.set(null);
    valueHolder.setId(1l);

    assertEquals(1l, valueHolder.getId().longValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "id", null, 1l);

    propertyChangeEventReference.set(null);
    valueHolder.setValue("testing");

    assertEquals("testing", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", "test", "testing");

    propertyChangeEventReference.set(null);
    valueHolder.removePropertyChangeListener(mockPropertyChangeListener);
    valueHolder.setId(42l);
    valueHolder.setValue("tested");

    assertEquals(42l, valueHolder.getId().longValue());
    assertEquals("tested", valueHolder.getValue());
    assertNull(propertyChangeEventReference.get());

    verify(mockPropertyChangeListener, times(3)).propertyChange(any(PropertyChangeEvent.class));
  }

  @Test
  public void addRemovePropertyChangeListenerForProperty() {
    final AtomicReference<PropertyChangeEvent> propertyChangeEventReference =
      new AtomicReference<PropertyChangeEvent>(null);

    PropertyChangeListener mockPropertyChangeListener = mock(PropertyChangeListener.class,
      "testAddRemovePropertyChangeListenerForProperty.MockPropertyChangeListener");

    doAnswer(new Answer<Void>() {
      @Override public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        propertyChangeEventReference.compareAndSet(null, invocationOnMock.getArgumentAt(0, PropertyChangeEvent.class));
        return null;
      }
    }).when(mockPropertyChangeListener).propertyChange(any(PropertyChangeEvent.class));

    ValueHolder<Object> valueHolder = new ValueHolder<Object>();

    assertNull(propertyChangeEventReference.get());
    assertNull(valueHolder.getId());
    assertNull(valueHolder.getValue());

    valueHolder.addPropertyChangeListener("value", mockPropertyChangeListener);
    valueHolder.setValue("test");

    assertEquals("test", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", null, "test");

    propertyChangeEventReference.set(null);
    valueHolder.setId(1l);

    assertEquals(1l, valueHolder.getId().longValue());
    assertNull(propertyChangeEventReference.get());

    valueHolder.setValue("testing");
    valueHolder.setId(42l);

    assertEquals(42l, valueHolder.getId().longValue());
    assertEquals("testing", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", "test", "testing");

    propertyChangeEventReference.set(null);
    valueHolder.removePropertyChangeListener("value", mockPropertyChangeListener);
    valueHolder.setId(69l);
    valueHolder.setValue("tested");

    assertEquals(69l, valueHolder.getId().longValue());
    assertEquals("tested", valueHolder.getValue());
    assertNull(propertyChangeEventReference.get());

    verify(mockPropertyChangeListener, times(2)).propertyChange(any(PropertyChangeEvent.class));
  }

  @Test
  public void addRemoveVetoableChangeListener() throws Exception {
    final AtomicReference<PropertyChangeEvent> propertyChangeEventReference =
      new AtomicReference<PropertyChangeEvent>(null);

    ValueHolder<Object> valueHolder = new ValueHolder<Object>();

    VetoableChangeListener mockVetoableChangeListener =
      mock(VetoableChangeListener.class, "testAddRemoveVetoableChangeListener.MockVetoableChangeListener");

    doAnswer(new Answer<Void>() {
      @Override public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        propertyChangeEventReference.compareAndSet(null, invocationOnMock.getArgumentAt(0, PropertyChangeEvent.class));
        return null;
      }
    }).when(mockVetoableChangeListener).vetoableChange(any(PropertyChangeEvent.class));

    assertNull(propertyChangeEventReference.get());
    assertNull(valueHolder.getId());
    assertNull(valueHolder.getValue());

    valueHolder.addVetoableChangeListener(mockVetoableChangeListener);
    valueHolder.setValue("test");

    assertEquals("test", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", null, "test");

    propertyChangeEventReference.set(null);
    valueHolder.setId(1l);

    assertEquals(1l, valueHolder.getId().longValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "id", null, 1l);

    propertyChangeEventReference.set(null);
    valueHolder.setValue("testing");

    assertEquals("testing", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", "test", "testing");

    propertyChangeEventReference.set(null);
    valueHolder.removeVetoableChangeListener(mockVetoableChangeListener);
    valueHolder.setId(42l);
    valueHolder.setValue("tested");

    assertEquals(42l, valueHolder.getId().longValue());
    assertEquals("tested", valueHolder.getValue());
    assertNull(propertyChangeEventReference.get());

    verify(mockVetoableChangeListener, times(3)).vetoableChange(any(PropertyChangeEvent.class));
  }

  @Test
  public void addRemoveVetoableChangeListenerForProperty() throws Exception {
    final AtomicReference<PropertyChangeEvent> propertyChangeEventReference =
      new AtomicReference<PropertyChangeEvent>(null);

    ValueHolder<Object> valueHolder = new ValueHolder<Object>();

    VetoableChangeListener mockVetoableChangeListener = mock(VetoableChangeListener.class,
      "testAddRemoveVetoableChangeListenerForProperty.MockVetoableChangeListener");

    doAnswer(new Answer<Void>() {
      @Override public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        propertyChangeEventReference.compareAndSet(null, invocationOnMock.getArgumentAt(0, PropertyChangeEvent.class));
        return null;
      }
    }).when(mockVetoableChangeListener).vetoableChange(any(PropertyChangeEvent.class));

    assertNull(propertyChangeEventReference.get());
    assertNull(valueHolder.getId());
    assertNull(valueHolder.getValue());

    valueHolder.addVetoableChangeListener("value", mockVetoableChangeListener);
    valueHolder.setValue("test");

    assertEquals("test", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", null, "test");

    propertyChangeEventReference.set(null);
    valueHolder.setId(1l);

    assertEquals(1l, valueHolder.getId().longValue());
    assertNull(propertyChangeEventReference.get());

    valueHolder.setValue("testing");
    valueHolder.setId(42l);

    assertEquals(42l, valueHolder.getId().longValue());
    assertEquals("testing", valueHolder.getValue());
    assertEvent(propertyChangeEventReference.get(), valueHolder, "value", "test", "testing");

    propertyChangeEventReference.set(null);
    valueHolder.removeVetoableChangeListener("value", mockVetoableChangeListener);
    valueHolder.setId(69l);
    valueHolder.setValue("tested");

    assertEquals(69l, valueHolder.getId().longValue());
    assertEquals("tested", valueHolder.getValue());
    assertNull(propertyChangeEventReference.get());

    verify(mockVetoableChangeListener, times(2)).vetoableChange(any(PropertyChangeEvent.class));
  }

  @Test
  public void changeState() {
    ValueHolder<Object> valueHolder = new ValueHolder<Object>();

    assertNull(valueHolder.getValue());

    valueHolder.changeState("value", "test");

    assertEquals("test", valueHolder.getValue());
  }

  @Test(expected = PropertyNotFoundException.class)
  public void changeStateOfNonExistingProperty() {
    try {
      new ValueHolder<Object>().changeState("nonExistingProperty", "test");
    }
    catch (PropertyNotFoundException expected) {
      assertEquals(String.format(
        "The property (nonExistingProperty) corresponding to field (nonExistingProperty) was not found on this Bean (%1$s)!",
          ValueHolder.class.getName()), expected.getMessage());
      assertTrue(expected.getCause() instanceof IllegalArgumentException);
      throw expected;
    }
  }

  @Test
  public void compareTo() {
    TestBean<Long> beanOne = new TestBean<Long>(1l);
    TestBean<Long> beanTwo = new TestBean<Long>(2l);
    TestBean<Long> beanZero = new TestBean<Long>();

    assertNegative(beanOne.compareTo(beanTwo));
    assertZero(beanOne.compareTo(beanOne));
    assertPositive(beanTwo.compareTo(beanOne));
    assertNegative(beanTwo.compareTo(beanZero));
  }

  @Test(expected = ClassCastException.class)
  public void compareToNull() {
    try {
      new TestBean<Long>(1l).compareTo(null);
    }
    catch (ClassCastException expected) {
      assertEquals(String.format("The Bean to compare with this Bean must be an instance of %1$s!",
        TestBean.class.getName()), expected.getMessage());
      throw expected;
    }
  }

  @Test(expected = ClassCastException.class)
  public void compareIncompatibleTypes() {
    try {
      new TestBean<Long>(1l).compareTo(new ValueHolder<Object>(1l));
    }
    catch (ClassCastException expected) {
      assertEquals(String.format("The Bean to compare with this Bean must be an instance of %1$s!",
        TestBean.class.getName()), expected.getMessage());
      throw expected;
    }
  }

  @Test
  public void equals() {
    TestBean<Long> beanOne = new TestBean<Long>(1l);
    TestBean<Long> beanTwo = new TestBean<Long>(2l);
    TestBean<Long> beanThree = new TestBean<Long>(1l);

    assertTrue(beanOne.equals(beanOne));
    assertFalse(beanOne.equals(beanTwo));
    assertTrue(beanOne.equals(beanThree));
    assertFalse(beanOne.equals(new Object()));
    assertFalse(beanOne.equals(Bean.class.cast(null)));
  }

  @Test
  public void testNoVetoChangeWhenEventDispatchDisabled() throws Exception {
    final ValueHolder<Object> valueHolder = new ValueHolder<Object>();

    VetoableChangeListener mockVetoableChangeListener = mock(VetoableChangeListener.class,
      "testNoVetoChangeWhenEventDispatchDisabled.MockVetoableChangeListener");

    doAnswer(new Answer<Void>() {
      @Override public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        PropertyChangeEvent event = invocationOnMock.getArgumentAt(0, PropertyChangeEvent.class);
        //log(event);
        //Thread.dumpStack();
        throw new PropertyVetoException(String.format("value (%1$s) is invalid", event.getNewValue()), event);
      }
    }).when(mockVetoableChangeListener).vetoableChange(any(PropertyChangeEvent.class));

    valueHolder.addVetoableChangeListener("value", mockVetoableChangeListener);

    assertTrue(valueHolder.isEventDispatchEnabled());
    assertNull(valueHolder.getValue());

    try {
      valueHolder.setValue("test");
      fail("'test' is not valid");
    }
    catch (IllegalPropertyValueException expected) {
      assertTrue(expected.getCause() instanceof PropertyVetoException);
      assertEquals("value (test) is invalid", expected.getCause().getMessage());
    }
    finally {
      assertNull(valueHolder.getValue());
    }

    valueHolder.setEventDispatchEnabled(false);
    valueHolder.setValue("testing");

    assertFalse(valueHolder.isEventDispatchEnabled());
    assertEquals("testing", valueHolder.getValue());

    valueHolder.setValue("tested");

    assertFalse(valueHolder.isEventDispatchEnabled());
    assertEquals("tested", valueHolder.getValue());

    verify(mockVetoableChangeListener, times(2)).vetoableChange(any(PropertyChangeEvent.class));
  }

  @Test
  public void testVetoChange() throws Exception {
    final ValueHolder<Integer> teenageYears = new ValueHolder<Integer>();

    VetoableChangeListener mockVetoableChangeListener = mock(VetoableChangeListener.class,
      "testVetoChange.MockVetoableChangeListener");

    doAnswer(new Answer<Void>() {
      @Override public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        PropertyChangeEvent event = invocationOnMock.getArgumentAt(0, PropertyChangeEvent.class);
        assertEvent(event, teenageYears, "value");
        Integer value = (Integer) event.getNewValue();

        if (value != null && RelationalOperator.lessThanOrGreaterThan(13, 19).evaluate(value)) {
          throw new PropertyVetoException(String.format("value (%1$s) is invalid", value), event);
        }

        return null;
      }
    }).when(mockVetoableChangeListener).vetoableChange(any(PropertyChangeEvent.class));

    teenageYears.addVetoableChangeListener("value", mockVetoableChangeListener);

    assertTrue(teenageYears.isEventDispatchEnabled());
    assertNull(teenageYears.getValue());

    teenageYears.setValue(16);

    assertEquals(16, teenageYears.getValue().intValue());

    try {
      teenageYears.setValue(21);
      fail("'21' is not valid");
    }
    catch (IllegalPropertyValueException expected) {
      assertTrue(expected.getCause() instanceof PropertyVetoException);
      assertEquals("value (21) is invalid", expected.getCause().getMessage());
    }
    finally {
      assertEquals(16, teenageYears.getValue().intValue());
    }

    teenageYears.setValue(19);

    assertEquals(19, teenageYears.getValue().intValue());

    verify(mockVetoableChangeListener, times(4)).vetoableChange(any(PropertyChangeEvent.class));
  }

  @Test
  public void testCreatePropertyChangeEvent() {
    TestBean<Long> bean = new TestBean<Long>();

    bean.addPropertyChangeListener(mock(PropertyChangeListener.class,
      "testCreatePropertyChangeEvent.MockPropertyChangeListener"));

    bean.addVetoableChangeListener(mock(VetoableChangeListener.class,
      "testCreatePropertyChangeEvent.MockVetoableChagneListener"));

    assertTrue(bean.isEventDispatchEnabled());

    PropertyChangeEvent event = bean.createPropertyChangeEvent("id", null, 1l);

    assertEvent(event, bean, "id", null, 1l);
  }

  @Test
  public void testCreatePropertyChangeEventWithNoVetoableChangeListener() {
    TestBean<Long> bean = new TestBean<Long>();

    bean.addPropertyChangeListener(mock(PropertyChangeListener.class,
      "testCreatePropertyChangeEventWithNoVetoableChangeListener.MockPropertyChangeListener"));

    assertTrue(bean.isEventDispatchEnabled());

    PropertyChangeEvent event = bean.createPropertyChangeEvent("id", 1l, 2l);

    assertEvent(event, bean, "id", 1l, 2l);
  }

  @Test
  public void testCreatePropertyChangeEventWithNoPropertyChangeListener() {
    TestBean<Long> bean = new TestBean<Long>();

    bean.addVetoableChangeListener(mock(VetoableChangeListener.class,
      "testCreatePropertyChangeEventWithNoPropertyChangeListener.MockVetoableChangeListener"));

    assertTrue(bean.isEventDispatchEnabled());

    PropertyChangeEvent event = bean.createPropertyChangeEvent("id", 2l, 4l);

    assertEvent(event, bean, "id", 2l, 4l);
  }

  @Test
  public void testCreatePropertyChangeEventWhenEventDispatchDisabled() {
    TestBean<Long> bean = new TestBean<Long>();

    bean.setEventDispatchEnabled(false);

    bean.addPropertyChangeListener(mock(PropertyChangeListener.class,
      "testCreatePropertyChangeEventWhenEventDispatchDisabled.MockPropertyChangeListener"));

    bean.addVetoableChangeListener(mock(VetoableChangeListener.class,
      "testCreatePropertyChangeEventWhenEventDispatchDisabled.MockVetoableChangeListener"));

    assertFalse(bean.isEventDispatchEnabled());

    PropertyChangeEvent event = bean.createPropertyChangeEvent("id", 4l, 8l);

    assertEvent(event, bean, null, null, null);

    PropertyChangeEvent anotherEvent = bean.createPropertyChangeEvent("id", 81l, 16l);

    assertEvent(anotherEvent, bean, null, null, null);
    assertSame(event, anotherEvent);
  }

  @Test
  public void testFireChange() {
  }

  protected static final class TestBean<ID extends Comparable<ID>> extends AbstractBean<ID, Object, Object> {

    public TestBean() {
    }

    public TestBean(final ID id) {
      super(id);
    }
  }

  @SuppressWarnings("unused")
  protected static final class ValueHolder<T> extends AbstractBean<Long, Object, Object> {

    private T value;

    public ValueHolder() {
    }

    public ValueHolder(final T value) {
      this.value = value;
    }

    public ValueHolder(final Long id) {
      super(id);
    }

    public ValueHolder(final Long id, final T value) {
      super(id);
      this.value = value;
    }

    public T getValue() {
      return value;
    }

    public void setValue(final T value) {
      processChange("value", this.value, value);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }

      if (!(obj instanceof ValueHolder)) {
        return false;
      }

      ValueHolder that = (ValueHolder) obj;

      return ObjectUtils.equals(this.getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
      int hashValue = 17;
      hashValue = 37 * hashValue + ObjectUtils.hashCode(getValue());
      return hashValue;
    }

    @Override
    public String toString() {
      return String.format("{ @type = %1$s, id = %2$s, value = %3$s }", getClass().getName(), getId(), getValue());
    }
  }

}
