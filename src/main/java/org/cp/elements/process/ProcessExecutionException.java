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
package org.cp.elements.process;

/**
 * {@link ProcessException} thrown when a runtime error occurs during the execution of a program,
 * which may or may not have terminated the {@link Process}.
 *
 * @author John Blum
 * @see org.cp.elements.process.ProcessException
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ProcessExecutionException extends ProcessException {

  /**
   * Constructs a new {@link ProcessExecutionException} with no {@link String message} and no {@link Throwable cause}.
   */
  public ProcessExecutionException() { }

  /**
   * Constructs an instance of {@link ProcessExecutionException} initialized with the given message
   * describing this {@link ProcessException}.
   *
   * @param message {@link String} describing this {@link ProcessException}.
   */
  public ProcessExecutionException(String message) {
    super(message);
  }

  /**
   * Constructs an instance of {@link ProcessExecutionException} initialized with the given {@link Throwable}
   * indicating the cause of this {@link ProcessException}.
   *
   * @param cause {@link Throwable} object indicating the cause of this {@link ProcessException}.
   * @see java.lang.Throwable
   */
  public ProcessExecutionException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs an instance of {@link ProcessExecutionException} initialized with the given message
   * describing this {@link ProcessException} and cause to indicate the reason this {@link ProcessException}
   * was thrown.
   *
   * @param message {@link String} describing this {@link ProcessException}.
   * @param cause {@link Throwable} object indicating the cause of this {@link ProcessException}.
   * @see java.lang.Throwable
   */
  public ProcessExecutionException(String message, Throwable cause) {
    super(message, cause);
  }
}
