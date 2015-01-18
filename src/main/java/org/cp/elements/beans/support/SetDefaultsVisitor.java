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
import java.util.HashSet;
import java.util.Set;

import org.cp.elements.beans.annotation.Default;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.Visitable;
import org.cp.elements.lang.Visitor;
import org.cp.elements.util.ArrayUtils;
import org.cp.elements.util.convert.ConversionService;
import org.cp.elements.util.convert.provider.DefaultConversionService;

/**
 * The SetDefaultsVisitor class is an implementation of Visitor that introspects all visited beans
 * setting default values on defaulted (@Default annotated) bean fields and properties.
 *
 * @author John J. Blum
 * @see java.beans.BeanInfo
 * @see java.beans.Introspector
 * @see java.beans.PropertyDescriptor
 * @see java.lang.reflect.Field
 * @see java.lang.reflect.Method
 * @see org.cp.elements.beans.annotation.Default
 * @see org.cp.elements.lang.Visitable
 * @see org.cp.elements.lang.Visitor
 * @see org.cp.elements.util.convert.ConversionService
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class SetDefaultsVisitor implements Visitor {

  private final ConversionService conversionService;

  public SetDefaultsVisitor() {
    this(new DefaultConversionService());
  }

  public SetDefaultsVisitor(final ConversionService conversionService) {
    this.conversionService = (conversionService != null ? conversionService : new DefaultConversionService());
  }

  /**
   * Returns the ConversionService instance used to convert between the default annotated value and the class type
   * of the bean property.
   *
   * @return an instance of the ConversionService used to convert between the default annotated value
   * and the class type of the bean property.
   * @see org.cp.elements.util.convert.ConversionService
   */
  protected ConversionService getConversionService() {
    return conversionService;
  }

  @Override
  public void visit(final Visitable visitable) {
    Set<String> evaluatedProperties = new HashSet<String>();

    Class<?> objectType = visitable.getClass();

    while (!Object.class.equals(objectType)) {
      for (Field declaredField : objectType.getDeclaredFields()) {
        if (declaredField.isAnnotationPresent(Default.class)) {
          Object value = ObjectUtils.getValue(visitable, declaredField, declaredField.getType());

          if (value == null) {
            Default defaultAnnotation = declaredField.getAnnotation(Default.class);
            ObjectUtils.setField(visitable, declaredField, getConversionService().convert(
              defaultAnnotation.value(), declaredField.getType()));
          }

          evaluatedProperties.add(declaredField.getName());
        }
      }

      objectType = objectType.getSuperclass();
    }

    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(visitable.getClass());

      for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
        if (!evaluatedProperties.contains(propertyDescriptor.getName())) {
          Method readMethod = propertyDescriptor.getReadMethod();
          Method writeMethod = propertyDescriptor.getWriteMethod();

          if (ObjectUtils.isAnnotationPresent(Default.class, readMethod, writeMethod)) {
            Object value = ObjectUtils.invoke(visitable, readMethod, null, readMethod.getReturnType());

            if (value == null) {
              Default defaultAnnotation = writeMethod.getAnnotation(Default.class);

              defaultAnnotation = (defaultAnnotation != null ? defaultAnnotation
                : readMethod.getAnnotation(Default.class));

              Object[] arguments = { getConversionService().convert(defaultAnnotation.value(),
                ArrayUtils.getFirst(writeMethod.getParameterTypes())) };

              ObjectUtils.invoke(visitable, writeMethod, arguments, Void.class);
            }

            evaluatedProperties.add(propertyDescriptor.getName());
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

}
