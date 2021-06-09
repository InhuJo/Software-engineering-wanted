FROM openjdk:11-jre-slim

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=payroll-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} MRS-springboot.jar

ENTRYPOINT ["java","-jar","/MRS-springboot.jar"]