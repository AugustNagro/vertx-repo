package com.augustnagro.vertx.repo.tests.pg;

import com.augustnagro.vertx.repo.Spec;
import com.augustnagro.vertx.repo.pg.Functions;
import com.augustnagro.vertx.repo.pg.Functions.ExtractField;
import com.augustnagro.vertx.repo.pg.Functions.TrimType;
import com.augustnagro.vertx.repo.pg.Functions.TruncField;
import com.augustnagro.vertx.repo.pg.SpecBuilder;
import com.augustnagro.vertx.repo.tests.pg.repos.TestPersonRepo;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RepoTests {

  private PgPool pool;
  private TestPersonRepo testPersonRepo;

  @BeforeEach
  void setUp() throws URISyntaxException, IOException, ExecutionException, InterruptedException {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
        .setUser(System.getProperty("user.name"))
        .setDatabase("test");
    pool = PgPool.pool(Vertx.vertx(), pgConnectOptions, new PoolOptions().setMaxSize(1));
    testPersonRepo = new TestPersonRepo(pool);

    String testSql = Files.readString(Path.of(getClass().getResource("/test_person.sql").toURI()));
    // since there's no injectable VertxTestContext, need to wait until this future completes..
    pool.query(testSql).execute().toCompletionStage().toCompletableFuture().get();
  }

  @Test
  void count(VertxTestContext ctx) throws Throwable {
    testPersonRepo.count().onComplete(ctx.succeeding(count -> ctx.verify(() -> {
      assertEquals(8L, (long) count);
      ctx.completeNow();
    })));
  }

  @Test
  void existsById(VertxTestContext ctx) {
    testPersonRepo.existsById(2L).onComplete(ctx.succeeding(exists -> ctx.verify(() -> {
      assertTrue(exists);
      ctx.completeNow();
    })));
  }

  @Test
  void doesNotExistById(VertxTestContext ctx) {
    testPersonRepo.existsById(9999L).onComplete(ctx.succeeding(exists -> ctx.verify(() -> {
      assertFalse(exists);
      ctx.completeNow();
    })));
  }

  @Test
  void findAll(VertxTestContext ctx) {
    testPersonRepo.findAll().onComplete(ctx.succeeding(testPeople -> ctx.verify(() -> {
      assertEquals(8, testPeople.size());
      ctx.completeNow();
    })));
  }

  @Test
  void findById(VertxTestContext ctx) {
    testPersonRepo.findById(1L).onComplete(ctx.succeeding(optionalPerson -> ctx.verify(() -> {
      assertTrue(optionalPerson.isPresent());
      assertEquals("Washington", optionalPerson.get().lastName());
      ctx.completeNow();
    })));
  }

  @Test
  void badFindById(VertxTestContext ctx) {
    testPersonRepo.findById(999L).onComplete(ctx.succeeding(optional -> ctx.verify(() -> {
      assertTrue(optional.isEmpty());
      ctx.completeNow();
    })));
  }

  @Test
  void findAllById(VertxTestContext ctx) {
    testPersonRepo.findAllById(List.of(1L, 8L, 999L)).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(2, people.size());
      ctx.completeNow();
    })));
  }

  @Test
  void delete(VertxTestContext ctx) {
    testPersonRepo.countAfterDeletingGeorge().onComplete(ctx.succeeding(count -> ctx.verify(() -> {
      assertEquals(7L, count);
      ctx.completeNow();
    })));
  }

  @Test
  void deleteAll(VertxTestContext ctx) {
    testPersonRepo.findAllById(List.of(1L, 2L))
        .flatMap(testPersonRepo::deleteAll)
        .flatMap(v -> testPersonRepo.count())
        .onComplete(ctx.succeeding(count -> ctx.verify(() -> {
          assertEquals(6L, count);
          ctx.completeNow();
        })));
  }

  @Test
  void deleteAllById(VertxTestContext ctx) {
    testPersonRepo.deleteAllById(List.of(1L, 2L, 3L, 4L))
        .flatMap(v -> testPersonRepo.count())
        .onComplete(ctx.succeeding(count -> ctx.verify(() -> {
          assertEquals(4, count);
          ctx.completeNow();
        })));
  }

  @Test
  void saveWithInsert(VertxTestContext ctx) {
    TestPerson johnSmith = new TestPerson("John", "Smith", null, false, null);
    testPersonRepo.save(johnSmith).onComplete(ctx.succeeding(person -> ctx.verify(() -> {
      assertEquals(johnSmith.lastName(), person.lastName());
      assertNotNull(person.id());
      ctx.completeNow();
    })));
  }

  @Test
  void saveWithUpdate(VertxTestContext ctx) {
    pool.withTransaction(con -> testPersonRepo.findById(con, 1L)
        .map(Optional::get)
        .flatMap(person -> testPersonRepo.save(con,
            new TestPerson("Geo", person.lastName(), person.id(), person.isAdmin(), person.created()))))
        .onComplete(ctx.succeeding(updatedPerson -> ctx.verify(() -> {
          assertEquals(1L, updatedPerson.id());
          assertEquals("Geo", updatedPerson.firstName());
          assertEquals("Washington", updatedPerson.lastName());
          ctx.completeNow();
        })));
  }

  @Test
  void failingSave(VertxTestContext ctx) {
    TestPerson badData = new TestPerson(null, null, null, null, null);
    testPersonRepo.save(badData).onComplete(ctx.failingThenComplete());
  }

  @Test
  void saveAll(VertxTestContext ctx) {
    // 1 update, 2 inserts
    List<TestPerson> toSave = List.of(
        new TestPerson("Geo", "Washington", 1L, true, OffsetDateTime.now()),
        new TestPerson("Kanye", "West", null, false, null),
        new TestPerson("Dwane", "Johnson", null, false, null)
    );

    testPersonRepo.saveAll(toSave).flatMap(savedPeople -> testPersonRepo.count()
        .onComplete(ctx.succeeding(count -> ctx.verify(() -> {
          assertEquals(10L, count);
          for (TestPerson savedPerson : savedPeople) {
            assertNotNull(savedPerson.id());
          }
          ctx.completeNow();
        }))));
  }

  @Test
  void failingSaveAll(VertxTestContext ctx) {
    // 1 update, 2 inserts, 1 bad save
    // the whole process should fail, keeping the count at 8, if saving one fails

    List<TestPerson> toSave = List.of(
        new TestPerson("Geo", "Washington", 1L, true, OffsetDateTime.now()),
        new TestPerson("Kanye", "West", null, false, null),
        new TestPerson("Dwane", "Johnson", null, false, null),
        new TestPerson(null, null, null, true, null)
    );

    testPersonRepo.saveAll(toSave).onComplete(ctx.failing(t ->
        testPersonRepo.count().onComplete(ctx.succeeding(count -> ctx.verify(() -> {
          assertEquals(8L, count);
          ctx.completeNow();
        })))));
  }

  @Test
  void specLimit(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .limit(2)
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(2, people.size());
      ctx.completeNow();
    })));
  }

  @Test
  void specFindGreaterThanCount(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(TestPersonRepo.ID.greaterThan(1))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      for (TestPerson person : people) {
        assertTrue(person.id() > 1);
      }
      ctx.completeNow();
    })));
  }

  @Test
  void specLengthFunction(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.length(TestPersonRepo.LAST_NAME).eq(3))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(1, people.size());
      assertEquals("John", people.get(0).firstName());
      ctx.completeNow();
    })));
  }

  @Test
  void testTrim(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.trim(TrimType.LEADING, "W", TestPersonRepo.LAST_NAME).eq("ashington"))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(1, people.size());
      assertEquals("George", people.get(0).firstName());
      ctx.completeNow();
    })));
  }

  @Test
  void specUpper(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.upper(TestPersonRepo.LAST_NAME).eq("WASHINGTON"))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(1, people.size());
      assertEquals("George", people.get(0).firstName());
      ctx.completeNow();
    })));
  }

  @Test
  void specConcat(VertxTestContext ctx) {
    String gw = "GeorgeWashington";
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(TestPersonRepo.FIRST_NAME.concat(TestPersonRepo.LAST_NAME).eq(gw))
        .where(TestPersonRepo.FIRST_NAME.concat(TestPersonRepo.LAST_NAME).eq(gw))
        .where(TestPersonRepo.FIRST_NAME.concat("Washing").concat("ton").eq(gw))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(1, people.size());
      assertEquals("George", people.get(0).firstName());
      ctx.completeNow();
    })));
  }

  @Test
  void specSort(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(TestPersonRepo.ID.greaterThan(3))
        .orderBy(TestPersonRepo.LAST_NAME.asc())
        .limit(1)
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals("Benjamin", people.get(0).firstName());
      ctx.completeNow();
    })));
  }

  @Test
  void specStartsWith(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.startsWith(Functions.lower(TestPersonRepo.FIRST_NAME), "geo"))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals("Washington", people.get(0).lastName());
      ctx.completeNow();
    })));
  }

  @Test
  void specCheckDate(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(TestPersonRepo.CREATED.lessThan(OffsetDateTime.now().minusDays(10)))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(1, people.size());
      assertEquals("Nagro", people.get(0).lastName());
      ctx.completeNow();
    })));
  }

  @Test
  void specYear(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.extract(ExtractField.YEAR, TestPersonRepo.CREATED).eq(1997))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(1, people.size());
      assertEquals("Nagro", people.get(0).lastName());
      ctx.completeNow();
    })));
  }

  @Test
  void specDateTrunc(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.dateTrunc(TruncField.DAY, TestPersonRepo.CREATED)
            .eq(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS)))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(7, people.size());
      ctx.completeNow();
    })));
  }

  @Test
  void specBetween(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(TestPersonRepo.ID.between(1).and(2))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(2, people.size());
      ctx.completeNow();
    })));
  }

  @Test
  void specOrderByMultiple(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .orderBy(Functions.length(TestPersonRepo.FIRST_NAME).asc())
        .orderBy(TestPersonRepo.ID.desc())
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals("Jay", people.get(0).lastName());
      assertEquals("Adams", people.get(1).lastName());
      ctx.completeNow();
    })));
  }

  @Test
  void specOffset(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .offset(1)
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(7, people.size());
      ctx.completeNow();
    })));
  }

  @Test
  void specSeek(VertxTestContext ctx) {
    String lastLastName = "Jefferson";
    Long lastId = 6L;

    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .orderBy(TestPersonRepo.LAST_NAME.asc().seekGreaterThan(lastLastName))
        .orderBy(TestPersonRepo.ID.asc().seekGreaterThan(lastId))
        .limit(2)
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(2, people.size());
      assertEquals("Madison", people.get(0).lastName());
      assertEquals("Nagro", people.get(1).lastName());
      ctx.completeNow();
    })));
  }

  // for https://github.com/eclipse-vertx/vertx-sql-client/issues/699
  @Test
  void concatOrderBug(VertxTestContext ctx) {
    pool.preparedQuery("SELECT COUNT(*) FROM test_person WHERE (CONCAT(first_name, $1) = $2)")
        .execute(Tuple.of("Washington", "GeorgeWashington"))
        .onComplete(ctx.succeeding(rs -> ctx.verify(() -> {
          assertEquals(1, rs.iterator().next().getLong(0));
          ctx.completeNow();
        })));
  }

  @Test
  void coalesce(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.coalesce(TestPersonRepo.FIRST_NAME, "George").eq("George"))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(2, people.size());
      ctx.completeNow();
    })));
  }

  @Test
  void coalesceManyArgs(VertxTestContext ctx) {
    Spec<TestPerson> spec = new SpecBuilder<TestPerson>()
        .where(Functions.coalesce(TestPersonRepo.FIRST_NAME, TestPersonRepo.LAST_NAME).notEq("Nagro"))
        .build();

    testPersonRepo.findAll(spec).onComplete(ctx.succeeding(people -> ctx.verify(() -> {
      assertEquals(7, people.size());
      ctx.completeNow();
    })));
  }

  // todo tests:
  // coalesce
  //


}
