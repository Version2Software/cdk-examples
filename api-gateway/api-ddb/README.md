# Introduction
Creating AWS Infrastructure using a high-level language is a powerful concept. The AWS Cloud Development Kit (CDK) makes this possible.
Manually editing Cloud Formation (CF) JSON templates is painful and not necessary.
In this exercise, we will use Java and the CDK to create a CF stack. This, in turn, will build an API Gateway and DynamoDB table.
We will then use curl to invoke the API, which will send JSON requests directly from the API Gateway to DynamoDB.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack. We do not need to see the JSON (a.k.a., assembly language) of the CF template.
2. Build an API Gateway with resources corresponding to DynamoDB's GetItem, PutItem, Scan, etc.
3. Integrate the API Gateway directly with DynamoDB, mitigating the need for a Lambda function.
4. Use curl scripts to invoke the API.
5. Understand a sampling of DynamoDB requests and responses.

# Assumptions
1. You have an AWS account.
2. You have generated an access key and secret key for an IAM account. Using root account access keys is strongly discouraged.
3. You have configured a default profile in ~/.aws/config and ~/.aws/credentials, either manually or by using "aws configure".

# Installation
Install the aws-cli. See https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html \
Install the CDK. See https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html#getting_started_prerequisites

# Requirements for this exercise
JDK (11 or later)\
mvn (3.6 or later)\
git\
curl (As an alternative you can use Postman. See https://www.postman.com/downloads)

# Quick Start
`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces two outputs, you will need below: ApiKeyId and MyApiEndpoint123456. These outputs are also available in the AWS console CF stack output tab. 

To restrict access to our API, we will use the x-api-key header in the curl commands. We need to retrieve the API key value to do this.
(Bear in mind, AWS recommends using more than just an API key to secure a production system.)

`cd examples`

Use the following command, replacing <YOUR_API_KEY_ID> with the output ApiKeyId. Also, replace <YOUR_REGION> with your region:

`aws apigateway get-api-key --api-key <YOUR_API_KEY_ID> --region <YOUR_REGION> --include-value`

From the JSON response, use the API key "value" to replace <YOUR_API_KEY_VALUE>. Also replace <YOUR_API_ENDPOINT> with the CF output for MyApiEndpoint123456.

`export API_KEY=<YOUR_API_KEY_VALUE>`\
`export API_ENDPOINT=<YOUR_API_ENDPOINT>`

We are now ready to being using the API. The JSON files in the examples directory give samples of DynamoDB requests.

# First example
`./getmock.sh` (Sanity test)\
`./putitem.sh`\
`./getitem.sh`\
`./updateitem.sh`\
`./getitem.sh` (Confirms the update)\
`./deleteitem.sh`\
`./scan.sh` (Confirms the deletion)

# Second example
`./batchwriteitem.sh` (Adds three records)\
`./scan.sh`\
`./scanfilter.sh`\
`./query.sh`\
`./querycount.sh`\
`./batchdelete.sh` (Deletes three records)\
`./scan.sh` (Confirms the deletion)

# Cleaning up
To avoid paying for the AWS resources created in this exercise, be sure to destroy the Cloud Formation stack. Return to the root directory and destroy it:

`cd ..`\
`cdk destroy`

# Other useful commands
`cdk synth` (Generate the Cloud Formation template file as YAML, without deploying)\
`cdk synth -j` (Generate the Cloud Formation template file as JSON, without deploying)\
`cdk diff`\
`cdk ls` (List all stacks in the application)\
`cdk docs`\
`cdk init app --language java` (Start a new project. Use this command in an empty directory. The other valid language arguments are: typescript, javascript, python, and csharp.)\
`aws cloudformation describe-stacks --stack-name api-ddb-example`
