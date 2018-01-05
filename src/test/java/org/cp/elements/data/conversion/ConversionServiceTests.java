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

package org.cp.elements.data.conversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cp.elements.util.ArrayUtils.asIterator;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests for {@link ConversionService}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.cp.elements.data.conversion.ConversionService
 * @since 1.0.0
 */
public class ConversionServiceTests {

  @Test
  public void canConvertWithObjectReturnsTrue() {

    ConversionService mockConversionService = mock(ConversionService.class);

    when(mockConversionService.canConvert(any(Object.class), any(Class.class))).thenCallRealMethod();
    when(mockConversionService.canConvert(any(Class.class), any(Class.class))).thenReturn(true);

    assertThat(mockConversionService.canConvert(new Object(), String.class)).isTrue();

    verify(mockConversionService, times(1)).canConvert(eq(Object.class), eq(String.class));
  }

  @Test
  public void canConverterWithObjectReturnsFalse() {

    ConversionService mockConversionService = mock(ConversionService.class);

    when(mockConversionService.canConvert(any(Object.class), any(Class.class))).thenCallRealMethod();
    when(mockConversionService.canConvert(any(Class.class), any(Class.class))).thenReturn(false);

    assertThat(mockConversionService.canConvert("test", Enum.class)).isFalse();

    verify(mockConversionService, times(1)).canConvert(eq(String.class), eq(Enum.class));
  }

  @Test
  public void canConvertWithNullObjectReturnsFalse() {

    ConversionService mockConversionService = mock(ConversionService.class);

    when(mockConversionService.canConvert(any(Object.class), any(Class.class))).thenCallRealMethod();

    assertThat(mockConversionService.canConvert((Object) null, Object.class)).isFalse();

    verify(mockConversionService, never()).canConvert(any(Class.class), any(Class.class));
  }

  @Test
  public void canConvertWithTypeReturnsTrue() {

    Converter<?, ?> mockConverter = mock(Converter.class);

    ConversionService mockConversionService = mock(ConversionService.class);

    when(mockConversionService.canConvert(any(Class.class), any(Class.class))).thenCallRealMethod();
    when(mockConversionService.iterator()).thenReturn(asIterator(mockConverter));
    when(mockConverter.canConvert(any(Class.class), any(Class.class))).thenReturn(true);

    assertThat(mockConversionService.canConvert(String.class, Enum.class)).isTrue();

    verify(mockConversionService, times(1)).iterator();
    verify(mockConverter, times(1)).canConvert(eq(String.class), eq(Enum.class));
  }

  @Test
  public void canConvertWithTypeWhenConverterCannotConvertReturnsFalse() {

    Converter<?, ?> mockConverter = mock(Converter.class);

    ConversionService mockConversionService = mock(ConversionService.class);

    when(mockConversionService.canConvert(any(Class.class), any(Class.class))).thenCallRealMethod();
    when(mockConversionService.iterator()).thenReturn(asIterator(mockConverter));
    when(mockConverter.canConvert(any(Class.class), any(Class.class))).thenReturn(false);

    assertThat(mockConversionService.canConvert(String.class, Enum.class)).isFalse();

    verify(mockConversionService, times(1)).iterator();
    verify(mockConverter, times(1)).canConvert(eq(String.class), eq(Enum.class));
  }

  @Test
  public void canConvertWithTypeWhenNoConvertersPresentReturnsFalse() {

    ConversionService mockConversionService = mock(ConversionService.class);

    when(mockConversionService.canConvert(any(Class.class), any(Class.class))).thenCallRealMethod();
    when(mockConversionService.iterator()).thenReturn(Collections.emptyIterator());

    assertThat(mockConversionService.canConvert(String.class, Enum.class)).isFalse();

    verify(mockConversionService, times(1)).iterator();
  }
}
