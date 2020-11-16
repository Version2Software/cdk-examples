# Introduction
This example project uses the AWS Cloud Development kit (CDK) to build an HTTP API using API Gateway V2.
It demonstrates an HTTP integration and a Lambda integration.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack. 
2. Build an HTTP based API Gateway as a front end to an external HTTP site.
3. Build an HTTP based API Gateway as a front end to a Lambda function, which will echo back the event request that it receives.
4. Use curl to invoke the API.

# Assumptions
1. You have an AWS account.
2. You have generated an access key and secret key for an IAM account. Using root account access keys is strongly discouraged.
3. You have used "aws configure" to configure a default profile (stored in ~/.aws/config and ~/.aws/credentials).

# Installation
Install the aws-cli. See https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html \
Install the CDK. See https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html#getting_started_prerequisites

# Requirements for this exercise
JDK (11 or later)\
mvn (3.6 or later)\

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
To avoid paying for the AWS resources created in this exercise, be sure to destroy the Cloud Formation stack.

`cdk destroy`

# Other useful commands
`cdk synth` (Generate the Cloud Formation template file as YAML, without deploying)\
`cdk synth -j` (Generate the Cloud Formation template file as JSON, without deploying)\
`cdk diff`\
`cdk ls` (List all stacks in the application)\
`cdk docs`\
`cdk init app --language java` (Start a new project. Use this command in an empty directory. The other valid language arguments are: typescript, javascript, python, and csharp.)\
`aws cloudformation describe-stacks --stack-name api-http-example`


