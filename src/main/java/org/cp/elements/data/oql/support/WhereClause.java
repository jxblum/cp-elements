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
package org.cp.elements.data.oql.support;

import java.util.Comparator;
import java.util.function.BiPredicate;

import org.cp.elements.data.oql.Oql;
import org.cp.elements.data.oql.QueryArguments;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;

/**
 * Default implementation of {@link Oql.Where}.
 *
 * @author John Blum
 * @param <S> {@link Class type} of {@link Object objects} in the {@link Iterable collection} to query.
 * @param <T> {@link Class type} of the {@link Object projected objects}.
 * @param from {@link Oql.From} clause in which this {@link Oql.Where} clause is based.
 * @param predicate {@link BiPredicate} constituting the conditions or filter in this {@link Oql.Where} clause.
 * @see java.util.function.BiPredicate
 * @see org.cp.elements.data.oql.Oql
 * @see org.cp.elements.data.oql.Oql.From
 * @see org.cp.elements.data.oql.Oql.Where
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public record WhereClause<S, T>(Oql.From<S, T> from, BiPredicate<QueryArguments, S> predicate)
    implements Oql.Where<S, T> {

  /**
   * Record constructor used to perform additional initialization and argument/state validation.
   *
   * @throws IllegalArgumentException if the {@link Oql.From} clause or {@link BiPredicate} are {@literal null}.
   */
  public WhereClause {
    ObjectUtils.requireObject(from, "From is required");
    ObjectUtils.requireObject(predicate, "Predicate is required");
  }

  /**
   * Factory method used to construct a new {@link WhereClause} with no {@literal query predicated (filter)},
   * thereby returning all results from the query result set.
   *
   * @param <S> {@link Class type} of {@link Object objects} in the {@link Iterable collection} to query.
   * @param <T> {@link Class type} of the {@link Object projected objects}.
   * @param from {@link Oql.From} clause in the OQL query; required.
   * @return a new {@link WhereClause} returning all results in the query result set.
   * @throws IllegalArgumentException if the {@link Oql.From} clause is {@literal null}.
   * @see Oql.From
   */
  @SuppressWarnings("unchecked")
  public static <S, T> WhereClause<S, T> all(@NotNull Oql.From<S, T> from) {
    return where(from, (BiPredicate<QueryArguments, S>) OqlUtils.ACCEPT_ALL_QUERY_PREDICATE);
  }

  /**
   * Factory method used to copy (clone) an existing {@link Oql.Where} clause.
   *
   * @param <S> {@link Class type} of {@link Object objects} in the {@link Iterable collection} to query.
   * @param <T> {@link Class type} of the {@link Object projected objects}.
   * @param where {@link Oql.Where} clause to copy (clone); required.
   * @return a new {@link WhereClause} copied from the given {@link Oql.Where} clause.
   * @throws IllegalArgumentException if the {@link Oql.Where} clause is {@literal null}.
   * @see Oql.Where
   */
  public static <S, T> WhereClause<S, T> copy(@NotNull Oql.Where<S, T> where) {

    Assert.notNull(where, "Where clause to copy is required");

    Oql.From<S, T> from = where.getFrom();
    WhereClause<S, T> copy = where(from, where.getPredicate());

    if (from instanceof FromClause<S, T> fromClause) {
      fromClause.withWhere(copy);
    }

    return copy;
  }

  /**
   * Builder method used to construct a new {@link WhereClause} from the given {@link Oql.From} clause
   * and {@link BiPredicate query predicate}.
   *
   * @param <S> {@link Class type} of {@link Object objects} in the {@link Iterable collection} to query.
   * @param <T> {@link Class type} of the {@link Object projected objects}.
   * @param from {@link Oql.From} clause in the OQL query; required.
   * @param predicate {@link BiPredicate} defining the criteria to match results in the query result set.
   * @return a new {@link WhereClause} defining the {@link BiPredicate query predicate}.
   * @throws IllegalArgumentException if the {@link Oql.From} clause {@link BiPredicate} are {@literal null}.
   * @see java.util.function.BiPredicate
   * @see QueryArguments
   * @see Oql.From
   */
  public static <S, T> WhereClause<S, T> where(@NotNull Oql.From<S, T> from,
      @NotNull BiPredicate<QueryArguments, S> predicate) {

    return new WhereClause<>(from, predicate);
  }

  @Override
  public Oql.From<S, T> getFrom() {
    return from();
  }

  @Override
  public BiPredicate<QueryArguments, S> getPredicate() {
    return predicate();
  }

  @Override
  public Oql.Where<S, T> and(BiPredicate<QueryArguments, S> predicate) {
    return copy(Oql.Where.super.and(predicate));
  }

  @Override
  public Oql.Where<S, T> or(BiPredicate<QueryArguments, S> predicate) {
    return copy(Oql.Where.super.or(predicate));
  }

  @Override
  public Oql.OrderBy<S, T> orderBy(Comparator<T> comparator) {
    return OrderByClause.copy(OrderByClause.of(getFrom(), comparator));
  }

  @Override
  public Oql.GroupBy<S, T> groupBy(Grouping<T> grouping) {
    return GroupByClause.copy(GroupByClause.of(getFrom(), grouping));
  }
}
