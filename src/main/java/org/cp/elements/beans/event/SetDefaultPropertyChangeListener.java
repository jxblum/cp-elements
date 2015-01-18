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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.cp.elements.beans.annotation.Default;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.InitializationException;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.util.ArrayUtils;
import org.cp.elements.util.convert.ConversionService;
import org.cp.elements.util.convert.provider.DefaultConversionService;

/**
 * The SetDefaultPropertyChangeListener class is a JavaBeans PropertyChangeListener implementation that introspects
 * a given bean reflecting on bean properties having default values as defined by the @Default annotation meta-data.
 *
 * @author John J. Blum
 * @see java.beans.PropertyChangeEvent
 * @see java.beans.PropertyChangeListener
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class SetDefaultPropertyChangeListener implements PropertyChangeListener {

  private final ConversionService conversionService;

  private final Map<PropertyDescriptor, Object> beanPropertyDefaults;

  /**
   * Constructs an instance of the SetDefaultPropertyChangeListener class for the given bean listening for property
   * change events on potential defaulted bean properties.
   *
   * @param bean the bean on which this PropertyChangeListener is registered.
   */
  public SetDefaultPropertyChangeListener(final Object bean) {
    this(bean, new DefaultConversionService());
  }

  /**
   * Constructs an instance of the SetDefaultPropertyChangeListener class for the given bean listening for property
   * change events on potential defaulted bean properties along with a ConversionService used to convert between the
   * Default annotation meta-data value and the bean's property's type.
   *
   * @param bean the bean on which this PropertyChangeListener is registered.
   * @param conversionService ConversionService used to convert between the Default annotation meta-data value
   * and the bean property's type.
   * @see java.beans.Introspector#getBeanInfo(Class)
   * @see java.beans.PropertyDescriptor
   */
  public SetDefaultPropertyChangeListener(final Object bean, final ConversionService conversionService) {
    Assert.notNull(bean, "The bean on which this PropertyChangeListener is registered cannot be null!");

    this.beanPropertyDefaults = new HashMap<PropertyDescriptor, Object>(bean.getClass().getMethods().length);
    this.conversionService = (conversionService != null ? conversionService : new DefaultConversionService());

    try {
      for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();

        if (ObjectUtils.isAnnotationPresent(Default.class, readMethod, writeMethod)) {
          Default defaultAnnotation = writeMethod.getAnnotation(Default.class);
          defaultAnnotation = (defaultAnnotation != null ? defaultAnnotation : readMethod.getAnnotation(Default.class));
          beanPropertyDefaults.put(propertyDescriptor, defaultAnnotation.value());
        }
      }

      Introspector.flushFromCaches(bean.getClass());
    }
    catch (IntrospectionException e) {
      throw new InitializationException(String.format("Failed to initialize... "), e);
    }
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

  /**
   * Decides whether setting the default value on the bean property is necessary when a PropertyChangeEvent occurs.
   * Setting the default value is necessary when the bean property is annotated with @Default annotation meta-data
   * and the new value for the bean property is null.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @return a boolean valued indicating whether setting the default value on the bean property is necessary.
   * @see java.beans.PropertyChangeEvent
   */
  protected boolean isSetDefaultNecessary(final PropertyChangeEvent event) {
    for (Map.Entry<PropertyDescriptor, Object> entry : beanPropertyDefaults.entrySet()) {
      if (entry.getKey().getName().equals(event.getPropertyName())) {
        return is(event.getNewValue()).Null();
      }
    }

    return false;
  }

  /**
   * Sets the default value for the bean property identified in the PropertyChangeEvent.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @see java.beans.PropertyChangeEvent
   */
  protected void setDefaultValue(final PropertyChangeEvent event) {
    for (Map.Entry<PropertyDescriptor, Object> entry : beanPropertyDefaults.entrySet()) {
      if (entry.getKey().getName().equals(event.getPropertyName())) {
        Method writeMethod = entry.getKey().getWriteMethod();
        Class<?> parameterType = ArrayUtils.getFirst(writeMethod.getParameterTypes());

        if (!parameterType.isInstance(entry.getValue())) {
          beanPropertyDefaults.put(entry.getKey(), getConversionService().convert(entry.getValue(), parameterType));
        }

        try {
          writeMethod.invoke(event.getSource(), beanPropertyDefaults.get(entry.getKey()));
          return;
        }
        catch (Exception e) {
          throw new IllegalStateException(String.format("Failed to set property (%1$s) of bean type (%2$s) with value (%3$s)!",
            event.getPropertyName(), event.getSource().getClass().getName(), beanPropertyDefaults.get(entry.getKey())));
        }
      }
    }
  }

  /**
   * Callback handler method for the PropertyChangeEvent fired when the bean property changes and has a default value.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @see java.beans.PropertyChangeEvent
   * @see #isSetDefaultNecessary(java.beans.PropertyChangeEvent)
   * @see #setDefaultValue(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    if (isSetDefaultNecessary(event)) {
      setDefaultValue(event);
    }
  }

}
