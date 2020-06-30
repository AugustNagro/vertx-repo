# (WIP) Vertx Repo

Spring Data-like Repositories for the [Vertx Sql Client](https://github.com/eclipse-vertx/vertx-sql-client).

```java
public class HelloWorld {

  // works with regular classes too
  @Entity
  public record Person(@Id Long id, String firstName, String lastName) {}

  // abstract PersonRepoBase generated with Person's @Entity annotation
  public class PersonRepo extends PersonRepoBase {
    public PersonRepo(PgPool pool) {
      super(pool);
    }
  }

  public static void main(String[] args) {
    PgPool pool = PgPool.pool(...); // configure
    PersonRepo repo = new PersonRepo(pool);
    Future<Long> future = repo.count();
    future.onSuccess(System.out::println);
  }
}
```

Benefits:
* Reduce boilerplate for common CRUD operations. See Repo.java.
* Simplify complex paging-and-sorting operations with the type-safe Spec api.
* Implemented as an Annotation Processor. So no reflection, fast startup, and better performance.
* Small codebase depending only on Vertx itself.

### Maven Coordinates and Javadoc

Not published on maven central yet. Add a dependency on:

```xml
<dependencies>
    <dependency>
        <groupId>com.augustnagro</groupId>
        <artifactId>vertx-repo-pg</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Then add the respective annotation processor:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <release>14</release>
                <compilerArgs>--enable-preview</compilerArgs>
                <annotationProcessorPaths>
                    <path>
                        <groupId>com.augustnagro</groupId>
                        <artifactId>vertx-repo-pg-processor</artifactId>
                        <version>1.0.0-SNAPSHOT</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Currently only Postgres is supported, although it will be easy to add new databases. The minimum java version supported is 11.

## Annotations

1. `@Entity`

Indicates that this type is an Entity, for which vertx-repo will construct a {@link Repo} implementation on the next compile.

An Entity type needs at least one public constructor. If the entity has multiple constructors, then one must be annotated with {@link Deserializer}. In either case, the target constructor must also have one parameter annotated with {@link Id}, which determines the entity's primary key.

This generated class will be created in `target/generated-sources/annotations`, with the same package as its entity, and has convenience methods for common crud operations. It is recommended you subclass the RepoBase classes in a `repos/` package, implementing custom methods methods as you go using the `protected final Pool sql` parameter. This also provides the benefit of consolidating your sql queries into one place.

Repo methods like `deleteAllById(Collection<ID> ids)` are final, and simply call their variant which takes a SqlClient instance (`deleteAllById(SqlClient sql, Collection<ID> ids)`). The later method takes the SqlClient so that it may be used in transactions, and the method itself can be overridden if you choose.

RepoBase classes include public static helper methods that help construct entities from Row and RowSet. Finally, to help build Specs, each repo has public static Expression fields corresponding to the table's column types.

2. `@ImmutableEntity`

Generates an abstract {@link ImmutableRepo} for this type, with all read-only CRUD operations.

3. `@Projection`

Creates a utility Projection class on the next compile for the annotated type. This class, located in `target/generated-sources/annotations` will be given the same package name as the annotated type, and provide static methods to build instances from a vertx RowSet.

The annotated type must have either a single public constructor, or one constructor annotated with {@link Deserializer}.

4. `@Id`

Represents the primary key. One and only one of the target constructor's parameters must be annotated with this.

5. `@Deserializer`

Determines the constructor to be used for building this entity from ResultSets. This annotation is required if there are more than one constructor, and there may only be one @Deserializer annotation on a given entity, otherwise the chosen constructor is indeterminate.


**Java to SQL:**

For all types annotated with `Entity`, `ImmutableEntity`, and `Projection`, the type name maps to the table name. Accessor methods are mapped to column names, and must follow the record naming convention (no `get` prefix). The type and accessor names are translated from camel to snake case. Making this mapping configurable is on the todo list.

**Entity Nesting:**

Nesting of Entities, like `@Entity record Car(@Id id, Engine engine) {}` is not supported. In fact, the only datatypes supported are those listed in the [Vertx Docs](https://vertx.io/docs/vertx-pg-client/java/#_postgresql_type_mapping). Nesting entities has many pitfalls, which heavy users of Spring Data will be aware of. Instead, create a new type with the fields needed from the union, and annotate with `@Projection`. Done!

## Specs

```java
import static com.example.PersonRepo.*;
import static com.augustnagro.vertx.repo.pg.Functions.*;

...

Spec<Person> spec = new SpecBuilder<Person>()
  .where(lower(FIRST_NAME).notEq("raymond"))
  .orderBy(LAST_NAME.asc().seekGreaterThan("Washington"))
  .orderBy(ID.asc().seekGreaterThan(125L))
  .limit(10)
  .build();

personRepo.findAll(spec).onSuccess(people -> System.out.println(people));
```

Specs provide a type-safe way to implement common paging and sorting functionality and support [Seek Pagination](https://blog.jooq.org/2016/08/10/why-most-programmers-get-pagination-wrong/).

Repos and ImmutableRepos have method `findAll(Spec<Entity> spec)`, and Specs can be fluently build using a SpecBuilder.

**Functions:**

Common SQL functions like `LENGTH`, `CURRENT_TIMESTAMP`, `COALESCE`, `TRIM`, and more are found in utility class `com.augustnagro.vertx.repo.pg.Functions`.

**Custom Select Clause:**

If you find yourself needing data from more than one Entity, create a database view. Then make a corresponding ImmutableEntity and execute your Specs on that!

**Sealed Interfaces**

Java doesn't support [sealed interfaces](https://openjdk.java.net/jeps/360) yet, but all the interfaces used in building Specs, like Expression, SqlBuilder, and Predicate, should be considered closed for external implementation.

## Building and Tests

It is difficult to test an annotation processor in its own maven project. So instead this repository has two maven projects; `vertx-repo-tests` is for testing. To run the tests, first run `mvn install` in the `vertx-repo` project. Then start Postgres with your system username. Create a database named `test` and executing `mvn test` in `vertx-repo-tests` will run fine.

## Todo
* Configurable Java -> Sql mapping for identifiers
* should the currentTimestamp, currentTime() functions be Expression<E, Temporal>?
* add support for other databases