package com.version2software.appsynclambda;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.appsync.AuthorizationConfig;
import software.amazon.awscdk.services.appsync.AuthorizationMode;
import software.amazon.awscdk.services.appsync.AuthorizationType;
import software.amazon.awscdk.services.appsync.BaseResolverProps;
import software.amazon.awscdk.services.appsync.GraphqlApi;
import software.amazon.awscdk.services.appsync.Schema;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.SingletonFunction;

import java.util.UUID;

public class AppSyncLambdaStack extends Stack {
    public AppSyncLambdaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AppSyncLambdaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        GraphqlApi api = GraphqlApi.Builder.create(this, "MyApi")
                .name("appsync-lambda-example")
                .schema(Schema.fromAsset("schema/schema.graphql"))
                .authorizationConfig(AuthorizationConfig.builder()
                        .defaultAuthorization(AuthorizationMode.builder()
                                .authorizationType(AuthorizationType.API_KEY)
                                .build())
                        .build())
                .build();

        SingletonFunction invoicesFunction =
                SingletonFunction.Builder.create(this, "appsync-lambda-invoices")
                        .description("Returns a list of invoices with line items")
                        .code(Code.fromAsset("lambda"))
                        .handler("invoices.main")
                        .timeout(Duration.seconds(300))
                        .runtime(Runtime.PYTHON_3_8)
                        .uuid(UUID.randomUUID().toString())
                        .build();

        SingletonFunction lineItemsFunction =
                SingletonFunction.Builder.create(this, "appsync-lambda-lineitems")
                        .description("Returns a list of line items")
                        .code(Code.fromAsset("lambda"))
                        .handler("lineitems.main")
                        .timeout(Duration.seconds(300))
                        .runtime(Runtime.PYTHON_3_8)
                        .uuid(UUID.randomUUID().toString())
                        .build();

        api.addLambdaDataSource("MyInvoicesSource", invoicesFunction)
                .createResolver(BaseResolverProps.builder()
                .typeName("Query")
                .fieldName("invoices")
                .build());

        api.addLambdaDataSource("MyLineItemsSource", lineItemsFunction)
                .createResolver(BaseResolverProps.builder()
                .typeName("Query")
                .fieldName("lineItems")
                .build());

        CfnOutput.Builder.create(this, "GraphQlUrl")
                .value(api.getGraphqlUrl())
                .build();

        CfnOutput.Builder.create(this, "ApiKey")
                .value(api.getApiKey())
                .build();
    }
}
