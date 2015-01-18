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

package org.cp.elements.beans.support;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.cp.elements.beans.RequiredPropertyNotSetException;
import org.cp.elements.beans.annotation.Required;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.StringUtils;
import org.cp.elements.lang.Visitable;
import org.cp.elements.lang.Visitor;

/**
 * The RequiredVisitor class is a Visitor implementation that introspects the visited Visitable object evaluating
 * and throwing on any properties that are required and not set.
 *
 * @author John J. Blum
 * @see org.cp.elements.lang.Visitable
 * @see org.cp.elements.lang.Visitor
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class RequiredVisitor implements Visitor {

  protected static final boolean DEFAULT_FAIL_FAST = false;

  private final boolean failFast;

  private final Map<Class, Set<String>> unsetRequiredProperties;

  public RequiredVisitor() {
    this(DEFAULT_FAIL_FAST);
  }

  public RequiredVisitor(final boolean failFast) {
    this.failFast = failFast;
    unsetRequiredProperties = (this.failFast ? Collections.<Class, Set<String>>emptyMap()
      : new TreeMap<Class, Set<String>>());
  }

  @Override
  public void visit(final Visitable visitable) {
    Set<String> evaluatedRequiredProperties = new TreeSet<String>();

    Class<?> objectType = visitable.getClass();

    while (!Object.class.equals(objectType)) {
      for (Field declaredField : objectType.getDeclaredFields()) {
        if (declaredField.isAnnotationPresent(Required.class)) {
          Object value = ObjectUtils.getValue(visitable, declaredField, declaredField.getType());
          evaluate(value, visitable.getClass(), declaredField.getName());
          evaluatedRequiredProperties.add(declaredField.getName());
        }
      }

      objectType = objectType.getSuperclass();
    }

    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(visitable.getClass());

      for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
        if (!evaluatedRequiredProperties.contains(propertyDescriptor.getName())) {
          Method readMethod = propertyDescriptor.getReadMethod();
          Method writeMethod = propertyDescriptor.getWriteMethod();

          if (ObjectUtils.isAnnotationPresent(Required.class, readMethod, writeMethod)) {
            Object value = ObjectUtils.invoke(visitable, readMethod, null, readMethod.getReturnType());
            evaluate(value, visitable.getClass(), propertyDescriptor.getName());
            evaluatedRequiredProperties.add(propertyDescriptor.getName());
          }
        }
      }
    }
    catch (IntrospectionException e) {
      throw new RuntimeException(String.format("Failed to introspect bean of type (%1$s)!",
        visitable.getClass().getName()), e);
    }
    finally {
      Introspector.flushFromCaches(visitable.getClass());
    }
  }

  private void evaluate(final Object value, final Class<?> type, final String propertyName) {
    if (value == null) {
      if (failFast) {
        throw new RequiredPropertyNotSetException(String.format(
          "Required property (%1$s) on object of type (%2$s) is not set!",
            propertyName, type.getName()));
      }
      else {
        addUnsetRequiredProperty(type, propertyName);
      }
    }
  }

  private boolean addUnsetRequiredProperty(final Class<?> type, final String propertyName) {
    Set<String> unsetRequiredPropertiesByType = unsetRequiredProperties.get(type);

    if (unsetRequiredPropertiesByType == null) {
      unsetRequiredPropertiesByType = new TreeSet<String>();
      unsetRequiredProperties.put(type, unsetRequiredPropertiesByType);
    }

    return unsetRequiredPropertiesByType.add(propertyName);
  }

  public void assertRequiredPropertiesSet() {
    if (!unsetRequiredProperties.isEmpty()) {
      StringBuilder buffer = new StringBuilder();

      for (Map.Entry<Class, Set<String>> mapEntry : unsetRequiredProperties.entrySet()) {
        buffer.append(buffer.length() > 0 ? StringUtils.LINE_SEPARATOR : StringUtils.EMPTY_STRING);
        buffer.append(String.format("Unset required properties (%1$s) for object of type (%2$s)!",
          mapEntry.getKey().getName(), mapEntry.getValue()));
      }

      throw new RequiredPropertyNotSetException(buffer.toString());
    }
  }

}
