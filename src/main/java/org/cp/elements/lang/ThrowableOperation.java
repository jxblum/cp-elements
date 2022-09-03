/*
 * Copyright 2016 Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.cp.elements.lang;

import static org.cp.elements.lang.ElementsExceptionsFactory.newThrowableOperationException;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.NullSafe;
import org.cp.elements.lang.annotation.Nullable;

/**
 * Operation capable of throwing a {@link Throwable} during execution.
 *
 * Implementation of the {@link Callable}, {@link Consumer}, {@link Runnable} and {@link Supplier} interfaces.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see java.lang.Runnable
 * @see java.util.concurrent.Callable
 * @see java.util.function.Consumer
 * @see java.util.function.Supplier
 * @since 1.0.0
 */
@FunctionalInterface
public interface ThrowableOperation<T> extends Callable<T>, Consumer<T>, Runnable, Supplier<T> {

  @SuppressWarnings("unused")
  ThrowableOperation<Object> NO_OP = args -> null;

  /**
   * Factory method used to construct a new instance of {@link ThrowableOperation}
   * from the given, required {@link VoidReturningThrowableOperation}.
   *
   * @param <T> {@link Class type} of the {@link Object return value}.
   * @param operation {@link VoidReturningThrowableOperation} to convert into a {@link ThrowableOperation}.
   * @return a new {@link ThrowableOperation} from the {@link VoidReturningThrowableOperation}
   * @throws IllegalArgumentException if the {@link VoidReturningThrowableOperation} is {@literal null}.
   * @see VoidReturningThrowableOperation
   */
  static <T> ThrowableOperation<T> from(@NotNull VoidReturningThrowableOperation operation) {

    Assert.notNull(operation, "VoidReturningThrowableOperation is required");

    return args -> {
      operation.run(args);
      return null;
    };
  }

  /**
   * Alias for {@link #run(Object...)}.
   *
   * Ignores any return value computed and returned from {@link #run(Object...)}.
   *
   * @param target {@link Object} processed by {@literal this} operation.
   * @throws RuntimeException wrapping the {@link Throwable cause} if {@literal this} operation results in an error.
   * @see java.util.function.Consumer#accept(Object)
   * @see #run(Object...)
   */
  @Override
  default void accept(T target) {

    try {
      run(target);
    }
    catch (Throwable cause) {
      throw newThrowableOperationException(cause, "Accept failed to operate on target [%s]", target);
    }
  }

  /**
   * Alias for {@link #run(Object...)}.
   *
   * @return the {@link Object result} of {@literal this} operation.
   * @throws Exception wrapping the {@link Throwable cause} if {@literal this} operation results in an error.
   * @see java.util.concurrent.Callable#call()
   * @see #run(Object...)
   */
  @Override
  default T call() throws Exception {

    try {
      return run(ObjectUtils.EMPTY_OBJECT_ARRAY);
    }
    catch (Throwable cause) {
      throw newThrowableOperationException(cause, "Call failed to complete operation");
    }
  }

  /**
   * Alias for {@link #run(Object...)}.
   *
   * @return the {@link Object result} of {@literal this} operation.
   * @throws RuntimeException wrapping the {@link Throwable cause} if {@literal this} operation results in an error.
   * @see java.util.function.Supplier#get()
   * @see #run(Object...)
   */
  @Override
  default T get() {

    try {
      return run(ObjectUtils.EMPTY_OBJECT_ARRAY);
    }
    catch (Throwable cause) {
      throw newThrowableOperationException(cause, "Get failed to return the result of the operation");
    }
  }

  /**
   * Alias for {@link #run(Object...)}.
   *
   * Accepts no arguments and ignores the {@link Object result} of the operation.
   *
   * @throws RuntimeException wrapping the {@link Throwable cause} if {@literal this} operation results in an error.
   * @see java.lang.Runnable#run()
   * @see #run(Object...)
   */
  @Override
  default void run() {

    try {
      run(ObjectUtils.EMPTY_OBJECT_ARRAY);
    }
    catch (Throwable cause) {
      throw newThrowableOperationException(cause, "Run failed to complete operation");
    }
  }

  /**
   * Performs an operation on the given, optional arguments.
   *
   * @param args optional array of {@link Object arguments} to process.
   * @return the {@link Object result} of the operation.
   * @throws Throwable if the operation fails.
   */
  T run(Object... args) throws Throwable;

  /**
   * Builder method used to compose this {@link ThrowableOperation} with the given {@link ThrowableOperation}.
   *
   * @param operation {@link ThrowableOperation} to compose with this {@link ThrowableOperation}.
   * @return a composed {@link ThrowableOperation} consisting of this {@link ThrowableOperation}
   * and the given {@link ThrowableOperation}. If the given {@link ThrowableOperation} is {@literal null},
   * then {@literal this} {@link ThrowableOperation} is returned.
   * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Composite Software Design Pattern</a>
   */
  @NullSafe
  default @NotNull ThrowableOperation<T> andThen(@Nullable ThrowableOperation<T> operation) {

    return operation != null
      ? arguments -> operation.run(this.run(arguments))
      : this;
  }

  /**
   * Interface defining a contract for implementing {@link Object Objects} that perform an operation
   * capable of throwing a {@link Throwable}.
   */
  interface VoidReturningThrowableOperation {
    void run(Object... args) throws Throwable;
  }
}
