# Introduction
This sample text-to-speech project uses the AWS Cloud Development kit (CDK) and Spring Boot to build an API Gateway front end to AWS Polly.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack. 
2. Build an API Gateway as a front end to Polly.
3. Build a simple browser client using Spring Boot and Thymeleaf to invoke the API.

# Quick Start
`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces an output similar to MyApiEndpoint123456. This output is also available in the AWS console CF stack output tab. 

Edit "src/main/resources/application.properties". Use MyApiEndpoint123456 to set the following:
- api.url=<MY_API_ENDPOINT>

`mvn spring-boot:run`

Open your browser and enter the url: localhost:8080. Select a voice, enter your text, and click Speak. The API Gateway root resource provides two methods.
The GET method retrieves the available English voices. The POST method sends your text and returns an mp3 byte stream.

Note: this example project does not use any authorizaion for the API. It is available to the public. 

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:\

`cdk destroy`
