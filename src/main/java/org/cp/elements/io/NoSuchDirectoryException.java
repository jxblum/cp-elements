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
package org.cp.elements.io;

import java.io.File;

/**
 * Java {@link RuntimeException} thrown when a {@link File Directory} is not found.
 *
 * @author John Blum
 * @see java.io.File
 * @see java.lang.RuntimeException
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class NoSuchDirectoryException extends RuntimeException {

  /**
   * Constructs a new {@link NoSuchDirectoryException} having no {@link String message} and no {@link Throwable cause}.
   */
  public NoSuchDirectoryException() { }

  /**
   * Constructs a new {@link NoSuchDirectoryException} initialized with the given {@link String message}
   * describing the missing {@link File Directory}.
   *
   * @param message {@link String} containing a description of this {@link RuntimeException}.
   * @see java.lang.String
   */
  public NoSuchDirectoryException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link NoSuchDirectoryException} initialized with the given {@link Throwable}
   * used as the {@literal cause} of this {@link RuntimeException}.
   *
   * @param cause {@link Throwable} used as the cause of this {@link RuntimeException}.
   * @see java.lang.Throwable
   */
  public NoSuchDirectoryException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@link NoSuchDirectoryException} initialized with the given {@link String message}
   * describing the missing {@link File Directory} along with the given {@link Throwable} used as the {@literal cause}
   * of this {@link RuntimeException}.
   *
   * @param message {@link String} containing a description of this {@link RuntimeException}.
   * @param cause {@link Throwable} used as the cause of this {@link RuntimeException}.
   * @see java.lang.Throwable
   * @see java.lang.String
   */
  public NoSuchDirectoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
