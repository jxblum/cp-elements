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

package org.cp.elements.data.struct;

/**
 * The Row interface defines a row in a Table data structure.
 * <p/>
 * @author John J. Blum
 * @see org.cp.elements.data.struct.Column
 * @see org.cp.elements.data.struct.Table
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface Row extends Iterable<Object> {

  public int getIndex();

  public boolean hasValue(int columnIndex);

  public boolean hasValue(String columnName);

  public boolean hasValue(Column column);

  public Object getValue(int columnIndex);

  public Object getValue(String columnName);

  public Object getValue(Column column);

  public Object setValue(int columnIndex, Object value);

  public Object settValue(String columnName, Object value);

  public Object setValue(Column column, Object value);

  public int size();

  public Object[] values();

}
