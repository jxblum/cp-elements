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
package org.cp.elements.lang.support;

import org.cp.elements.lang.Identifiable;
import org.cp.elements.lang.annotation.Id;

/**
 * {@link IdentifiableSupport} is an abstract base class supporting implementations of
 * the {@link Identifiable} interface.
 *
 * @author John Blum
 * @see java.lang.Comparable
 * @see org.cp.elements.lang.Identifiable
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class IdentifiableSupport<ID extends Comparable<ID>> implements Identifiable<ID> {

  @Id
  private ID id;

  /**
   * @inheritDoc
   */
  @Override
  public ID getId() {
    return this.id;
  }

  /**
   * @inheritDoc
   */
  @Override
  public void setId(ID id) {
    this.id = id;
  }
}
