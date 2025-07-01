FROM maven:3.9.10-eclipse-temurin-24 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM eclipse-temurin:24
WORKDIR /opt
EXPOSE 8080
COPY --from=build /home/app/target/*.jar /opt/app.jar
CMD java -jar /opt/app.jar