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
package org.cp.elements.biz.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link Rule}.
 *
 * @author John Blum
 * @see org.cp.elements.biz.rules.Rule
 * @see org.junit.jupiter.api.Test
 * @see org.mockito.Mockito
 * @since 2.0.0
 */
public class RuleUnitTests {

  @Test
  void defaultExpectedOutcome() {

    Rule<?, ?> mockRule = mock(Rule.class);

    doCallRealMethod().when(mockRule).getExpectedOutcome();

    assertThat(mockRule.getExpectedOutcome()).isTrue();

    verify(mockRule, times(1)).getExpectedOutcome();
    verifyNoMoreInteractions(mockRule);
  }

  @Test
  void defaultIsThrowExceptionOnFailure() {

    Rule<?, ?> mockRule = mock(Rule.class);

    doCallRealMethod().when(mockRule).isThrowExceptionOnFailure();

    assertThat(mockRule.isThrowExceptionOnFailure()).isFalse();

    verify(mockRule, times(1)).isThrowExceptionOnFailure();
    verifyNoMoreInteractions(mockRule);
  }
}
