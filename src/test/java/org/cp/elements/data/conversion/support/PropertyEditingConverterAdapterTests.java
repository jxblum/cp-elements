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

package org.cp.elements.data.conversion.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cp.elements.lang.RuntimeExceptionsFactory.newIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.beans.PropertyEditor;

import org.cp.elements.data.conversion.ConversionException;
import org.cp.elements.enums.Gender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link PropertyEditingConverterAdapter}.
 *
 * @author John Blum
 * @see java.beans.PropertyEditor
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.cp.elements.data.conversion.Converter
 * @see org.cp.elements.data.conversion.support.PropertyEditingConverterAdapter
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PropertyEditingConverterAdapterTests {

  @Mock
  private PropertyEditor mockPropertyEditor;

  @Test
  public void constructNewPropertyEditingConverterAdapter() {

    PropertyEditingConverterAdapter converter = new PropertyEditingConverterAdapter(this.mockPropertyEditor);

    assertThat(converter).isNotNull();
    assertThat(converter.getPropertyEditor()).isEqualTo(this.mockPropertyEditor);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructNewPropertyEditingConverterAdapterWithNullPropertyEditorThrowsException() {

    try {
      new PropertyEditingConverterAdapter(null);
    }
    catch (IllegalArgumentException expected) {

      assertThat(expected).hasMessage("PropertyEditor is required");
      assertThat(expected).hasNoCause();

      throw expected;
    }
  }

  @Test
  public void convertIsSuccessful() {

    when(this.mockPropertyEditor.getValue()).thenReturn(Gender.FEMALE);

    assertThat(PropertyEditingConverterAdapter.of(this.mockPropertyEditor).convert("female"))
      .isEqualTo(Gender.FEMALE);

    verify(this.mockPropertyEditor, times(1)).setAsText(eq("female"));
    verify(this.mockPropertyEditor, times(1)).getValue();
  }

  @Test(expected = ConversionException.class)
  public void convertHandlesIllegalArgumentExceptionThrowsConversionException() {

    doThrow(newIllegalArgumentException("test")).when(this.mockPropertyEditor).setAsText(anyString());

    try {
      PropertyEditingConverterAdapter.of(this.mockPropertyEditor).convert("test");
    }
    catch (ConversionException expected) {

      assertThat(expected).hasMessage("Cannot convert [test] to an Object type");
      assertThat(expected).hasCauseInstanceOf(IllegalArgumentException.class);
      assertThat(expected.getCause()).hasMessage("test");
      assertThat(expected.getCause()).hasNoCause();

      throw expected;
    }
    finally {
      verify(this.mockPropertyEditor, times(1)).setAsText(eq("test"));
      verify(this.mockPropertyEditor, never()).getValue();
    }
  }
}
