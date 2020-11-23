# Introduction
This sample project uses the AWS Cloud Development kit (CDK) and Spring Boot to build an API Gateway as a front end to Lambda.
It uses both proxy and nonproxy integrations. There is one Lambda function, which will echo back the event request that it receives.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack. 
2. Build an API Gateway as a front end to Lambda, using proxy integration.
3. Build an API Gateway as a front end to Lambda, using nonproxy integration.
4. Build a simple browser client using Spring Boot and Thymeleaf.

# Quick Start
Bootstrap the S3 asset deployment staging area, if you have not already done so in another project. You only need to do this once across all projects:\
`cdk bootstrap "aws://<YOUR_ACCOUNT_NUMBER>/<YOUR_REGION>"`

`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces an output similar to MyApiEndpoint123456. This output is also available in the AWS console CF stack output tab. 

Edit "src/main/resources/application.properties" with the following changes:
- api.url (use the value for MyApiEndpoint123456)

`mvn spring-boot:run`

Open your browser and enter the url: localhost:8080. You will see a variety of scenarios that exercise the proxy and nonproxy integrations.
These scenarios will demonstrate the various requests and responses of the different techniques. Notice that in the case of proxy integration,
the API Gateway sends a large event object to Lambda. In the case of nonproxy integration, the API Gateway only sends an event that contains
what you specify.

Note: this example project does not use any authorizaion for the API. It is available to the public. However, the API only exercises one Lambda function that simply echoes back the event that it receives from the gateway.

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:\

`cdk destroy`
