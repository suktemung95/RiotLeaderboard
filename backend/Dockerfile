FROM amazoncorretto:25 AS build

WORKDIR /app

RUN dnf install -y tar gzip

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM amazoncorretto:25

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]