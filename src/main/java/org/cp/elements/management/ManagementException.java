/*
 * Copyright 2017-Present Author or Authors.
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
package org.cp.elements.management;

/**
 * Java {@link RuntimeException} thrown when a component management function fails.
 *
 * @author John Blum
 * @see java.lang.RuntimeException
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class ManagementException extends RuntimeException {

  /**
   * Constructs a new {@link ManagementException} with no {@link String message} and no {@link Throwable cause}.
   */
  public ManagementException() { }

  /**
   * Constructs a new {@link ManagementException} with the given {@link String message} describing the reason
   * this exception was thrown.
   *
   * @param message {@link String message} describing the reason this exception was thrown.
   */
  public ManagementException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link ManagementException} with the given {@link Throwable cause} of this exception.
   *
   * @param cause {@link Throwable} used as the cause of this exception.
   */
  public ManagementException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@link ManagementException} with the given {@link String message} describing the reason
   * this exception was thrown along with the underlying {@link Throwable cause} of this exception.
   *
   * @param message {@link String message} describing the reason this exception was thrown.
   * @param cause {@link Throwable} used as the cause of this exception.
   */
  public ManagementException(String message, Throwable cause) {
    super(message, cause);
  }
}
