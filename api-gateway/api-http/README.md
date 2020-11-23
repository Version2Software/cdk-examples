# Introduction
This example project uses the AWS Cloud Development kit (CDK) to build an HTTP API using API Gateway V2.
It demonstrates an HTTP integration and a Lambda integration.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack. 
2. Build an HTTP based API Gateway as a front end to an external HTTP site.
3. Build an HTTP based API Gateway as a front end to a Lambda function, which will echo back the event request that it receives.
4. Use curl to invoke the API.

# Quick Start
Bootstrap the S3 asset deployment staging area, if you have not already done so in another project. You only need to do this once across all projects:\
`cdk bootstrap "aws://<YOUR_ACCOUNT_NUMBER>/<YOUR_REGION>"`

`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces an output similar to the following. This output is also available in the AWS console CF stack output tab.\
`api-http-example.ApiUrl = https://<MY_API_ID>.execute-api.<MY_REGION>.amazonaws.com/`\

Using the endpoint, you can now exercise the API Gateway using curl.

The first example is a proxy to an external site:

`curl https://<MY_API_ID>.execute-api.<MY_REGION>.amazonaws.com/http`

The second example is a proxy to Lambda:

`curl https://<MY_API_ID>.execute-api.<MY_REGION>.amazonaws.com/lambda`

Note: this example project does not use any authorization for the API. It is available to the public, but harmless. Be sure to delete it (see following) when you finish the exercise to avoid unwanted traffic.

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:\

`cdk destroy`
