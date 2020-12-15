package com.version2software.lambda.cdk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.DockerImageCode;
import software.amazon.awscdk.services.lambda.DockerImageFunction;

public class LambdaDockerJavaStack extends Stack {
    public LambdaDockerJavaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public LambdaDockerJavaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        DockerImageFunction function = DockerImageFunction.Builder.create(this, "lambda-docker-java-example")
                .code(DockerImageCode.fromImageAsset("../app"))  //Specifies the directory containing Dockerfile
                .timeout(Duration.seconds(30))
                .build();

        RestApi api = RestApi.Builder.create(this, "my-rest-api")
                .restApiName("lambda-docker-java-example")
                .build();

        LambdaIntegration proxyIntegration = LambdaIntegration.Builder.create(function)
                .proxy(true)
                .build();

        api.getRoot().addMethod("ANY", proxyIntegration);
    }
}
