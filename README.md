# cdk-examples
The projects below are Java examples that use the AWS CDK (Cloud Development Kit). All examples are based on JDK 11. The CDK will build the Cloud Formation infrastructure using Java constructs.
Some of the examples use curl to invoke the API. Other examples use Spring Boot and Thymeleaf to build a simple browser UI.
 
 Developed and tested with:
 - macOS Catalina Version 10.15.7
 - cdk 1.78.0 (build 2c74f4c)
 - openjdk version "11.0.8" 2020-07-14

# Assumptions
1. You have an AWS account.
2. You have generated an access key and secret key for an IAM account. Using root account access keys is strongly discouraged.
3. You have configured a default profile in ~/.aws/config and ~/.aws/credentials, either manually or by using "aws configure".

# Installation
Install the aws-cli. See https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html \
Install the CDK. See https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html#getting_started_prerequisites

# Requirements for exercises
JDK (11 or later)\
mvn (3.6 or later)\
curl (As an alternative you can use Postman. See https://www.postman.com/downloads)

# Getting Started
After cloning this project, cd into each project directory listed below, and follow the README.md directions for that project.

# Api Gateway examples
| Example | Description |
|---------|-------------|
| [api-ddb](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-ddb/) | Uses the API Gateway as a frontend to Dynamo DB directly (i.e., without Lambda in the middle). Uses an Api-Key to protect the Api. Exercises API with curl and Dynamo DB request as the post body. |
| [api-http](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-http/) | Builds an HTTP based API using the API Gateway V2. Uses both HTTP and Lambda integrations. Does not use any authentication. |
| [api-lambda](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-lambda/) | Uses the API Gateway as a frontend to Lambda, using proxy and nonproxy integrations. Does not use any authentication. Uses Spring Boot and Thymeleaf to build a simple Browser UI.  |
| [api-polly](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-polly/) | Builds a text-to-speech application using the API Gateway as a frontend to AWS Polly. Does not use any authentication. Uses Spring Boot and Thymeleaf to build a simple Browser UI.  |
| [api-s3](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-s3/) | Uses the API Gateway as a frontend to S3. Uses IAM authentication. Uses Spring Boot and Thymeleaf to build a simple Browser UI. |

# AppSync examples

Build serverless GraphQL APIs.

| Example | Description |
|---------|-------------|
| [appsync-trivial](https://github.com/Version2Software/cdk-examples/tree/main/appsync/appsync-trivial/) | Builds a simple GraphQL API using the NoneDataSource. Uses an Api-Key. Invokes the API using curl.  | 
| [appsync-lambda](https://github.com/Version2Software/cdk-examples/tree/main/appsync/appsync-lambda/) | Builds a GraphQL API using the LambdaDataSource. Uses an Api-Key. Invokes the API using curl.  | 


# Cleaning up
To avoid paying for the AWS resources created in these exercises, be sure to destroy the Cloud Formation stack.
From the root directory of each project you created, issue the following command to destroy the stack:

`cdk destroy`

# Other useful commands
`cdk synth` (Generate the Cloud Formation template file as YAML, without deploying)\
`cdk synth -j` (Generate the Cloud Formation template file as JSON, without deploying)\
`cdk diff`\
`cdk ls` (List all stacks in the application)\
`cdk docs`\
`cdk init app --language java` (Start a new project. Use this command in an empty directory. The other valid language arguments are: typescript, javascript, python, and csharp.)\
`aws cloudformation describe-stacks --stack-name api-ddb-example`

