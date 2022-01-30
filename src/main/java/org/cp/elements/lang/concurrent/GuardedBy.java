/*
 * Copyright 2011-Present Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cp.elements.lang.concurrent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The {@link GuardedBy} annotation declares that a Java {@link Class}, {@link Field} or {@link Method}
 * is guarded by the {@link String named} {@link Object lock} in a multi-Threaded, highly concurrent application.
 *
 * @author John J. Blum
 * @see java.lang.annotation.Documented
 * @see java.lang.annotation.ElementType#FIELD
 * @see java.lang.annotation.ElementType#METHOD
 * @see java.lang.annotation.ElementType#TYPE
 * @see java.lang.annotation.Inherited
 * @see java.lang.annotation.Retention
 * @see java.lang.annotation.RetentionPolicy#SOURCE
 * @see java.lang.annotation.Target
 * @since 1.0.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@SuppressWarnings("unused")
public @interface GuardedBy {

  /**
   * {@link String Name} of the Java {@link Object lock} used to guard a Java {@link Class}, {@link Field}
   * or {@link Method}.
   *
   * @return a {@link String} containing the {@literal name} of the Java {@link Object lock}.
   */
  String value();

}
