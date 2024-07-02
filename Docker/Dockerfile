FROM amazoncorretto:21
LABEL authors="ababcdcd"
WORKDIR /app
COPY ../.. /app
EXPOSE 8087
ARG JAR_FILE=llm-rating-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]