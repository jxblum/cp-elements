/*
 * Copyright 2016 Author or Authors.
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

package org.cp.elements.lang.reflect.support;

import java.lang.reflect.Method;

import org.cp.elements.lang.reflect.MethodInterceptor;
import org.cp.elements.lang.reflect.MethodInvocation;

/**
 * The {@link MethodInvokingMethodInterceptor} class is {@link MethodInterceptor} implementation used to invoke
 * the {@link Method} on a specified target {@link Object}.
 *
 * @author John Blum
 * @see java.lang.reflect.Method
 * @see org.cp.elements.lang.reflect.MethodInterceptor
 * @see org.cp.elements.lang.reflect.MethodInvocation
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class MethodInvokingMethodInterceptor implements MethodInterceptor {

  private final Object target;

  /**
   * Factory method to construct an instance of {@link MethodInvokingMethodInterceptor} initialized
   * with the given target {@link Object} on which the {@link Method} invocation will be called.
   *
   * @param target target {@link Object} of the {@link Method} invocation.
   * @return a new instance of {@link MethodInvokingMethodInterceptor} initialized with
   * the given target {@link Object}.
   * @see org.cp.elements.lang.reflect.support.MethodInvokingMethodInterceptor
   * @see #MethodInvokingMethodInterceptor(Object)
   * @see java.lang.Object
   */
  public static MethodInvokingMethodInterceptor newMethodInvokingMethodInterceptor(Object target) {
    return new MethodInvokingMethodInterceptor(target);
  }

  /**
   * Default constructor used to construct an instance of {@link MethodInvokingMethodInterceptor}
   * with no target {@link Object}.
   *
   * This constructor is used to invoke {@link java.lang.reflect.Modifier#STATIC} {@link Method methods}.
   *
   * @see #MethodInvokingMethodInterceptor(Object)
   */
  public MethodInvokingMethodInterceptor() {
    this(null);
  }

  /**
   * Constructs an instance of the {@link MethodInvokingMethodInterceptor} initialized with
   * the given target {@link Object} used in the actual {@link Method} invocation.
   *
   * @param target {@link Object} used as the target of the {@link Method} invocation.
   * @see java.lang.Object
   */
  public MethodInvokingMethodInterceptor(Object target) {
    this.target = target;
  }

  /**
   * @inheritDoc
   */
  @Override
  public Object getTarget() {
    return this.target;
  }

  /**
   * @inheritDoc
   */
  @Override
  public Object intercept(MethodInvocation methodInvocation) {
    return methodInvocation.invoke(getTarget());
  }
}
