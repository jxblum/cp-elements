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
package org.cp.elements.lang;

/**
 * Java {@link IllegalArgumentException} thrown when {@link Object} is suffering an identity crisis.
 *
 * @author John J. Blum
 * @see java.lang.IllegalArgumentException
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class IdentityException extends IllegalArgumentException {

  /**
   * Construct a new, uninitialized instance of {@link IdentityException}.
   */
  public IdentityException() {
  }

  /**
   * Constructs a new {@link IdentityException} initialized with the given {@link String message}
   * describing the identity error.
   *
   * @param message {@link String} describing the identity error.
   */
  public IdentityException(final String message) {
    super(message);
  }

  /**
   * Construct a new {@link IdentityException} initialized with the given {@link Throwable}
   * used as the cause of this identity error.
   *
   * @param cause {@link Throwable} used as the cause of this equality error.
   */
  public IdentityException(final Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@link IdentityException} initialized with the given {@link String message}
   * describing the identity error along with the given {@link Throwable} used as the cause
   * of this identity error.
   *
   * @param message {@link String} describing the identity error.
   * @param cause {@link Throwable} used as the cause of this equality error.
   */
  public IdentityException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
