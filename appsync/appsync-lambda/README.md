# Introduction
In this exercise, we use the CDK to create a GraphQL API using the LambdaDataSource.
The Lambda functions define a hard-coded set of invoices and line items to emulate a database.
Each LambdaDataSource sends a fully populated request to Lambda, without the need of request or response templates.

# Objectives
1. Use high-level Java constructs to build and deploy an AWS Cloud Formation stack, which defines an AppSync GraphQL API.
2. Build two LambdaDataSources; one for invoices, and one for line items.
3. Define python Lambda functions that correspond to each datasource.
3. Use a curl scripts to invoke the API. These scripts use an API Key for security.

# Bootstrap
If you have not already done so in another project, bootstrap the S3 asset deployment staging area for your Lambda functions.
You only need to do this once across all projects:\
`cdk bootstrap "aws://<YOUR_ACCOUNT_NUMBER>/<YOUR_REGION>"`

# Quick Start
`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces two outputs, you will need below: ApiKey and GraphQlUrl. These outputs are also available in the AWS console CF stack output tab.
Use these values to make the appropriate substitutions in the following export statements.

`export GRAPHQL_URL=<GraphQlUrl>`\
`export API_KEY=<ApiKey>`

To exercise the API, change into the examples directory, and run each script.

`cd examples`

`./get_all_invoices.sh`\
`./get_one_invoice.sh`

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:\

`cdk destroy`
