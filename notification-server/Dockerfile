FROM amazoncorretto:21

ENV ACTIVEMQ_ADMIN_LOGIN=tcp://localhost:61616
ENV ACTIVEMQ_ADMIN_LOGIN=admin
ENV ACTIVEMQ_ADMIN_PASSWORD=admin

COPY target/*.jar /app/notification.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "notification.jar"]
