FROM public.ecr.aws/lambda/java:11
COPY target/* /var/task/
CMD [ "com.version2software.lambda.app.Handler::handleRequest" ]
