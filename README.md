# cdk-examples
The projects below are Java examples that use the AWS CDK (Cloud Development Kit). All examples are based on JDK 11. The CDK will build the Cloud Formation infrastructure using Java constructs.
The first example uses curl to invoke the API Gateway. The second example uses Spring Boot and Thymeleaf to build a local web server and browser UI that exercises the API Gateway.
 
 Developed and tested with:
 - macOS Catalina Version 10.15.7
 - openjdk version "11.0.8" 2020-07-14

## Api Gateway examples
| Example | Description |
|---------|-------------|
| [api-ddb](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-ddb/) | Uses the Api Gateway as a frontend to Dynamo DB directly (i.e., without Lambda in the middle). Uses an Api-Key to protect the Api. Exercises API with curl and Dynamo DB request as the post body. |
| [api-lambda](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-lambda/) | Uses the Api Gateway as a frontend to Lambda, using proxy and nonproxy integrations. Does not use any authentication. Uses Spring Boot and Thymeleaf to build a simple Browser UI.  |
| [api-s3](https://github.com/Version2Software/cdk-examples/tree/main/api-gateway/api-s3/) | Uses the Api Gateway as a frontend to S3. Uses IAM authentication. Uses Spring Boot and Thymeleaf to build a simple Browser UI.  |

## App Sync examples
| Example | Description |
|---------|-------------|
| Coming soon. | Stay tuned. 
