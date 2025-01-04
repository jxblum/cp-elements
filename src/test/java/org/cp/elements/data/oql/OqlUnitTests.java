/*
 * Copyright 2017-Present Author or Authors.
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
package org.cp.elements.data.oql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Comparator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.Nameable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Unit Tests for {@link Oql}.
 *
 * @author John Blum
 * @see org.cp.elements.data.oql.Oql
 * @see org.junit.jupiter.api.Test
 * @since 2.0.0
 */
public class OqlUnitTests {

  private static final Set<Person> PEOPLE = Set.of(
    Person.named("Jon", "Doe").withAge(42).asMale(),
    Person.named("Jane", "Doe").withAge(48).asFemale(),
    Person.named("Bob", "Doe").withAge(24).asMale(),
    Person.named("Cookie", "Doe").withAge(8).asFemale(),
    Person.named("Dill", "Doe").withAge(51).asMale(),
    Person.named("Fro", "Doe").withAge(21).asMale(),
    Person.named("Hoe", "Doe").withAge(33).asFemale(),
    Person.named("Joe", "Doe").withAge(12).asMale(),
    Person.named("Lan", "Doe").withAge(47).asMale(),
    Person.named("Moe", "Doe").withAge(19).asMale(),
    Person.named("Pie", "Doe").withAge(16).asFemale(),
    Person.named("Sour", "Doe").withAge(13).asFemale()
  );

  private static final Person[] PEOPLE_ARRAY = PEOPLE.toArray(new Person[0]);

  @Test
  void projection() {

    Oql.Projection<Person, String> projection = Oql.Projection.<Person, String>of(String.class)
      .fromType(Person.class)
      .mappedWith(Person::getName);

    assertThat(projection).isNotNull();
    assertThat(projection.getFromType()).isEqualTo(Person.class);
    assertThat(projection.getType()).isEqualTo(String.class);
    assertThat(projection.map(Person.named("Jon", "Doe"))).isEqualTo("Jon Doe");
  }

