FROM amazoncorretto:21-alpine-jdk

ARG APP_NAME=ioto-cmp-service-sms
ARG APP_VERSION=0.0.1

LABEL org.opencontainers.image.title="${APP_NAME}"
LABEL org.opencontainers.image.version="${APP_VERSION}"
LABEL org.opencontainers.image.description="IoTo CMP SMS service"
LABEL org.opencontainers.image.vendor="ioto.cmp"

COPY build/libs/${APP_NAME}-${APP_VERSION}.jar /service/ioto-cmp-service-sms.jar
COPY src/main/resources/application-docker.yaml /service/application.yaml

WORKDIR /service
ENTRYPOINT ["java","-Dspring.config.location=/service/application.yaml", "-jar", "/service/ioto-cmp-service-sms.jar"]

EXPOSE 9006 9007



