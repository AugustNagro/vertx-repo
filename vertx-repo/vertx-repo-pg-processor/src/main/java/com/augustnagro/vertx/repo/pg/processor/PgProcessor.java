package com.augustnagro.vertx.repo.pg.processor;

import com.augustnagro.vertx.repo.Deserializer;
import com.augustnagro.vertx.repo.Entity;
import com.augustnagro.vertx.repo.Id;
import com.augustnagro.vertx.repo.ImmutableEntity;
import com.augustnagro.vertx.repo.Projection;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@SupportedAnnotationTypes("com.augustnagro.vertx.repo.*")
public class PgProcessor extends AbstractProcessor {

  private static final Set<Class<? extends Annotation>> TYPE_ANNOTATIONS =
      Set.of(Projection.class, ImmutableEntity.class, Entity.class);

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      for (Element e : roundEnv.getElementsAnnotatedWithAny(TYPE_ANNOTATIONS)) {
        if (e.getAnnotation(Projection.class) != null) {
          buildProjection(e, roundEnv);
        } else {
          boolean buildImmutable = e.getAnnotation(ImmutableEntity.class) != null;
          buildRepo(e, buildImmutable, roundEnv);
        }
      }
    } catch (Exception e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }
    return true;
  }

  private void buildRepo(Element projection, boolean buildImmutable, RoundEnvironment roundEnv) throws IOException {
    if (!(projection instanceof TypeElement))
      throw new RuntimeException("@Entity or @ImmutableEntity annotation for " + projection.getSimpleName()
                                 + " must be on a TypeElement (class, record, interface, etc)");
    TypeElement projectionType = (TypeElement) projection;

    String className = projectionType.getQualifiedName().toString();
    String packageName = packageName(className);
    String simpleClassName = projectionType.getSimpleName().toString();
    String tableName = camelToSnakeCase(simpleClassName);
    String repoClassName = simpleClassName + "RepoBase";
    if (packageName != null) repoClassName = packageName + "." + repoClassName;
    String repoSimpleClassName = simpleClassName + "RepoBase";

    JavaFileObject src = processingEnv.getFiler().createSourceFile(repoClassName);
    try (PrintWriter out = new PrintWriter(src.openWriter(), false)) {

      if (packageName != null) {
        out.println("package " + packageName + ";");
        out.println();
      }

      ExecutableElement constructor = constructor(projectionType);
      String idParamName = null;
      String idColumnName = null;
      String idType = null;
      StringJoiner newInstanceWithIdParams = new StringJoiner(", ", "(", ")");
      List<? extends VariableElement> parameters = constructor.getParameters();
      int numParameters = parameters.size();
      String[] paramNames = new String[numParameters];
      String[] columnNames = new String[numParameters];
      String[] simpleParamTypes = new String[numParameters];
      for (int i = 0; i < numParameters; ++i) {
        VariableElement param = parameters.get(i);
        String paramName = param.getSimpleName().toString();
        paramNames[i] = paramName;
        String columnName = camelToSnakeCase(paramName);
        columnNames[i] = columnName;
        String paramType = param.asType().toString();
        int lastDot = paramType.lastIndexOf('.');
        simpleParamTypes[i] = lastDot == -1 ? paramType : paramType.substring(lastDot + 1);
        if (param.getAnnotation(Id.class) != null) {
          idParamName = paramName;
          idColumnName = columnName;
          switch (paramType) {
            case "java.lang.Long":
              idType = "Long";
              break;
            case "java.lang.Integer":
              idType = "Integer";
              break;
            case "java.lang.String":
              idType = "String";
              break;
            default:
              throw new IllegalArgumentException("@Id params can only be of type Long, Integer, or String");
          }
          newInstanceWithIdParams.add("id");
        } else {
          newInstanceWithIdParams.add("entity." + paramName + "()");
        }
      }
      if (idParamName == null) throw new RuntimeException("@Repo requires @Id on a constructor parameter");
      String getId = "entity." + idParamName + "()";

      String repoInterfaceImport;
      String repoInterface;
      if (buildImmutable) {
        repoInterfaceImport = "import com.augustnagro.vertx.repo.ImmutableRepo;";
        repoInterface = "ImmutableRepo<" + simpleClassName + ", " + idType + ">";
      } else {
        repoInterfaceImport = "import com.augustnagro.vertx.repo.Repo;";
        repoInterface = "Repo<" + simpleClassName + ", " + idType + ">";
      }

      out.println("import java.util.*;");
      out.println("import java.util.stream.Collector;");
      out.println("import java.util.stream.Collectors;");
      out.println();
      out.println("import io.vertx.core.CompositeFuture;");
      out.println("import io.vertx.core.Future;");
      out.println("import io.vertx.sqlclient.SqlClient;");
      out.println("import io.vertx.pgclient.PgPool;");
      out.println("import io.vertx.sqlclient.SqlResult;");
      out.println("import io.vertx.sqlclient.Tuple;");
      out.println("import io.vertx.sqlclient.Row;");
      out.println("import io.vertx.sqlclient.RowSet;");
      out.println("import io.vertx.sqlclient.RowIterator;");
      out.println("import io.vertx.sqlclient.data.Numeric;");
      out.println("import java.time.LocalDate;");
      out.println("import java.time.LocalTime;");
      out.println("import java.time.OffsetTime;");
      out.println("import java.time.LocalDateTime;");
      out.println("import java.time.OffsetDateTime;");
      out.println("import io.vertx.core.buffer.Buffer;");
      out.println("import io.vertx.core.json.JsonObject;");
      out.println("import io.vertx.core.json.JsonArray;");
      out.println("import io.vertx.pgclient.data.Interval;");
      out.println("import io.vertx.pgclient.data.Point;");
      out.println("import io.vertx.pgclient.data.Circle;");
      out.println("import io.vertx.pgclient.data.Polygon;");
      out.println("import io.vertx.pgclient.data.Path;");
      out.println("import io.vertx.pgclient.data.Box;");
      out.println("import io.vertx.pgclient.data.LineSegment;");
      out.println("import io.vertx.pgclient.data.Line;");
      out.println();
      out.println(repoInterfaceImport);
      out.println("import com.augustnagro.vertx.repo.CollectorUtil;");
      out.println("import com.augustnagro.vertx.repo.pg.Expression;");
      out.println("import com.augustnagro.vertx.repo.pg.NumberExpression;");
      out.println("import com.augustnagro.vertx.repo.pg.StringExpression;");
      out.println("import com.augustnagro.vertx.repo.pg.Predicate;");
      out.println("import com.augustnagro.vertx.repo.Spec;");
      out.println("import com.augustnagro.vertx.repo.pg.SpecBuilder;");
      out.println("import " + className + ";");
      out.println();
      out.println("public abstract class " + repoSimpleClassName + " implements " + repoInterface + " {");
      out.println();

      for (int i = 0; i < columnNames.length; ++i) {
        String columnName = columnNames[i];
        String paramType = simpleParamTypes[i];
        String columnExpression;
        switch (paramType) {
          case "Long":
          case "Double":
          case "Integer":
          case "Byte":
          case "Short":
            columnExpression = "  public static final NumberExpression<" + simpleClassName + "> " +
                               columnName.toUpperCase() + " = NumberExpression.of(\"" + columnName + "\");";
            break;
          case "String":
            columnExpression = "  public static final StringExpression<" + simpleClassName + "> " +
                               columnName.toUpperCase() + " = StringExpression.of(\"" + columnName + "\");";
            break;
          case "Boolean":
            columnExpression = "  public static final Predicate<" + simpleClassName + "> " +
                               columnName.toUpperCase() + " = Predicate.of(\"" + columnName + "\");";
            break;
          default:
            columnExpression = "  public static final Expression<" + simpleClassName + ", " + paramType + "> " +
                               columnName.toUpperCase() + " = Expression.of(\"" + columnName + "\");";
        }
        out.println(columnExpression);
      }
      out.println();


      out.println("  protected final PgPool sql;");
      out.println();
      out.println("  public " + repoSimpleClassName + "(PgPool sql) {");
      out.println("    this.sql = sql;");
      out.println("  }");
      out.println();

      out.println(builderMethods(constructor, simpleClassName, repoSimpleClassName));

      out.println("  public static " + simpleClassName + " withId(" + idType + " id, "
                  + simpleClassName + " entity) {");
      out.println("    return new " + simpleClassName + newInstanceWithIdParams + ";");
      out.println("  }");
      out.println();

      out.println("  public static " + simpleClassName + " withId(Row row, " + simpleClassName + " entity) {");
      out.println("    return withId(row.get" + idType + "(\"" + idColumnName + "\"), entity);");
      out.println("  }");
      out.println();

      out.println("  @Override");
      out.println("  public final Future<Long> count() {");
      out.println("    return count(sql);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<Long> count(SqlClient sql) {");
      out.println("    return sql.preparedQuery(\"SELECT COUNT(*) FROM " + tableName + "\")");
      out.println("        .execute()");
      out.println("        .map(rowSet -> rowSet.iterator().next().getLong(0));");
      out.println("  }");
      out.println();

      String existsByIdQuery = "SELECT * FROM " + tableName + " WHERE " + idColumnName + " = $1";
      out.println("  @Override");
      out.println("  public final Future<Boolean> existsById(" + idType + " id) {");
      out.println("    return existsById(sql, id);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<Boolean> existsById(SqlClient sql, " + idType + " id) {");
      out.println("    return sql.preparedQuery(\"" + existsByIdQuery + "\")");
      out.println("        .execute(Tuple.of(id))");
      out.println("        .map(rowSet -> rowSet.iterator().hasNext());");
      out.println("  }");
      out.println();

      String findAllQuery = "SELECT * FROM " + tableName;
      out.println("  @Override");
      out.println("  public final Future<List<" + simpleClassName + ">> findAll() {");
      out.println("    return findAll(sql);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<List<" + simpleClassName + ">> findAll(SqlClient sql) {");
      out.println("    return sql.preparedQuery(\"" + findAllQuery + "\")");
      out.println("        .collecting(listCollector())");
      out.println("        .execute()");
      out.println("        .map(SqlResult::value);");
      out.println("  }");
      out.println();

      out.println("  @Override");
      out.println("  public final Future<List<" + simpleClassName + ">> findAll(Spec<" + simpleClassName + "> spec) {");
      out.println("    return findAll(sql, spec);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<List<" + simpleClassName + ">> findAll(SqlClient sql, " +
                  "Spec<" + simpleClassName + "> spec) {");
      out.println("    return sql.preparedQuery(\"" + findAllQuery + " \" + spec.sql())");
      out.println("        .collecting(listCollector())");
      out.println("        .execute(spec.tuple())");
      out.println("        .map(SqlResult::value);");
      out.println("  }");
      out.println();

      String findByIdQuery = "SELECT * FROM " + tableName + " WHERE " + idColumnName + " = $1";
      out.println("  @Override");
      out.println("  public final Future<Optional<" + simpleClassName + ">> findById(" + idType + " id) {");
      out.println("    return findById(sql, id);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<Optional<" + simpleClassName + ">> findById(SqlClient sql, " + idType + " id) {");
      out.println("    return sql.preparedQuery(\"" + findByIdQuery + "\")");
      out.println("        .execute(Tuple.of(id))");
      out.println("        .map(rowSet -> rowSet.iterator().hasNext()");
      out.println("            ? Optional.of(buildSingle(rowSet))");
      out.println("            : Optional.<" + simpleClassName + ">empty());");
      out.println("  }");
      out.println();

      String findAllByIdQuery = "SELECT * FROM " + tableName + " WHERE " + idColumnName + " = ANY($1)";
      out.println("  @Override");
      out.println("  public final Future<List<" + simpleClassName + ">> findAllById(Collection<" + idType + "> ids) {");
      out.println("    return findAllById(sql, ids);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<List<" + simpleClassName + ">> findAllById(SqlClient sql, Collection<" + idType +
                  "> ids) {");
      out.println("    return sql.preparedQuery(\"" + findAllByIdQuery + "\")");
      out.println("        .collecting(listCollector())");
      out.println("        .execute(Tuple.of(ids.toArray(new " + idType + "[0])))");
      out.println("        .map(SqlResult::value);");
      out.println("  }");
      out.println();

      if (buildImmutable) {
        out.println("}");
        return;
      }

      String deleteQuery = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = $1";
      out.println("  @Override");
      out.println("  public final Future<Void> delete(" + simpleClassName + " entity) {");
      out.println("    return delete(sql, entity);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<Void> delete(SqlClient sql, " + simpleClassName + " entity) {");
      out.println("    return sql.preparedQuery(\"" + deleteQuery + "\")");
      out.println("        .execute(Tuple.of(" + getId + "))");
      out.println("        .mapEmpty();");
      out.println("  }");
      out.println();

      String deleteAllQuery = "DELETE FROM " + tableName;
      out.println("  @Override");
      out.println("  public final Future<Void> deleteAll() {");
      out.println("    return deleteAll(sql);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<Void> deleteAll(SqlClient sql) {");
      out.println("    return sql.preparedQuery(\"" + deleteAllQuery + "\")");
      out.println("        .execute()");
      out.println("        .mapEmpty();");
      out.println("  }");
      out.println();

      String deleteAllOfQuery = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ANY($1)";
      String idArray = idType + "[] ids = new " + idType + "[entities.size()];";
      out.println("  @Override");
      out.println("  public final Future<Void> deleteAll(Collection<" + simpleClassName + "> entities) {");
      out.println("    return deleteAll(sql, entities);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<Void> deleteAll(SqlClient sql, Collection<" + simpleClassName + "> entities) {");
      out.println("    " + idArray);
      out.println("    int i = 0;");
      out.println("    for (" + simpleClassName + " entity : entities) {");
      out.println("      ids[i] = " + getId + ";");
      out.println("      ++i;");
      out.println("    }");
      out.println("    return sql.preparedQuery(\"" + deleteAllOfQuery + "\")");
      out.println("        .execute(Tuple.of(ids))");
      out.println("        .mapEmpty();");
      out.println("  }");
      out.println();

      out.println("  @Override");
      out.println("  public final Future<Void> deleteAllById(Collection<" + idType + "> ids) {");
      out.println("    return deleteAllById(sql, ids);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<Void> deleteAllById(SqlClient sql, Collection<" + idType + "> ids) {");
      out.println("    return sql.preparedQuery(\"" + deleteAllOfQuery + "\")");
      out.println("        .execute(Tuple.of(ids.toArray(new " + idType + "[0])))");
      out.println("        .mapEmpty();");
      out.println("  }");
      out.println();

      StringJoiner insertKeys = new StringJoiner(", ");
      StringJoiner insertValues = new StringJoiner(", ");
      StringJoiner insertTupleSj = new StringJoiner(", ", "Tuple.of(", ")");
      int updateIdPosition = 1;
      StringJoiner updateAssignmentSj = new StringJoiner(", ");
      StringJoiner updateTupleSj = new StringJoiner(", ", "Tuple.of(", ")");
      for (int i = 0, j = 1; i < paramNames.length; ++i) {
        String name = paramNames[i];
        String columnName = camelToSnakeCase(name);
        updateTupleSj.add("entity." + name + "()");
        if (name.equals(idParamName)) {
          updateIdPosition = i + 1;
          continue;
        }
        insertKeys.add(columnName);
        insertValues.add("$" + j);
        insertTupleSj.add("entity." + name + "()");
        updateAssignmentSj.add(columnName + " = $" + (i + 1));
        ++j;
      }
      String insertQuery = "INSERT INTO " + tableName + " (" + insertKeys + ") VALUES ("
                           + insertValues + ") RETURNING " + idColumnName;
      String updateQuery = "UPDATE " + tableName + " SET " + updateAssignmentSj +
                           " WHERE " + idColumnName + " = $" + updateIdPosition;

      String insertTuple = insertTupleSj.toString();
      String updateTuple = updateTupleSj.toString();

      out.println("  @Override");
      out.println("  public final Future<" + simpleClassName + "> save(" + simpleClassName + " entity) {");
      out.println("    return save(sql, entity);");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<" + simpleClassName + "> save(SqlClient sql, " + simpleClassName + " entity) {");
      out.println("    if (" + getId + " == null) {");
      out.println("      return sql.preparedQuery(\"" + insertQuery + "\")");
      out.println("          .execute(" + insertTuple + ")");
      out.println("          .map(rs -> withId(rs.iterator().next(), entity));");
      out.println("    } else {");
      out.println("      return sql.preparedQuery(\"" + updateQuery + "\")");
      out.println("          .execute(" + updateTuple + ")");
      out.println("          .map(entity);");
      out.println("    }");
      out.println("  }");
      out.println();

      out.println("  @Override");
      out.println("  public final Future<List<" + simpleClassName + ">> saveAll(Collection<" + simpleClassName +
                  "> entities) {");
      out.println("    return sql.withTransaction(con -> saveAll(con, entities));");
      out.println("  }");
      out.println();
      out.println("  @Override");
      out.println("  public Future<List<" + simpleClassName + ">> saveAll(SqlClient sql, Collection<"
                  + simpleClassName + "> entities) {");
      out.println("    ArrayList<Tuple> insertBatch = new ArrayList<>();");
      out.println("    ArrayList<TestPerson> entitiesToInsert = new ArrayList<>();");
      out.println("    ArrayList<Tuple> updateBatch = new ArrayList<>();");
      out.println("    ArrayList<" + simpleClassName + "> updatedEntities = new ArrayList<>();");
      out.println("    for (" + simpleClassName + " entity : entities) {");
      out.println("      if (" + getId + " == null) {");
      out.println("        entitiesToInsert.add(entity);");
      out.println("        insertBatch.add(" + insertTuple + ");");
      out.println("      } else {");
      out.println("        updatedEntities.add(entity);");
      out.println("        updateBatch.add(" + updateTuple + ");");
      out.println("      }");
      out.println("    }");
      out.println("    Iterator<TestPerson> insertIter = entitiesToInsert.iterator();");
      out.println("    int totalSize = insertBatch.size() + updateBatch.size();");
      out.println("    boolean insertBatchBigger = insertBatch.size() > updateBatch.size();");
      out.println("    int insertCollectorSize;");
      out.println("    if (insertBatchBigger) {");
      out.println("      insertCollectorSize = totalSize;");
      out.println("    } else {");
      out.println("      insertCollectorSize = insertBatch.size();");
      out.println("      updateBatch.ensureCapacity(totalSize);");
      out.println("    }");
      out.println();
      out.println("    return sql.preparedQuery(\"" + insertQuery + "\")");
      out.println("        .collecting(Collectors.mapping(row -> withId(row, insertIter.next()), " +
                  "CollectorUtil.toList(insertCollectorSize)))");
      out.println("        .executeBatch(insertBatch)");
      out.println("        .map(SqlResult::value)");
      out.println("        .flatMap(insertedEntities -> sql");
      out.println("            .preparedQuery(\"" + updateQuery + "\")");
      out.println("            .executeBatch(updateBatch)");
      out.println("            .map(rs -> {");
      out.println("              if (insertBatchBigger) {");
      out.println("                insertedEntities.addAll(updatedEntities);");
      out.println("                return insertedEntities;");
      out.println("              } else {");
      out.println("                updatedEntities.addAll(insertedEntities);");
      out.println("                return updatedEntities;");
      out.println("              }");
      out.println("            }));");
      out.println("  }");
      out.println();

      out.println("}");
    }
  }

  private void buildProjection(Element projection, RoundEnvironment roundEnv) throws IOException {
    if (!(projection instanceof TypeElement))
      throw new RuntimeException("@Projection annotation in wrong place for " + projection.getSimpleName());
    TypeElement projectionType = (TypeElement) projection;

    String className = projectionType.getQualifiedName().toString();
    String packageName = packageName(className);
    String simpleClassName = projectionType.getSimpleName().toString();
    String projectionClassName = simpleClassName + "Projection";
    if (packageName != null) projectionClassName = packageName + "." + projectionClassName;
    String projectionSimpleClassName = simpleClassName + "Projection";

    JavaFileObject src = processingEnv.getFiler().createSourceFile(projectionClassName);
    try (PrintWriter out = new PrintWriter(src.openWriter(), false)) {

      if (packageName != null) {
        out.println("package " + packageName + ";");
        out.println();
      }

      out.println("import java.util.List;");
      out.println("import java.util.ArrayList;");
      out.println("import java.util.stream.Collector;");
      out.println("import java.util.stream.Collectors;");
      out.println();
      out.println("import io.vertx.sqlclient.Row;");
      out.println("import io.vertx.sqlclient.RowSet;");
      out.println("import io.vertx.sqlclient.RowIterator;");
      out.println("import io.vertx.sqlclient.data.Numeric;");
      out.println("import java.time.LocalDate;");
      out.println("import java.time.LocalTime;");
      out.println("import java.time.OffsetTime;");
      out.println("import java.time.LocalDateTime;");
      out.println("import java.time.OffsetDateTime;");
      out.println("import io.vertx.core.buffer.Buffer;");
      out.println("import io.vertx.core.json.JsonObject;");
      out.println("import io.vertx.core.json.JsonArray;");
      out.println("import io.vertx.pgclient.data.Interval;");
      out.println("import io.vertx.pgclient.data.Point;");
      out.println("import io.vertx.pgclient.data.Circle;");
      out.println("import io.vertx.pgclient.data.Polygon;");
      out.println("import io.vertx.pgclient.data.Path;");
      out.println("import io.vertx.pgclient.data.Box;");
      out.println("import io.vertx.pgclient.data.LineSegment;");
      out.println("import io.vertx.pgclient.data.Line;");
      out.println();
      out.println("import com.augustnagro.vertx.repo.CollectorUtil;");
      out.println("import " + className + ";");
      out.println();

      out.println("public class " + projectionSimpleClassName + " {");
      out.println();

      ExecutableElement constructor = constructor(projectionType);
      out.println(builderMethods(constructor, simpleClassName, projectionSimpleClassName));

      out.println("}");
    }
  }

  /**
   * If idType and idColumn are null, the withId helper is not generated.
   */
  private static String builderMethods(ExecutableElement constructor, String simpleClassName,
                                       String simpleGeneratedClassName) {

    // code to make a new instance from a Row
    StringJoiner constructorParams = new StringJoiner(", ");
    for (VariableElement param : constructor.getParameters()) {
      String columnName = camelToSnakeCase(param.getSimpleName().toString());
      String paramType = param.asType().toString();
      String methodPart;
      switch (paramType) {
        case "java.lang.Boolean":
          methodPart = "getBoolean(";
          break;
        case "java.lang.Short":
          methodPart = "getShort(";
          break;
        case "java.lang.Integer":
          methodPart = "getInteger(";
          break;
        case "java.lang.Long":
          methodPart = "getLong(";
          break;
        case "java.lang.Float":
          methodPart = "getFloat(";
          break;
        case "java.lang.Double":
          methodPart = "getDouble(";
          break;
        case "java.lang.String":
          methodPart = "getString(";
          break;
        case "io.vertx.sqlclient.data.Numeric":
        case "java.lang.Number":
          methodPart = "get(Numeric.class, ";
          break;
        case "java.util.UUID":
          methodPart = "getUUID(";
          break;
        case "java.time.LocalDate":
          methodPart = "getLocalDate(";
          break;
        case "java.time.LocalTime":
          methodPart = "getLocalTime(";
          break;
        case "java.time.OffsetTime":
          methodPart = "getOffsetTime(";
          break;
        case "java.time.LocalDateTime":
          methodPart = "getLocalDateTime(";
          break;
        case "java.time.OffsetDateTime":
          methodPart = "getOffsetDateTime(";
          break;
        case "io.vertx.pgclient.data.Interval":
          methodPart = "get(Interval.class, ";
          break;
        case "io.vertx.core.buffer.Buffer":
          methodPart = "getBuffer(";
          break;
        case "io.vertx.core.json.JsonObject":
          methodPart = "getJsonObject(";
          break;
        case "io.vertx.core.json.JsonArray":
          methodPart = "getJsonArray(";
          break;
        case "io.vertx.pgclient.data.Point":
          methodPart = "get(Point.class, ";
          break;
        case "io.vertx.pgclient.data.Line":
          methodPart = "get(Line.class, ";
          break;
        case "io.vertx.pgclient.data.LineSegment":
          methodPart = "get(LineSegment.class, ";
          break;
        case "io.vertx.pgclient.data.Box":
          methodPart = "get(Box.class, ";
          break;
        case "io.vertx.pgclient.data.Path":
          methodPart = "get(Path.class, ";
          break;
        case "io.vertx.pgclient.data.Polygon":
          methodPart = "get(Polygon.class, ";
          break;
        case "io.vertx.pgclient.data.Circle":
          methodPart = "get(Circle.class, ";
          break;
        default:
          throw new RuntimeException("Unsupported param type of " + paramType);
      }
      constructorParams.add("row." + methodPart + "\"" + columnName + "\")");
    }
    String newInstanceCode = "new " + simpleClassName + "(" + constructorParams + ")";

    return "  public static " + simpleClassName + " buildSingle(Row row) {\n" +
           "    return " + newInstanceCode + ";\n" +
           "  }\n" +
           "\n" +
           "  public static " + simpleClassName + " buildSingle(RowSet<Row> rowSet) {\n" +
           "    RowIterator<Row> iterator = rowSet.iterator();\n" +
           "    if (!iterator.hasNext()) throw new IllegalArgumentException(\"RowSet is empty\");\n" +
           "    Row row = iterator.next();\n" +
           "    return buildSingle(row);\n" +
           "  }\n" +
           "\n" +
           "  public static List<" + simpleClassName + "> build(RowSet<Row> rowSet) {\n" +
           "    ArrayList<" + simpleClassName + "> res = new ArrayList<>(rowSet.size());\n" +
           "    for (; rowSet != null; rowSet = rowSet.next()) {\n" +
           "      for (Row row : rowSet) {\n" +
           "        res.add(buildSingle(row));\n" +
           "      }\n" +
           "    }\n" +
           "    return res;\n" +
           "  }\n" +
           "\n" +
           "  public static Collector<Row, ?, List<" + simpleClassName + ">> listCollector() {\n" +
           "    return Collectors.mapping(" + simpleGeneratedClassName + "::buildSingle, Collectors.toList());\n" +
           "  }\n" +
           "\n" +
           "  public static Collector<Row, ?, List<" + simpleClassName + ">> listCollector(int expectedSize) {\n" +
           "    return Collectors.mapping(" + simpleGeneratedClassName +
           "::buildSingle, CollectorUtil.toList(expectedSize));\n" +
           "  }\n";
  }

  private static ExecutableElement constructor(TypeElement projectionType) {
    ExecutableElement constructor = null;
    for (Element enclosedElement : projectionType.getEnclosedElements()) {
      if (!(enclosedElement instanceof ExecutableElement)) continue;
      ExecutableElement method = (ExecutableElement) enclosedElement;
      if (method.getSimpleName().toString().equals("<init>")
          && (constructor == null || method.getAnnotation(Deserializer.class) != null)) {
        constructor = method;
      }
    }
    if (constructor == null) throw new RuntimeException(
        "No constructor annotated with @Deserializer for class " + projectionType.getQualifiedName());
    return constructor;
  }

  private static String camelToSnakeCase(String s) {
    StringBuilder sb = new StringBuilder();
    sb.append(Character.toLowerCase(s.charAt(0)));
    for (int i = 1; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (Character.isUpperCase(c)) {
        sb.append('_');
        sb.append(Character.toLowerCase(c));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private static String packageName(String qualifiedClassName) {
    int lastDot = -1;
    for (int i = 0; i < qualifiedClassName.length(); ++i) {
      char c = qualifiedClassName.charAt(i);
      // class could be nested in another type
      if (Character.isUpperCase(c)) {
        return qualifiedClassName.substring(0, i - 1);
      }
      if (c == '.') {
        lastDot = i;
      }
    }
    // otherwise, package name is substring to last dot.
    if (lastDot > 0) {
      return qualifiedClassName.substring(0, lastDot);
    }
    // could also be in default package
    return null;
  }

}
