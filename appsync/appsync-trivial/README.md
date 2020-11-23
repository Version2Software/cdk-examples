# Introduction
In this exercise, we will use the CDK to create a trivial GraphQL API using the NoneDataSource.
The behavior of the NoneDataSource resolver is analogous to the API Gateway mock request and response:
the payload of the request will be returned in the response. 

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack.
2. Build a simple GraphQL API using a NoneDataSource.
3. Use a curl script to invoke the API.

# Quick Start
`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces two outputs, you will need below: ApiKey and GraphQlUrl. These outputs are also available in the AWS console CF stack output tab.
Use these values to make the appropriate substitutions in the following export statements.

`export GRAPHQL_URL=<GraphQlUrl>`\
`export API_KEY=<ApiKey>`

To exercise the API, change into the examples directory, and run the script. The "getanswer.sh" script demonstrates a minimal GraphQL query.

`cd examples`

`./getanswer.sh`

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:\

`cdk destroy`
