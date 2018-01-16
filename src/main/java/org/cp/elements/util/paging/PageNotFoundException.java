/*
 * Copyright (c) 2011-Present. Codeprimate, LLC and authors.  All Rights Reserved.
 * <p/>
 * This software is licensed under the Codeprimate End User License Agreement (EULA).
 * This software is proprietary and confidential in addition to an intellectual asset
 * of the aforementioned authors.
 * <p/>
 * By using the software, the end-user implicitly consents to and agrees to be in compliance
 * with all terms and conditions of the EULA.  Failure to comply with the EULA will result in
 * the maximum penalties permissible by law.
 * <p/>
 * In short, this software may not be reverse engineered, reproduced, copied, modified
 * or distributed without prior authorization of the aforementioned authors, permissible
 * and expressed only in writing.  The authors grant the end-user non-exclusive, non-negotiable
 * and non-transferable use of the software "as is" without expressed or implied WARRANTIES,
 * EXTENSIONS or CONDITIONS of any kind.
 * <p/>
 * For further information on the software license, the end user is encouraged to read
 * the EULA @ ...
 */

package org.cp.elements.util.paging;

/**
 * The {@link PageNotFoundException} class is a {@link RuntimeException} indicating that a {@link Page}
 * could not be found in the {@link Pageable} object.
 *
 * @author John J. Blum
 * @see java.lang.RuntimeException
 * @see org.cp.elements.util.paging.Page
 * @see org.cp.elements.util.paging.Pageable
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class PageNotFoundException extends RuntimeException {

  /**
   * Default constructor to construct a new, uninitialized instance of {@link PageNotFoundException}.
   */
  public PageNotFoundException() {
  }

  /**
   * Constructs a new instance of {@link PageNotFoundException} initialized with a given {@link String message}
   * to describe the {@link Page} not found error.
   *
   * @param message {@link String} describing the {@link Page} not found error.
   * @see java.lang.String
   */
  public PageNotFoundException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance of {@link PageNotFoundException} initialized with the given {@link Throwable}
   * to indicate the cause of the {@link Page} not found error.
   *
   * @param cause {@link Throwable} indicating the cause of the {@link Page} not found error.
   * @see java.lang.Throwable
   */
  public PageNotFoundException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new instance of {@link PageNotFoundException} initialized with a given {@link String message}
   * to describe the {@link Page} not found error and a {@link Throwable} to indicate the cause
   * of the {@link Page} not found error.
   *
   * @param message {@link String} describing the {@link Page} not found error.
   * @param cause {@link Throwable} indicating the cause of the {@link Page} not found error.
   * @see java.lang.String
   * @see java.lang.Throwable
   */
  public PageNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
