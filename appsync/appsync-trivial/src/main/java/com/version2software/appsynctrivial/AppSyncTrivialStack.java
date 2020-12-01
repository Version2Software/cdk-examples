package com.version2software.appsynctrivial;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.appsync.AuthorizationConfig;
import software.amazon.awscdk.services.appsync.AuthorizationMode;
import software.amazon.awscdk.services.appsync.AuthorizationType;
import software.amazon.awscdk.services.appsync.BaseResolverProps;
import software.amazon.awscdk.services.appsync.GraphqlApi;
import software.amazon.awscdk.services.appsync.MappingTemplate;
import software.amazon.awscdk.services.appsync.NoneDataSource;
import software.amazon.awscdk.services.appsync.Schema;

public class AppSyncTrivialStack extends Stack {
    public AppSyncTrivialStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AppSyncTrivialStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        GraphqlApi api = GraphqlApi.Builder.create(this, "MyApi")
                .name("appsync-trivial-example")
                .schema(Schema.fromAsset("assets/schema.graphql"))
                .authorizationConfig(AuthorizationConfig.builder()
                        .defaultAuthorization(AuthorizationMode.builder()
                                .authorizationType(AuthorizationType.API_KEY)
                                .build())
                        .build())
                .build();

        NoneDataSource dataSource = api.addNoneDataSource("TrivalDataSource");

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Query")
                .fieldName("getAnswer")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/trivialRequestMappingTemplate.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/trivialResponseMappingTemplate.vm"))
                .build());

        CfnOutput.Builder.create(this, "GraphQlUrl")
                .value(api.getGraphqlUrl())
                .build();

        CfnOutput.Builder.create(this, "ApiKey")
                .value(api.getApiKey())
                .build();
    }
}
