FROM amazoncorretto:21-alpine

USER root

RUN echo "America/Bogota" > /etc/timezone \
&& apk add --update curl gcompat && rm -rf /var/cache/apk/* \

ENV LD_PRELOAD=/lib/libgcompat.so.0

RUN addgroup -S app && adduser -S -G app app
WORKDIR /home/userapp

COPY target/*.jar app.jar
COPY src/main/resources/config/application.yaml config/application.yaml
RUN mkdir logs && chown -R app:app /home/userapp

USER app

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl --fail http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java","-jar","app.jar"]