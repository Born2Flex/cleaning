FROM amazoncorretto:21

ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/cleaning_app
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=password

COPY target/*.jar /app/cleaning.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "cleaning.jar"]