  @Test
  void projectionOfNullType() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> Oql.Projection.of(null))
      .withMessage("Type is required")
      .withNoCause();
  }

  @Test
  void projectionWithNullFromType() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> Oql.Projection.of(String.class).fromType(null))
      .withMessage("From type is required")
      .withNoCause();
  }

  @Test
  void projectionWithNullFunction() {

    assertThatIllegalArgumentException()
      .isThrownBy(() -> Oql.Projection.of(String.class).mappedWith(null))
      .withMessage("Mapping Function is required")
      .withNoCause();
  }

  @Test
  void projectionWithSelection() {

    Oql.Projection<Person, String> projection = Oql.Projection.of(String.class);

    projection = Oql.defaultProvider()
      .select(projection)
      .from(PEOPLE)
      .getSelection()
      .getProjection();

    assertThat(projection).isNotNull();
    assertThat(projection.getType()).isEqualTo(String.class);
    assertThat(projection.getFromType()).isEqualTo(Person.class);
  }

  @Test
  void projectionWithoutSelection() {

    Oql.Projection<Person, Person> projection = Oql.Projection.star();

    assertThat(projection).isNotNull();
    assertThat(projection.getType()).isEqualTo(Object.class);
    assertThat(projection.getFromType()).isEqualTo(Object.class);
  }

  @Test
  void queryAll() {

    Set<Person> people = Set.of(
      Person.named("Jon", "Doe"),
      Person.named("Jane", "Doe"),
      Person.named("Pie", "Doe")
    );

    Iterable<Person> result = Oql.defaultProvider()
      .select(Oql.Projection.<Person>star())
      .from(people)
      .execute();

    assertThat(result).isNotNull();
    assertThat(result).isNotSameAs(people);
    assertThat(result).containsAll(people);
  }

  @Test
  void queryProjection() {

    Set<Person> people = Set.of(
      Person.named("Pie", "Doe"),
      Person.named("Jon", "Doe"),
      Person.named("Jane", "Doe")
    );

    Oql.Projection<Person, String> projection = Oql.Projection.<Person, String>of(String.class)
      .mappedWith(Person::getName);

    Iterable<String> result = Oql.defaultProvider()
      .select(projection)
      .from(people)
      .execute();

    assertThat(result).isNotNull();
    assertThat(result).containsExactlyInAnyOrder("Jane Doe", "Jon Doe", "Pie Doe");
  }

  @Test
  void queryProjectionWithOrdering() {

    Set<Person> people = Set.of(
      Person.named("Jane", "Doe").withAge(48),
      Person.named("Pie", "Doe").withAge(16),
      Person.named("Jon", "Doe").withAge(42)
    );

    Oql.Projection<Person, String> projection = Oql.Projection.<Person, String>of(String.class)
      .mappedWith(Person::getName);

    Iterable<String> result = Oql.defaultProvider()
      .select(projection)
      .from(people)
      .orderBy(Comparator.comparing(Person::getAge))
      .execute();

    assertThat(result).isNotNull();
    assertThat(result).containsExactly("Pie Doe", "Jon Doe", "Jane Doe");
  }

  @Test
  void queryProjectionWithOrderingAndFilter() {

    Set<Person> people = Set.of(
      Person.named("Pie", "Doe").withAge(16),
      Person.named("Jon", "Doe").withAge(42),
      Person.named("Jane", "Doe").withAge(48),
      Person.named("Jack", "Handy").withAge(51),
      Person.named("Sandy", "Handy").withAge(47)
    );

    Oql.Projection<Person, String> projection = Oql.Projection.<Person, String>of(String.class)
      .mappedWith(Person::getName);

    Iterable<String> result = Oql.defaultProvider()
      .select(projection)
      .from(people)
      .where(person -> "doe".equalsIgnoreCase(person.getLastName()))
      .and(person -> person.getAge() > 40)
      .orderBy(Comparator.comparing(Person::getAge))
      .descending()
      .execute();

    assertThat(result).isNotNull();
    assertThat(result).containsExactly("Jane Doe", "Jon Doe");
  }

  @Test
  void queryProjectionWithOrCondition() {

    Oql.Projection<Person, String> projection = Oql.Projection.<Person, String>of(String.class)
      .mappedWith(Person::getName);

    Iterable<String> result = Oql.defaultProvider()
      .select(projection)
      .from(PEOPLE)
      .where(person -> person.getAge() < 13)
      .or(person -> person.getAge() > 50)
      .orderBy(Comparator.comparing(Person::getAge))
      .execute();

    assertThat(result).isNotNull();
    assertThat(result).containsExactly("Cookie Doe", "Joe Doe", "Dill Doe");
  }

  @Test
  void countAll() {

    Long count = Oql.defaultProvider()
      .select(Oql.Projection.<Person>star())
      .from(PEOPLE)
      .count();

    assertThat(count).isNotNull();
    assertThat(count).isEqualTo(PEOPLE.size());
  }

  @Test
  void countFemales() {

    Long count = Oql.defaultProvider()
      .select(Oql.Projection.<Person>star())
      .from(PEOPLE)
      .where(Person::isFemale)
      .count();

    assertThat(count).isNotNull();
    assertThat(count).isEqualTo(5L);
  }

  @Test
  void countMatureMales() {

    Long count = Oql.defaultProvider()
      .select(Oql.Projection.<Person>star())
      .from(PEOPLE)
      .where(Person::isMale)
      .and(person -> person.getAge() > 17)
      .count();

    assertThat(count).isNotNull();
    assertThat(count).isEqualTo(6L);
  }

  @Test
  void countOne() {

    Long count = Oql.defaultProvider()
      .select(Oql.Projection.<Person>star())
      .from(PEOPLE)
      .where(person -> person.getName().equalsIgnoreCase("jon doe"))
      .count();

    assertThat(count).isNotNull();
    assertThat(count).isOne();
  }

  @Test
  void countTeenagers() {

    Long count = Oql.defaultProvider()
      .select(Oql.Projection.<Person>star())
      .from(PEOPLE)
      .where(person -> person.getAge() > 12)
      .and(person -> person.getAge() < 17)
      .count();

    assertThat(count).isNotNull();
    assertThat(count).isEqualTo(2L);
  }

  @Test
  void countZero() {

    Long count = Oql.defaultProvider()
      .select(Oql.Projection.<Person>star())
      .from(PEOPLE)
      .where(Person::isNonBinary)
      .count();

    assertThat(count).isNotNull();
    assertThat(count).isZero();
  }

  @Test
  void oqlAsQuery() {

    Query<Person, String> query = Oql.defaultProvider()
      .select(Oql.Projection.<Person, String>of(String.class).mappedWith(Person::getName))
      .from(PEOPLE)
      .where(person -> "doe".equalsIgnoreCase(person.getLastName()))
      .orderBy(Comparator.comparing(Person::getAge)).descending()
      .asQuery();

    assertThat(query).isNotNull();
    assertThat(query.selection()).isNotNull();
    assertThat(query.selection().isDistinct()).isFalse();
    assertThat(query.selection().getProjection()).isNotNull();
    assertThat(query.selection().getProjection().getType()).isEqualTo(String.class);
    assertThat(query.selection().getProjection().getFromType()).isEqualTo(Person.class);
    assertThat(query.getFrom()).isNotNull();
    assertThat(query.getFrom().getCollection()).containsExactlyInAnyOrder(PEOPLE_ARRAY);
    assertThat(query.predicate()).isPresent();
    assertThat(query.orderBy()).isPresent();
    assertThat(query.groupBy()).isNotPresent();
  }

  @Getter
  @ToString
  @EqualsAndHashCode
  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  static class Person implements Comparable<Person>, Nameable<String> {

    static final String NAME_FORMAT = "%s %s";

    static Person named(String firstName, String lastName) {
      return new Person(firstName, lastName);
    }

    private final String firstName;
    private final String lastName;

    private Gender gender;

    private int age;

    @Override
    public int compareTo(Person that) {
      return this.getName().compareTo(that.getName());
    }

    @Override
    public String getName() {
      return NAME_FORMAT.formatted(getFirstName(), getLastName());
    }

    boolean isFemale() {
      return Gender.isFemale(getGender());
    }

    boolean isMale() {
      return Gender.isMale(getGender());
    }

    boolean isNonBinary() {
      return getGender() == null;
    }

    Person asFemale() {
      this.gender = Gender.FEMALE;
      return this;
    }

    Person asMale() {
      this.gender = Gender.MALE;
      return this;
    }

    Person withAge(int age) {
      Assert.isTrue(age > 0, "Person's age must be greater than 0");
      this.age = age;
      return this;
    }
  }

  enum Gender {

    FEMALE, MALE;

    static boolean isFemale(Gender gender) {
      return FEMALE.equals(gender);
    }

    static boolean isMale(Gender gender) {
      return MALE.equals(gender);
    }
  }
}
