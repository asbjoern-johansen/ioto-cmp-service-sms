FROM amazoncorretto:21-alpine-jdk

ARG APP_NAME=SMPPSim
ARG APP_VERSION=2.6.11

LABEL org.opencontainers.image.title="${APP_NAME}"
LABEL org.opencontainers.image.version="${APP_VERSION}"
LABEL org.opencontainers.image.description="Selenium SMSC"
LABEL org.opencontainers.image.vendor="com.seleniumsoftware"

COPY smsc/smppsim.jar /service/smppsim.jar
COPY smsc/conf/ /service/conf/
COPY smsc/log/ /service/log/
COPY smsc/www/ /service/www/

WORKDIR /service
ENTRYPOINT ["java","-Djava.net.preferIPv4Stack=true", "-Djava.util.logging.config.file=/service/conf/logging.properties", "-jar", "/service/smppsim.jar", "/service/conf/smppsim.props"]

EXPOSE 2775 2776





