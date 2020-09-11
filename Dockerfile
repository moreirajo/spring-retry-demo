# Download dependencies and build the Jar
FROM maven:3.6.3-jdk-11-slim AS BUILDER
COPY pom.xml /usr/src/demo/retry-demo-pom.xml
# Download the dependencies to speed up future builds
RUN mvn -e -B dependency:resolve dependency:resolve-plugins -f /usr/src/demo/retry-demo-pom.xml
COPY src /usr/src/demo/src
# Build the Jar
RUN mvn -f /usr/src/demo/retry-demo-pom.xml clean install

# Build container for running the microservice
FROM adoptopenjdk/openjdk11:alpine
RUN mkdir /opt/demo
# Copy the Jar built in the BUILDER container.
COPY --from=BUILDER /usr/src/demo/target/retry-demo*.jar /opt/demo/retry-demo.jar
# Set env variables to allow debuging
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n -Djdk.attach.allowAttachSelf=true
ENTRYPOINT ["java", "-jar", "/opt/demo/retry-demo.jar"]