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
package org.cp.elements.io.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import org.cp.elements.io.FileExtensionFilter;
import org.junit.Test;

/**
 * {@link AbstractFileExtensionFilterTests} is an abstract base class containing test cases common to all
 * {@link org.cp.elements.io.FileExtensionFilter} tests in the {@link org.cp.elements.io.support} package.
 *
 * @author John Blum
 * @see java.io.File
 * @see org.junit.Test
 * @see org.cp.elements.io.FileExtensionFilter
 * @since 1.0.0
 */
public abstract class AbstractFileExtensionFilterTests {

  protected abstract String[] expectedFileExtensions();

  protected int expectedSize() {
    return expectedFileExtensions().length;
  }

  protected abstract FileExtensionFilter fileExtensionFilter();

  protected File newFile(String pathname) {
    return new File(pathname);
  }

  protected abstract String[] unexpectedFileExtensions();

  @Test
  public void acceptIsSuccessfulWithExpectedFileExtensions() {
    Set<String> fileExtensions = fileExtensionFilter().getFileExtensions();

    assertThat(fileExtensions).isNotNull();
    assertThat(fileExtensions.size()).isEqualTo(expectedSize());
    assertThat(fileExtensions.containsAll(Arrays.asList(expectedFileExtensions()))).isTrue();

    for (String fileExtension : fileExtensions) {
      assertThat(fileExtensionFilter().accept(newFile(String.format("file.%s", fileExtension)))).isTrue();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void acceptWithNullThrowsIllegalArgumentException() {

    try {
      fileExtensionFilter().accept(null);
    }
    catch (IllegalArgumentException expected) {

      assertThat(expected).hasMessage("File cannot be null");
      assertThat(expected).hasNoCause();

      throw expected;
    }
  }

  @Test
  public void rejectIsSuccessfulWithUnexpectedFileExtensions() {
    for (String fileExtension : unexpectedFileExtensions()) {
      assertThat(fileExtensionFilter().accept(newFile(fileExtension))).isFalse();
    }
  }
}
