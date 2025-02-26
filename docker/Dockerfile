FROM azul/zulu-openjdk-alpine:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} dart.jar
ENTRYPOINT ["java", "-jar", "/dart.jar"]