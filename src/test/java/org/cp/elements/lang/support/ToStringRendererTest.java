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

package org.cp.elements.lang.support;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * The ToStringRendererTest class is a test suite of test cases testing the contract and functionality
 * of the {@link ToStringRenderer} class.
 *
 * @author John J. Blum
 * @see org.junit.Test
 * @see org.cp.elements.lang.support.ToStringRenderer
 * @since 1.0.0
 */
public class ToStringRendererTest {

  @Test
  public void render() {
    assertThat(new ToStringRenderer<String>().render("test"), is(equalTo("test")));
  }

}
