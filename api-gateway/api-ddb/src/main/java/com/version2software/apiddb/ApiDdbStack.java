package com.version2software.apiddb;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Fn;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.ApiKey;
import software.amazon.awscdk.services.apigateway.AuthorizationType;
import software.amazon.awscdk.services.apigateway.Integration;
import software.amazon.awscdk.services.apigateway.IntegrationOptions;
import software.amazon.awscdk.services.apigateway.IntegrationResponse;
import software.amazon.awscdk.services.apigateway.IntegrationType;
import software.amazon.awscdk.services.apigateway.Method;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.MethodResponse;
import software.amazon.awscdk.services.apigateway.PassthroughBehavior;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.apigateway.UsagePlan;
import software.amazon.awscdk.services.apigateway.UsagePlanPerApiStage;
import software.amazon.awscdk.services.apigateway.UsagePlanProps;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;

import java.util.Arrays;
import java.util.HashMap;

public class ApiDdbStack extends Stack {
    public ApiDdbStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ApiDdbStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        RestApi restApi = buildApi();

        Role dynRole = buildRole();

        Table table = buildTable(restApi, dynRole);
        table.grantReadWriteData(dynRole);

        buildGet(restApi); // Mock for sanity test

        buildPost(restApi, dynRole, "Query");
        buildPost(restApi, dynRole, "Scan");
        buildPost(restApi, dynRole, "PutItem");
        buildPost(restApi, dynRole, "GetItem");
        buildPost(restApi, dynRole, "UpdateItem");
        buildPost(restApi, dynRole, "BatchWriteItem");
        buildPost(restApi, dynRole, "DeleteItem");

        ApiKey apiKey = buildApiKey(restApi);
        buildUsagePlan(restApi, apiKey);

        CfnOutput.Builder.create(this, "ApiKeyId")
                .value(apiKey.getKeyId())
                .build();
    }

    private RestApi buildApi() {
        return RestApi.Builder.create(this, "MyApi")
                .restApiName("api-ddb-example")
                .build();
    }

    private Role buildRole() {
        return Role.Builder.create(this, "dynRole")
                .assumedBy(ServicePrincipal.Builder.create("apigateway.amazonaws.com")
                        .build())
                .build();
    }

    private Table buildTable(RestApi restApi, Role dynRole) {
        return Table.Builder.create(this, "MyTable")
                .tableName("api-ddb-example")
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .removalPolicy(RemovalPolicy.DESTROY)  // Allow deletion of table when using "cdk destroy"
                .partitionKey(Attribute.builder()
                        .name("symbol")
                        .type(AttributeType.STRING)
                        .build())
                .build();
    }

    private Method buildPost(RestApi restApi, Role dynRole, String resource) {
        Resource actionResource = restApi.getRoot().addResource(resource);

        return Method.Builder.create(restApi, resource +"Method")
                .httpMethod("POST")
                .resource(actionResource)
                .integration(Integration.Builder.create()
                        .type(IntegrationType.AWS)
                        .integrationHttpMethod("POST")
                        .uri(Fn.sub("arn:aws:apigateway:${AWS::Region}:dynamodb:action"+actionResource.getPath()))
                        .options(IntegrationOptions.builder()
                                .credentialsRole(dynRole)
                                .integrationResponses(Arrays.asList(
                                        IntegrationResponse.builder().statusCode("200").build(),
                                        IntegrationResponse.builder().statusCode("400").selectionPattern("4\\d{2}").build(),
                                        IntegrationResponse.builder().statusCode("500").selectionPattern("5\\d{2}").build()))
                                .build())
                        .build())
                .options(MethodOptions.builder()
                        .authorizationType(AuthorizationType.NONE)
                        .apiKeyRequired(true)
                        .methodResponses(Arrays.asList(
                                MethodResponse.builder().statusCode("200").build(),
                                MethodResponse.builder().statusCode("400").build(),
                                MethodResponse.builder().statusCode("500").build()))
                        .build())
                .build();
    }

    private void buildGet(RestApi restApi) {
        Method.Builder.create(restApi, "GetMock")
                .httpMethod("GET")
                .resource(restApi.getRoot())
                .integration(Integration.Builder.create()
                        .type(IntegrationType.MOCK)
                        .options(IntegrationOptions.builder()
                                .passthroughBehavior(PassthroughBehavior.WHEN_NO_TEMPLATES)
                                .requestTemplates(new HashMap<String,String>() {
                                    {
                                        put("application/json", "{\"statusCode\": 200}");
                                    }
                                })
                                .integrationResponses(Arrays.asList(IntegrationResponse.builder()
                                        .responseTemplates(new HashMap<String,String>() {
                                            {
                                                put("application/json", "{\"message\": \"OK\"}");
                                            }
                                        })
                                        .statusCode("200")
                                        .build()))
                                .build())
                        .build())
                .options(MethodOptions.builder()
                        .authorizationType(AuthorizationType.NONE)
                        .apiKeyRequired(true)
                        .methodResponses(Arrays.asList(MethodResponse.builder()
                                .statusCode("200")
                                .build()))
                        .build())
                .build();
    }

    private ApiKey buildApiKey(RestApi restApi) {
        return ApiKey.Builder.create(this, "MyApiKey")
                .enabled(true)
                .generateDistinctId(false)
                .build();
    }

    private UsagePlan buildUsagePlan(RestApi restApi, ApiKey apiKey) {
        return restApi.addUsagePlan("MyApiDdbUsagePlan", UsagePlanProps.builder()
                .apiKey(apiKey)
                .apiStages(Arrays.asList(UsagePlanPerApiStage.builder()
                        .api(restApi)
                        .stage(restApi.getDeploymentStage())
                        .build()))
                .build());
    }
}
