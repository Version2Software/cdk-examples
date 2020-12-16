# Introduction
This tutorial uses the AWS Cloud Development kit (CDK) to deploy a Java-based Docker container to AWS Lambda. It also deploys an API Gateway endpoint to invoke the Lambda function.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack.
2. Build and deploy a docker image that contains a Java class that adhears to the Lambda Runtime Interface. 
2. Use curl to send a request to the API Gateway, which will invoke the AWS Lambda function (i.e., actually a Java method to be more precise).

# Extra Installation Instructions

For this tutorial, your local PC will need to install Docker. See https://docs.docker.com/get-docker/

# Quick Start

This project uses two child Maven modules: app and cdk.

1. Compile the Lambda handler\
`cd app`\
`mvn clean package` (This will use shade to create a fat jar. In the future, if you need thirdparty jars, they will also be included in the fat jar.)

2. Deploy to Lambda. This also installs the container in the Elastic Container Registry.\
`cd ../cdk`\
`cdk deploy`  (Uses mvn to compile the Java Stack files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces an output similar to MyApiEndpoint123456. This output is also available in the AWS console CF stack output tab. 

`export API_ENDPOINT=<YOUR_API_ENDPOINT>`

#Examples

`cd ../examples`

`./testget.sh`\
`./testpost.sh`

#Optional

If you wish to run your lambda container locally for testing:

`cd ../app`\
`docker build -t myjava .`\
`docker run -p 9000:8080 myjava`

In another terminal window, enter:\
`curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{"requestContext":{"accountId":"123"}, "body":"{color:red}"}'`

To shut down, return to the terminal window where you ran `docker run -p 9000:8080 myjava`, and enter control-C.

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:\

`cdk destroy`



