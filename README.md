# cdk-examples
The projects below are Java examples that use the AWS CDK (Cloud Development Kit). All examples are based on JDK 11. The CDK will build the Cloud Formation infrastructure using Java constructs.
Some of the examples use curl to invoke the API. Other examples use Spring Boot and Thymeleaf to build a simple browser UI.
 
 Developed and tested with:
 - macOS Catalina Version 10.15.7
 - openjdk version "11.0.8" 2020-07-14

## Api Gateway examples
| Example | Description |
|---------|-------------|
| [api-ddb](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-ddb/) | Uses the API Gateway as a frontend to Dynamo DB directly (i.e., without Lambda in the middle). Uses an Api-Key to protect the Api. Exercises API with curl and Dynamo DB request as the post body. |
| [api-http](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-http/) | Builds an HTTP based API using the API Gateway V2. Uses both HTTP and Lambda integrations. Does not use any authentication. |
| [api-lambda](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-lambda/) | Uses the API Gateway as a frontend to Lambda, using proxy and nonproxy integrations. Does not use any authentication. Uses Spring Boot and Thymeleaf to build a simple Browser UI.  |
| [api-s3](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-s3/) | Uses the API Gateway as a frontend to S3. Uses IAM authentication. Uses Spring Boot and Thymeleaf to build a simple Browser UI.  |

## App Sync examples
| Example | Description |
|---------|-------------|
| Coming soon. | Stay tuned. 
