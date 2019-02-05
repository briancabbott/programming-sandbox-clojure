FROM openjdk:8-alpine

COPY target/uberjar/adder.jar /adder/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/adder/app.jar"]
