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
package org.cp.elements.data.struct.tabular.support;

import org.cp.elements.data.struct.tabular.Column;
import org.cp.elements.data.struct.tabular.Table;
import org.cp.elements.data.struct.tabular.View;
import org.cp.elements.lang.annotation.Nullable;

/**
 * Java {@link RuntimeException} used to indicate that a {@link Column} could not be found
 * in a {@link Table} or {@link View}.
 *
 * @author John Blum
 * @see java.lang.RuntimeException
 * @see org.cp.elements.data.struct.tabular.Column
 * @see org.cp.elements.data.struct.tabular.Table
 * @see org.cp.elements.data.struct.tabular.View
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ColumnNotFoundException extends RuntimeException {

  /**
   * Constructs a new {@link ColumnNotFoundException} with no {@link String message} and no {@link Throwable cause}.
   */
  public ColumnNotFoundException() { }

  /**
   * Constructs a new {@link ColumnNotFoundException} with the given {@link String message} used to describe this error.
   *
   * @param message {@link String} containing a {@literal description} of this {@link ColumnNotFoundException}
   */
  public ColumnNotFoundException(@Nullable String message) {
    super(message);
  }

  /**
   * Constructs a new {@link ColumnNotFoundException} with the given {@link Throwable cause}
   * used as the underlying reason this {@link ColumnNotFoundException} was thrown.
   *
   * @param cause {@link Throwable} used as the reason (cause) for this {@link ColumnNotFoundException}
   */
  public ColumnNotFoundException(@Nullable Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@link ColumnNotFoundException} with the given {@link String message} used to describe this error
   * along with an underlying reason this {@link ColumnNotFoundException} was thrown.
   *
   * @param message {@link String} containing a {@literal description} of this {@link ColumnNotFoundException}
   * @param cause {@link Throwable} used as the reason (cause) for this {@link ColumnNotFoundException}
   */
  public ColumnNotFoundException(@Nullable String message, @Nullable Throwable cause) {
    super(message, cause);
  }
}
