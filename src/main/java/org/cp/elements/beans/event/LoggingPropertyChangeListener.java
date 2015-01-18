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
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cp.elements.lang.Assert;

/**
 * The LoggingPropertyChangeListener class is a JavaBeans PropertyChangeListener implementation used to log
 * property change events for properties on the bean for which this listener is registered.
 *
 * @author John J. Blum
 * @see java.beans.PropertyChangeEvent
 * @see java.beans.PropertyChangeListener
 * @see java.util.logging.Level
 * @see java.util.logging.Logger
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class LoggingPropertyChangeListener implements PropertyChangeListener {

  protected static final Level DEFAULT_LOG_LEVEL = Level.FINE;

  private Level logLevel;

  private final Logger logger;

  /**
   * Constructs an instance of the LoggingPropertyChangeListener for logging property change events
   * on the registered bean.
   *
   * @param bean the JavaBean for which property change events will be logged.
   * @see #LoggingPropertyChangeListener(Object, java.util.logging.Level)
   */
  public LoggingPropertyChangeListener(final Object bean) {
    this(bean, DEFAULT_LOG_LEVEL);
  }

  /**
   * Constructs an instance of the LoggingPropertyChangeListener for logging property change events
   * on the registered bean at the given log level.
   *
   * @param bean the JavaBean for which property change events will be logged.
   * @param logLevel the log level at which property change events for the given bean will be logged.
   * @see java.util.logging.Level
   * @see java.util.logging.Logger#getLogger(String)
   */
  public LoggingPropertyChangeListener(final Object bean, final Level logLevel) {
    Assert.notNull(bean, "The Bean upon which to log the property change events cannot be null!");
    this.logger = Logger.getLogger(bean.getClass().getName());
    this.logLevel = logLevel;
  }

  /**
   * Returns the minimum log level at which the bean's logger must be set to in order for property change events
   * originating from the bean to be logged.
   *
   * @return the minimum log Level at which the bean's logger must be set in order for property change events
   * originating from the bean to be logged.
   * @see java.util.logging.Level
   */
  public Level getLogLevel() {
    return logLevel;
  }

  /**
   * Gets a reference to the bean Logger used for logging the bean's property change events.
   *
   * @return a reference to the bean Logger used for logging the bean's property change events.
   * @see java.util.logging.Logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Callback handler method receiving notification of the bean's property change event, which is called every time
   * one of the bean's properties is changed.
   *
   * @param event the PropertyChangeEvent detailing the value change on the bean property.
   * @see java.beans.PropertyChangeEvent
   * @see #getLogger()
   * @see #getLogLevel()
   */
  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    if (getLogger().isLoggable(getLogLevel())) {
      getLogger().log(getLogLevel(), String.format(
        "{ @type = %1$s, @property = %2$s, @oldValue = %2$s, @newValue = %4$s }",
          event.getSource().getClass().getName(), event.getPropertyName(), event.getOldValue(), event.getNewValue()));
    }
  }

}
