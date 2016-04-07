/*
 * Copyright 2016 Author or Authors.
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

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.cp.elements.util.convert.ConversionService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The PropertiesAdapterTests class is a test suite of test cases testing the contract and functionality
 * of the {@link PropertiesAdapter} class.
 *
 * @author John J. Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.cp.elements.util.PropertiesAdapter
 */
public class PropertiesAdapterTests {

  public ExpectedException exception = ExpectedException.none();

  private static PropertiesAdapter propertiesAdapter;

  @BeforeClass
  public static void setupPropertiesAdapter() {
    Properties properties = new Properties();

    properties.setProperty("booleanProperty", "true");
    properties.setProperty("characterProperty", "X");
    properties.setProperty("doubleProperty", "3.14159");
    properties.setProperty("integerProperty", "2");
    properties.setProperty("stringProperty", "test");

    propertiesAdapter = PropertiesAdapter.from(properties);
  }

  @Before
  public void setup() {
    assertThat(propertiesAdapter, is(notNullValue()));
    assertThat(propertiesAdapter.getProperties(), is(notNullValue()));
    assertThat(propertiesAdapter.getConversionService(), is(notNullValue()));
  }

  @Test
  public void constructPropertiesAdapterWithNull() {
    exception.expect(NullPointerException.class);
    exception.expectCause(is(nullValue(Throwable.class)));
    exception.expectMessage("The Properties to wrap cannot be null");

    new PropertiesAdapter(null);
  }

  @Test
  public void fromProperties() {
    Properties mockProperties = mock(Properties.class);

    PropertiesAdapter propertiesAdapter = PropertiesAdapter.from(mockProperties);

    assertThat(propertiesAdapter, is(notNullValue()));
    assertThat(propertiesAdapter.getProperties(), is(sameInstance(mockProperties)));
    assertThat(propertiesAdapter.getConversionService(), is(notNullValue()));
  }

  @Test
  public void containsExistingPropertiesIsTrue() {
    assertThat(propertiesAdapter.contains("booleanProperty"), is(true));
    assertThat(propertiesAdapter.contains("characterProperty"), is(true));
    assertThat(propertiesAdapter.contains("doubleProperty"), is(true));
    assertThat(propertiesAdapter.contains("integerProperty"), is(true));
    assertThat(propertiesAdapter.contains("stringProperty"), is(true));
  }

  @Test
  public void containsNonExistingPropertiesIsFalse() {
    assertThat(propertiesAdapter.contains("boolProperty"), is(false));
    assertThat(propertiesAdapter.contains("charProperty"), is(false));
    assertThat(propertiesAdapter.contains("floatProperty"), is(false));
    assertThat(propertiesAdapter.contains("intProperty"), is(false));
    assertThat(propertiesAdapter.contains("strProperty"), is(false));
    assertThat(propertiesAdapter.contains("STRINGPROPERTY"), is(false));
  }

  @Test
  public void convertUsesConversionService() {
    ConversionService mockConversionService = mock(ConversionService.class);
    Properties mockProperties = mock(Properties.class);

    when(mockProperties.getProperty(anyString())).thenReturn("test");
    when(mockConversionService.convert(any(Object.class), eq(Class.class))).thenAnswer(
      (invocationOnMock) -> invocationOnMock.getArgumentAt(0, Object.class));

    PropertiesAdapter propertiesAdapter = new PropertiesAdapter(mockProperties) {
      @Override protected ConversionService getConversionService() {
        return mockConversionService;
      }
    };

    assertThat(propertiesAdapter.getConversionService(), is(sameInstance(mockConversionService)));
    assertThat(propertiesAdapter.getProperties(), is(sameInstance(mockProperties)));
    assertThat(propertiesAdapter.convert("prop", String.class), is(equalTo("test")));

    verify(mockProperties, times(1)).getProperty(eq("prop"));
    verify(mockConversionService, times(1)).convert(eq("test"), eq(String.class));
  }

  @Test
  public void defaultIfNotExistsReturnsValueOfExistingProperty() {
    assertThat(propertiesAdapter.defaultIfNotExists("booleanProperty", "false", String.class), is(equalTo("true")));
    assertThat(propertiesAdapter.defaultIfNotExists("characterProperty", "Y", String.class), is(equalTo("X")));
    assertThat(propertiesAdapter.defaultIfNotExists("doubleProperty", "1.21", String.class), is(equalTo("3.14159")));
    assertThat(propertiesAdapter.defaultIfNotExists("integerProperty", "4", String.class), is(equalTo("2")));
    assertThat(propertiesAdapter.defaultIfNotExists("stringProperty", "mock", String.class), is(equalTo("test")));
  }

