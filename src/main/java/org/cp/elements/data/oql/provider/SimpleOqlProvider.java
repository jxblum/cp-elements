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
package org.cp.elements.data.oql.provider;

import org.cp.elements.data.oql.Oql;
import org.cp.elements.data.oql.QueryExecutor;
import org.cp.elements.data.oql.support.SelectClause;
import org.cp.elements.lang.annotation.NotNull;

/**
 * Provider implementation for Elements {@link Oql}.
 *
 * @author John Blum
 * @see org.cp.elements.data.oql.Oql
 * @since 2.0.0
 */
public class SimpleOqlProvider implements Oql {

  @Override
  public <S, T> QueryExecutor<S, T> executor() {
    return new SimpleQueryExecutor<>();
  }

  @Override
  public <S, T> Select<S, T> select(@NotNull Projection<S, T> projection) {
    return SelectClause.select(projection);
  }
}
