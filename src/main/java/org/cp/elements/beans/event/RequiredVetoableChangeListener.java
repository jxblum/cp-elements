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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.cp.elements.beans.annotation.Required;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.InitializationException;
import org.cp.elements.lang.ObjectUtils;

/**
 * The RequiredVetoableChangeListener class is a JavaBeans VetoableChangeListener implementation that evaluates the
 * registered bean for any @Required annotated properties to determine and constrain the bean's required properties
 * at runtime.
 *
 * @author John J. Blum
 * @see java.beans.Introspector
 * @see java.lang.reflect.Field
 * @see java.lang.reflect.Method
 * @see org.cp.elements.beans.annotation.Required
 * @see org.cp.elements.beans.event.RequirePropertyVetoableChangeListener
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class RequiredVetoableChangeListener extends RequirePropertyVetoableChangeListener {

  /**
   * Constructs an instance of the RequiredVetoableChangeListener initialized with the given bean, which is
   * introspected for required properties.  Required properties are determined by the @Required annotation on
   * the bean properties accessor methods (getter or setter, or both).
   *
   * @param bean the JavaBean on which to introspect required properties and register this listener.
   * @see #getRequiredProperties(Object)
   */
  public RequiredVetoableChangeListener(final Object bean) {
    super(getRequiredProperties(bean));
  }

  /**
   * Introspects the given JavaBean to determine the required properties.  The properties of the given bean class
   * are considered required if the accessor methods of the property are annotated with @Required.
   *
   * @param bean the JavaBean introspected for required properties.
   * @return an array of Strings indicating the names of the bean's required properties
   * @see java.beans.BeanInfo
   * @see java.beans.Introspector#getBeanInfo(Class)
   * @see java.beans.PropertyDescriptor
   * @see org.cp.elements.beans.annotation.Required
   */
  protected static String[] getRequiredProperties(final Object bean) {
    Assert.notNull(bean, "The bean on which to register the RequiredVetoableChangeListener cannot be null!");

    Set<String> requiredPropertyNames = new HashSet<String>();

    Class<?> beanType = bean.getClass();

    while (!Object.class.equals(beanType)) {
      for (Field declaredField : beanType.getDeclaredFields()) {
        if (declaredField.isAnnotationPresent(Required.class)) {
          requiredPropertyNames.add(declaredField.getName());
        }
      }

      beanType = beanType.getSuperclass();
    }

    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());

      for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
        if (!requiredPropertyNames.contains(propertyDescriptor.getName())) {
          Method getterMethod = propertyDescriptor.getReadMethod();
          Method setterMethod = propertyDescriptor.getWriteMethod();

          if (ObjectUtils.isAnnotationPresent(Required.class, getterMethod, setterMethod)) {
            requiredPropertyNames.add(propertyDescriptor.getName());
          }
        }
      }

      return requiredPropertyNames.toArray(new String[requiredPropertyNames.size()]);
    }
    catch (IntrospectionException e) {
      throw new InitializationException(String.format(
        "Failed to initialize the RequiredVetoableChangeListener for bean of type (%1$s)!",
          bean.getClass().getName()), e);
    }
    finally {
      Introspector.flushFromCaches(bean.getClass());
    }
  }

}
