# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (including provided scope like Lombok)
COPY pom.xml .
RUN mvn dependency:resolve -B && \
    mvn dependency:resolve-plugins -B && \
    mvn dependency:copy-dependencies -DincludeArtifactIds=lombok -DoutputDirectory=/tmp/lombok

# Copy source code and build with explicit Lombok configuration
COPY src ./src
RUN mvn clean compile -DskipTests && \
    mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create uploads directory
RUN mkdir -p /tmp/uploads

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run application
# Priority: Use SPRING_DATASOURCE_URL if set, otherwise build from PG* variables
ENTRYPOINT ["sh", "-c", "\
  echo '==> Checking database environment variables...'; \
  if [ -n \"$SPRING_DATASOURCE_URL\" ]; then \
    echo '==> Using pre-configured SPRING_DATASOURCE_URL (PRIORITY)'; \
    echo 'URL (password hidden):'$(echo $SPRING_DATASOURCE_URL | sed 's/password=[^&]*/password=***/'); \
  elif [ -n \"$PGHOST\" ]; then \
    export SPRING_DATASOURCE_URL=\"jdbc:postgresql://${PGHOST}:${PGPORT:-5432}/${PGDATABASE}?sslmode=require\"; \
    export SPRING_DATASOURCE_USERNAME=\"$PGUSER\"; \
    export SPRING_DATASOURCE_PASSWORD=\"$PGPASSWORD\"; \
    echo '==> Built JDBC URL from PG variables (FALLBACK)'; \
    echo 'URL:'$(echo $SPRING_DATASOURCE_URL | sed 's/:[^:]*@/:***@/'); \
  else \
    echo '==> ERROR: No database configuration found!'; \
  fi; \
  java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar \
"]
