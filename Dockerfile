# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

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
# Render provides PGHOST, PGPORT, PGDATABASE, PGUSER, PGPASSWORD
# We convert these to Spring Boot format
ENTRYPOINT ["sh", "-c", "\
  if [ -n \"$PGHOST\" ]; then \
    export SPRING_DATASOURCE_URL=\"jdbc:postgresql://${PGHOST}:${PGPORT:-5432}/${PGDATABASE}\"; \
    export SPRING_DATASOURCE_USERNAME=\"$PGUSER\"; \
    export SPRING_DATASOURCE_PASSWORD=\"$PGPASSWORD\"; \
  fi; \
  java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar \
"]
