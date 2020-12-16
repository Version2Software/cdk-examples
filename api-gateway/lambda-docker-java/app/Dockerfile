FROM public.ecr.aws/lambda/java:11
RUN mkdir /var/task/lib
COPY target/lambda-docker-java-app-0.1.jar /var/task/lib/lambda-docker-java-app-0.1.jar
CMD [ "com.version2software.lambda.app.Handler::handleRequest" ]
