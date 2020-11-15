package com.version2software.apilambda.cdk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.IntegrationResponse;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.MethodResponse;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.SingletonFunction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApiLambdaStack extends Stack {
    public ApiLambdaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ApiLambdaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        SingletonFunction lambdaFunction =
                SingletonFunction.Builder.create(this, "api-lambda-example")
                        .description("This function echoes back the event received in the request")
                        .code(Code.fromAsset("./lambda_code"))
                        .handler("index.main")
                        .timeout(Duration.seconds(300))
                        .runtime(Runtime.PYTHON_3_8)
                        .uuid(UUID.randomUUID().toString())
                        .build();

        RestApi api = RestApi.Builder.create(this, "MyApi")
                .restApiName("api-lambda-example")
                .build();

        Resource proxyResource = api.getRoot()
                .addResource("proxy")
                .addResource("{status}");

        Resource nonProxyResource = api.getRoot()
                .addResource("nonproxy")
                .addResource("{status}");

        LambdaIntegration proxyIntegration = buildProxyLambdaIntegration(lambdaFunction);
        proxyResource.addMethod("GET", proxyIntegration);

        LambdaIntegration nonproxyIntegration = buildNonproxyLambdaIntegration(lambdaFunction);
        MethodOptions nonproxyMethodOptions = buildNonproxyMethodOptions();
        nonProxyResource.addMethod("GET", nonproxyIntegration, nonproxyMethodOptions);
    }

    private LambdaIntegration buildProxyLambdaIntegration(SingletonFunction lambdaFunction) {
         return LambdaIntegration.Builder.create(lambdaFunction)
                .proxy(true)
                .build();
    }

    private LambdaIntegration buildNonproxyLambdaIntegration(SingletonFunction lambdaFunction) {
        return LambdaIntegration.Builder.create(lambdaFunction)
                .proxy(false)
                .requestTemplates(Map.of(
                        "application/json", "{\"status\":\"${method.request.path.status}\"}"))
                .integrationResponses(List.of(
                        IntegrationResponse.builder()
                                .statusCode("200") // lack of a selection pattern will make this the default
                                .build(),
                        IntegrationResponse.builder()
                                .statusCode("400")
                                .selectionPattern("400")
                                .build(),
                        IntegrationResponse.builder()
                                .statusCode("500")
                                .selectionPattern("500")
                                .build(),
                        IntegrationResponse.builder()
                                .statusCode("502")
                                .selectionPattern("502")
                                .build()))
                .build();
    }

    private MethodOptions buildNonproxyMethodOptions() {
        return MethodOptions.builder()
                .requestParameters(Map.of(
                        "method.request.path.status", true))
                .methodResponses(List.of(
                        MethodResponse.builder().statusCode("200").build(),
                        MethodResponse.builder().statusCode("400").build(),
                        MethodResponse.builder().statusCode("500").build(),
                        MethodResponse.builder().statusCode("502").build()))
                .build();
    }

}
