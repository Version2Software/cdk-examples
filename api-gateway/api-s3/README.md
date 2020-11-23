# Introduction
This sample project uses the AWS Cloud Development kit (CDK) and Spring Boot to build an API Gateway as a front end to S3.
It is loosely based on the AWS tutorial "Create a REST API as an Amazon S3 proxy in API Gateway" found at https://docs.aws.amazon.com/apigateway/latest/developerguide/integrating-api-with-aws-services-s3.html.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack. 
This stack will build an API Gateway as a front end to S3, providing functionality to: list buckets, create a bucket, delete a bucket, list objects, upload an object, download an object, and delete an object.
2. Build a simple S3 browser client using Spring Boot and Thymeleaf.
3. Learn how to sign an AWS_IAM auth request using the AWSRequestSigningApacheInterceptor from https://github.com/awslabs/aws-request-signing-apache-interceptor.

# Quick Start
`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces an output similar to MyApiEndpoint123456. This output is also available in the AWS console CF stack output tab. 

Edit "src/main/resources/application.properties" with the following changes:
- api.url (use the value for MyApiEndpoint123456)
- aws.region (e.g., us-east-1)
- file.download.dir (Optional, you can leave this set to your working directory)

`mvn spring-boot:run`

Open your browser and enter the url: localhost:8080. You will see seven use cases:
- List Buckets
- List Objects
- Create Bucket
- Delete Bucket
- Upload Object
- Download Object
- Delete Object

Exercise each use case, to see what the API expects and what it returns. Each request sent from Spring Boot 
to the API Gateway will be signed using the AWSRequestSigningApacheInterceptor.
In this demo, the bucket and object must exist in the same region as the Cloud Formation stack.

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:\

`cdk destroy`
