# Introduction
This sample project uses the AWS Cloud Development kit (CDK) and Spring Boot to build an API Gateway as a front end to S3.
It is loosely based on the AWS tutorial "Create a REST API as an Amazon S3 proxy in API Gateway" found at https://docs.aws.amazon.com/apigateway/latest/developerguide/integrating-api-with-aws-services-s3.html.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack. 
This stack will build an API Gateway as a front end to S3, providing functionality to: list buckets, create a bucket, delete a bucket, list objects, upload an object, download an object, and delete an object.
2. Build a simple S3 browser client using Spring Boot and Thymeleaf.
3. Learn how to sign an AWS_IAM auth request using the AWSRequestSigningApacheInterceptor from https://github.com/awslabs/aws-request-signing-apache-interceptor.

# Assumptions
1. You have an AWS account.
2. You have generated an access key and secret key for an IAM account. Using root account access keys is strongly discouraged.
3. You have created an EC2 key pair.
3. You have used "aws configure" to configure a default profile (stored in ~/.aws/config and ~/.aws/credentials).

# Installation
Install the aws-cli. See https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html \
Install the CDK. See https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html#getting_started_prerequisites

# Requirements for this exercise
JDK (11 or later)\
mvn (3.6 or later)\
git

# Quick Start
`git clone https://github.com/Version2Software/cdk-example-apis3.git`

`cd cdk-example-apis3`

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
To avoid paying for the AWS resources created in this exercise, be sure to destroy the Cloud Formation stack.

`cdk destroy`

# Other useful commands
`cdk synth` (Generate the Cloud Formation template file as YAML, without deploying)\
`cdk synth -j` (Generate the Cloud Formation template file as JSON, without deploying)\
`cdk diff`\
`cdk ls` (List all stacks in the application)\
`cdk docs`\
`cdk init app --language java` (Start a new project. Use this command in an empty directory. The other valid language arguments are: typescript, javascript, python, and csharp.)\
`aws cloudformation describe-stacks --stack-name ApiS3`
