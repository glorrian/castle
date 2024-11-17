# Этап 1: Сборка JAR файла
FROM openjdk:21-jdk-slim AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew shadowJar

# Этап 2: Финальный образ
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/castle-0.1-all.jar castle.jar

RUN if [ -d ".local" ]; then cp -r .local .local; fi

CMD ["java", "-jar", "castle.jar"]
