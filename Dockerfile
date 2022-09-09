# 在执行 docker build 之前，请先通过 maven 编译好， mvn clean && mvn package -P flutter
FROM openjdk:8-jdk-alpine
WORKDIR /app

# 拷贝打好的Jar包
COPY ./bootstrap/target/bootstrap-2.0.jar scaffold.jar

ENTRYPOINT ["java","-jar","/app/scaffold.jar", "--spring.profiles.active=flutter"]