FROM maven:3.9.10-eclipse-temurin-21 AS build
COPY pom.xml /home/app/
COPY order-history/pom.xml /home/app/order-history/
COPY order-history/src /home/app/order-history/src
RUN mvn -f /home/app/order-history/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
RUN useradd -r -u 1001 appuser
WORKDIR /opt
EXPOSE 8080
COPY --from=build /home/app/order-history/target/*.jar /opt/app.jar
USER appuser
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
CMD ["java", "-jar", "/opt/app.jar"]
