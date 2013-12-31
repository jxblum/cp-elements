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

package org.cp.elements.util.convert.support;

import org.cp.elements.util.convert.ConverterAdapter;

/**
 * The StringConverter class converts an Object value into a String.
 * <p/>
 * @author John J. Blum
 * @see java.lang.Object
 * @see java.lang.String
 * @see org.cp.elements.util.convert.ConverterAdapter
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class StringConverter extends ConverterAdapter<Object, String> {

  @Override
  public boolean canConvert(final Class<?> fromType, final Class<?> toType) {
    return String.class.equals(toType);
  }

  @Override
  public String convert(final Object value) {
    return String.valueOf(value);
  }

}
