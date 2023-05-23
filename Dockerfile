#FROM openjdk:19-jdk-alpine
#COPY ./target/Token-0.0.1-SNAPSHOT.jar Token-0.0.1-SNAPSHOT.jar
#CMD ["java","-jar","Token-0.0.1-SNAPSHOT.jar"]


# syntax=docker/dockerfile:experimental
# Java image
FROM openjdk:19-jdk-alpine
# Khi container bat len thi tu dong nhay vao thu muc nay
# working directory
WORKDIR /app

EXPOSE 8888
#Copy from Host(PC , Laptop) to container
COPY ./target/Token-0.0.1-SNAPSHOT.jar .

#Run inside container
CMD ["java","-jar", "Token-0.0.1-SNAPSHOT.jar"]