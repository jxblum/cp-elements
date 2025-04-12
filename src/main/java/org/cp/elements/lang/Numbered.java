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
package org.cp.elements.lang;

/**
 * Interface defining a contract for {@link Object objects} that can be {@literal numbered}.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @since 2.0.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface Numbered {

  /**
   * Gets the {@link Long number} of this {@link Object}.
   *
   * @return the {@link Long number} of this {@link Object}.
   */
  long getNumber();

}
