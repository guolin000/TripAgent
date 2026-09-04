FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests \
    && cp "$(find target -maxdepth 1 -type f -name '*.jar' ! -name 'original-*' | head -n 1)" app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

COPY --from=build /app/app.jar /app/app.jar
RUN mkdir -p /app/tmp

EXPOSE 8123

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