  @Test
  public void defaultIfNotExistsReturnsDefaultValue() {
    assertThat(propertiesAdapter.defaultIfNotExists("boolProperty", false, Boolean.TYPE), is(false));
    assertThat(propertiesAdapter.defaultIfNotExists("charProperty", 'Y', Character.TYPE), is('Y'));
    assertThat(propertiesAdapter.defaultIfNotExists("floatProperty", 3.14f, Float.TYPE), is(3.14f));
    assertThat(propertiesAdapter.defaultIfNotExists("intProperty", 4, Integer.TYPE), is(4));
    assertThat(propertiesAdapter.defaultIfNotExists("strProperty", "TEST", String.class), is("TEST"));
  }

  @Test
  public void filterForTextBasedProperties() {
    PropertiesAdapter filteredPropertiesAdapter = propertiesAdapter.filter(
      (property) -> property.startsWith("char") || property.startsWith("str"));

    assertThat(filteredPropertiesAdapter, is(notNullValue()));
    assertThat(filteredPropertiesAdapter.contains("characterProperty"), is(true));
    assertThat(filteredPropertiesAdapter.contains("stringProperty"), is(true));
    assertThat(filteredPropertiesAdapter.contains("booleanProperty"), is(false));
    assertThat(filteredPropertiesAdapter.contains("doubleProperty"), is(false));
    assertThat(filteredPropertiesAdapter.contains("integerProperty"), is(false));
  }

  @Test
  public void filterForNonExistingProperties() {
    PropertiesAdapter filteredPropertiesAdapter = propertiesAdapter.filter((property) -> false);

    assertThat(filteredPropertiesAdapter, is(notNullValue()));
    assertThat(filteredPropertiesAdapter.isEmpty(), is(true));
  }

  @Test
  public void getExistingPropertyReturnsValue() {
    assertThat(propertiesAdapter.get("booleanProperty"), is(equalTo("true")));
    assertThat(propertiesAdapter.get("characterProperty"), is(equalTo("X")));
    assertThat(propertiesAdapter.get("doubleProperty"), is(equalTo("3.14159")));
    assertThat(propertiesAdapter.get("integerProperty"), is(equalTo("2")));
    assertThat(propertiesAdapter.get("stringProperty"), is(equalTo("test")));
  }

  @Test
  public void getExistingPropertyWithDefaultValueReturnsPropertyValue() {
    assertThat(propertiesAdapter.get("stringProperty", "MOCK"), is(equalTo("test")));
  }

  @Test
  public void getExistingPropertyAsTypeReturnsTypedValue() {
    assertThat(propertiesAdapter.getAsType("booleanProperty", Boolean.TYPE), is(equalTo(true)));
    assertThat(propertiesAdapter.getAsType("characterProperty", Character.TYPE), is(equalTo('X')));
    assertThat(propertiesAdapter.getAsType("doubleProperty", Double.TYPE), is(equalTo(3.14159d)));
    assertThat(propertiesAdapter.getAsType("integerProperty", Integer.TYPE), is(equalTo(2)));
    assertThat(propertiesAdapter.getAsType("stringProperty", String.class), is(equalTo("test")));
  }

  @Test
  public void getExistingPropertyAsTypeWithDefaultValueReturnsTypedPropertyValue() {
    assertThat(propertiesAdapter.getAsType("characterProperty", 'Y', Character.TYPE), is(equalTo('X')));
  }

  @Test
  public void getNonExistingPropertyReturnsNull() {
    assertThat(propertiesAdapter.get("nonExistingProperty"), is(nullValue()));
  }

  @Test
  public void getNonExistingPropertyWithDefaultValueReturnsDefaultValue() {
    assertThat(propertiesAdapter.get("nonExistingProperty", "TEST"), is(equalTo("TEST")));
  }

  @Test
  public void getNonExistingPropertyAsTypeReturnsNull() {
    assertThat(propertiesAdapter.getAsType("nonExistingProperty", Double.TYPE), is(nullValue()));
  }

  @Test
  public void getNonExistingPropertyAsTypeWithDefaultValueReturnsDefaultValue() {
    assertThat(propertiesAdapter.getAsType("nonExistingProperty", 123.45d, Double.TYPE), is(equalTo(123.45d)));
  }

  @Test
  public void isEmpty() {
  }

}
