FROM openjdk:8-alpine

COPY target/uberjar/prime-factorization.jar /prime-factorization/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/prime-factorization/app.jar"]
