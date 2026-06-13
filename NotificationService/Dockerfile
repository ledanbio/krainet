# Этап сборки
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем родительский POM и устанавливаем его
COPY pom.xml .
RUN mvn install -N -DskipTests

# Копируем Common модуль и собираем его
COPY Common Common/
RUN cd Common && mvn clean install -DskipTests

# Копируем NotificationService и собираем его
COPY NotificationService NotificationService/
RUN cd NotificationService && mvn clean package -DskipTests

# Этап выполнения
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Создаем непривилегированного пользователя
RUN addgroup -S spring && adduser -S spring -G spring

# Копируем JAR файл
COPY --from=build /app/NotificationService/target/*.jar app.jar

# Переключаемся на непривилегированного пользователя
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]