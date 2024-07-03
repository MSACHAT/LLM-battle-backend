
FROM amazoncorretto:21

# 设置工作目录
WORKDIR /app

# 复制 Maven 构建的 JAR 文件到镜像中
COPY target/llm-rating-*.jar /app/application.jar

# 暴露应用程序的端口
EXPOSE 8087

ENV SPRING_PROFILES_ACTIVE=prod

# 运行应用程序
ENTRYPOINT ["java", "-jar", "application.jar"]