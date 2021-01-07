# Introduction
This sample project uses the AWS Cloud Development kit (CDK) and Spring Boot to build an AppSync front end to DynamoDB.

# Objectives
1. Use Java constructs to build and deploy an AWS Cloud Formation stack that implements an AppSync front end to DynamoDB.
2. Use the Velocity Template Language to implement GraphQL Query and Mutation resolvers that integrate directly with DynamoDB
3. Utilize a single-table design with DynamoDB. The example uses invoices, lineitems, products, customers, and addresses all contained in a single table.
4. Build a simple browser client using Spring Boot and Thymeleaf. Use the browser window to post GraphQL queries.

# Quick Start
`cdk deploy`  (Uses mvn to compile the Java files, generates the JSON template, and builds the CF stack in AWS.)

The "cdk deploy" command produces two outputs: ApiKey and GraphQL. These outputs are also available in the AWS console Cloud Formation stack output tab. 

Edit "src/main/resources/application.properties" with the following changes:
- api.url (use the value for GraphQL abover)
- aws.key (use the value for ApiKey above)

`mvn spring-boot:run`

Open your browser and enter the url: http://localhost:8080/query. Use the file examples/example.txt to copy and paste each query and mutation into to browser window.
Click submit to post the query to Spring Boot, which will convert it into a proper GraphQL query and send it to AppSync.

# Cleaning up
When you are finished, don't forget to cleanup to avoid unwanted charges. From the project root directory, issue:

`cdk destroy`
