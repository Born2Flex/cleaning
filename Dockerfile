FROM amazoncorretto:21

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cleaning_app
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=password

COPY target/*.jar /app/cleaning.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "cleaning.jar"]
