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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.util.ArrayUtils;

/**
 * The ImmutableVetoableChangeListener class is a JavaBeans VetoableChangeListener implementation that declares
 * registered beans or properties of registered beans as immutable.
 *
 * @author John J. Blum
 * @see java.beans.PropertyChangeEvent
 * @see java.beans.VetoableChangeListener
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ImmutableVetoableChangeListener implements VetoableChangeListener {

  private Set<String> immutablePropertyNames = Collections.emptySet();

  /**
   * Constructs an instance of the ImmutableVetoableChangeListener initialized with the specified property names
   * declaring that the provided properties of the registered bean are immutable.  If the array is null or empty,
   * then the bean on which this listener is registered is considered immutable.
   *
   * @param propertyNames an array of Strings indicating the names of all immutable properties on the registered bean.
   */
  public ImmutableVetoableChangeListener(final String... propertyNames) {
    if (!ArrayUtils.isEmpty(propertyNames)) {
      immutablePropertyNames = new TreeSet<String>();
      Collections.addAll(immutablePropertyNames, propertyNames);
    }
  }

  /**
   * Returns the set of property names for the registered bean that are considered immutable by this listener.
   *
   * @return a Set containing the names of all properties on the registered bean that are considered immutable
   * by this listener.
   * @see java.util.Set
   */
  protected Set<String> getImmutablePropertyNames() {
    return immutablePropertyNames;
  }

  /**
   * Determines whether the value for the given bean property identified in the PropertyChangeEvent has been changed.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @return a boolean value indicating whether the value of the property identified in the event has been changed.
   * @see java.beans.PropertyChangeEvent#getOldValue()
   * @see java.beans.PropertyChangeEvent#getNewValue()
   * @see org.cp.elements.lang.ObjectUtils#equalsIgnoreNull(Object, Object)
   */
  protected boolean isChange(final PropertyChangeEvent event) {
    return !ObjectUtils.equalsIgnoreNull(event.getOldValue(), event.getNewValue());
  }

  /**
   * Determines whether the given bean property identified in the PropertyChangeEvent is immutable.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @return a boolean value indicating whether the given bean property identified in the PropertyChangeEvent
   * is immutable.
   * @see java.beans.PropertyChangeEvent#getPropertyName()
   * @see #getImmutablePropertyNames()
   */
  protected boolean isImmutable(final PropertyChangeEvent event) {
    return (getImmutablePropertyNames().isEmpty() || getImmutablePropertyNames().contains(event.getPropertyName()));
  }

  /**
   * Determines whether the bean property identified in the PropertyChangeEvent is immutable, and then if the value
   * of the bean property changed, is vetoable.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @return a boolean value indicating whether the bean property is immutable and if the value has changed, vetoable.
   * @see java.beans.PropertyChangeEvent
   * @see #isImmutable(java.beans.PropertyChangeEvent)
   * @see #isChange(java.beans.PropertyChangeEvent)
   */
  protected boolean isVetoable(final PropertyChangeEvent event) {
    return (isImmutable(event) && isChange(event));
  }

  /**
   * Callback handler method receiving notification of the bean's property change event, which is called every time
   * one of the bean's properties is changed.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @throws java.beans.PropertyVetoException if the bean or one of it's declared properties is immutable and the value
   * has changed as detected by this listener.
   * @see java.beans.PropertyChangeEvent
   * @see #isVetoable(java.beans.PropertyChangeEvent)
   */
  @Override
  public void vetoableChange(final PropertyChangeEvent event) throws PropertyVetoException {
    if (isVetoable(event)) {
      String message = (getImmutablePropertyNames().isEmpty() ? "The bean of type (%1$s) is immutable!"
        : "Property (%2$s) on bean type (%1$s) is immutable!");
      throw new PropertyVetoException(String.format(message, event.getSource().getClass().getName(),
        event.getPropertyName()), event);
    }
  }

}
