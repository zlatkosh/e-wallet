FROM gradle:7.4.1 AS gradle
USER root
WORKDIR /tmp
COPY . /tmp/
RUN chmod -R 777 /tmp && gradle clean bootJar

FROM openjdk:17-jdk-alpine
RUN addgroup -S eweksd && adduser -S eweksd -G eweksd
USER eweksd
WORKDIR /app
COPY --from=gradle /tmp/server/build/libs/e-wallet-boot*.jar e-wallet.jar

ENTRYPOINT ["java","-jar","e-wallet.jar"]
