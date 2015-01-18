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

package org.cp.elements.beans.event;

import static org.cp.elements.lang.LangExtensions.is;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.cp.elements.util.ArrayUtils;

/**
 * The RequirePropertyVetoableChangeListener class is a JavaBeans VetoableChangeListener implementation indicating
 * which bean properties on the bean class are required when the corresponding setter is called.
 *
 * @author John J. Blum
 * @see java.beans.VetoableChangeListener
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class RequirePropertyVetoableChangeListener implements VetoableChangeListener {

  private Set<String> requiredPropertyNames = Collections.emptySet();

  /**
   * Constructs an instance of the RequirePropertyeVetoableChangeListener class initialized with the specified
   * property names indicating the required properties of the registered bean.  If the array is null or empty,
   * then all properties of the bean on which this listener is registered are considered required.
   *
   * @param propertyNames an array of String values indicating the names of the properties that are required.
   */
  public RequirePropertyVetoableChangeListener(final String... propertyNames) {
    if (!ArrayUtils.isEmpty(propertyNames)) {
      requiredPropertyNames = new TreeSet<String>();
      Collections.addAll(requiredPropertyNames, propertyNames);
    }
  }

  /**
   * Returns the set of property names that are required by this listener.
   *
   * @return a Set containing the names of all the properties that are required by this listener.
   * @see java.util.Set
   */
  protected Set<String> getRequiredPropertyNames() {
    return requiredPropertyNames;
  }

  /**
   * Determines whether the given property by name is required.
   *
   * @param propertyName a String value specifying the name of the property.
   * @return a boolean value indicating whether the given property by name is required.
   * @see #getRequiredPropertyNames()
   */
  protected boolean isRequired(final String propertyName) {
    return (getRequiredPropertyNames().isEmpty() || getRequiredPropertyNames().contains(propertyName));
  }

  /**
   * Determines whether property identified in the PropertyChangeEvent is required and if null, vetoable.
   *
   * @param event the PropertyChangeEvent capturing the details of the property change for the specified property.
   * @return a boolean value indicating if the property is required and the new value is null.
   * @see java.beans.PropertyChangeEvent
   * @see #isRequired(String)
   */
  protected boolean isVetoable(final PropertyChangeEvent event) {
    return (isRequired(event.getPropertyName()) && is(event.getNewValue()).Null());
  }

  /**
   * Callback handler method that gets notified when a property changes on the bean for which this listener
   * is registered.
   *
   * @param event the PropertyChangeEvent capturing the details of the property change.
   * @throws PropertyVetoException if the property indicated in the PropertyChangeEvent is required
   * and it's new value is null.
   * @see java.beans.PropertyChangeEvent
   * @see #isVetoable(java.beans.PropertyChangeEvent)
   */
  @Override
  public void vetoableChange(final PropertyChangeEvent event) throws PropertyVetoException {
    if (isVetoable(event)) {
      throw new PropertyVetoException(String.format("Property (%1$s) on bean of type (%2$s) is required!",
        event.getPropertyName(), event.getSource().getClass().getName()),
          event);
    }
  }

}
