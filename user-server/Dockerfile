FROM amazoncorretto:21

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cleaning_app
ENV SPRING_DATASOURCE_USERNAME=userserver
ENV SPRING_DATASOURCE_PASSWORD=8(efQ8cx1H]q}

COPY target/*.jar /app/user-cleaning.jar

WORKDIR /app

EXPOSE 1177

CMD ["java", "-jar", "user-cleaning.jar"]
