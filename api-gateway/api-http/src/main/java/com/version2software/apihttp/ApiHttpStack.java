package com.version2software.apihttp;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.HttpRoute;
import software.amazon.awscdk.services.apigatewayv2.HttpRouteKey;
import software.amazon.awscdk.services.apigatewayv2.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.HttpProxyIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.LambdaProxyIntegration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.SingletonFunction;

import java.util.UUID;

public class ApiHttpStack extends Stack {
    public ApiHttpStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ApiHttpStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        SingletonFunction lambdaFunction =
                SingletonFunction.Builder.create(this, "api-http-example")
                        .description("This function echoes the event received in the request")
                        .code(Code.fromAsset("./assets"))
                        .handler("index.main")
                        .timeout(Duration.seconds(300))
                        .runtime(Runtime.PYTHON_3_8)
                        .uuid(UUID.randomUUID().toString())
                        .build();

        HttpApi api = HttpApi.Builder.create(this, "MyApi")
                .apiName("api-http-example")
                .build();

        // TODO don't use v2 url, use something else?
        HttpRoute.Builder.create(this, "HttpRoute")
                .httpApi(api)
                .routeKey(HttpRouteKey.with("/http"))
                .integration(HttpProxyIntegration.Builder.create()
                        .method(HttpMethod.ANY)
                        .url("http://version2software.com/end.html")
                        .build())
                .build();

        HttpRoute.Builder.create(this, "LambdaRoute")
                .httpApi(api)
                .routeKey(HttpRouteKey.with("/lambda"))
                .integration(LambdaProxyIntegration.Builder.create()
                        .handler(lambdaFunction)
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_1_0)
                        .build())
                .build();

        CfnOutput.Builder.create(this, "ApiUrl")
                .value(api.getUrl())
                .build();
    }
}
