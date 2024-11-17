FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/castle-0.1-all.jar castle.jar

CMD ["java", "-jar", "castle.jar"]
