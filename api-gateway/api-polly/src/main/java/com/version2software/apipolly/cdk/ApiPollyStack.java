package com.version2software.apipolly.cdk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Fn;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.AuthorizationType;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.apigateway.Integration;
import software.amazon.awscdk.services.apigateway.IntegrationOptions;
import software.amazon.awscdk.services.apigateway.IntegrationResponse;
import software.amazon.awscdk.services.apigateway.IntegrationType;
import software.amazon.awscdk.services.apigateway.Method;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.MethodResponse;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;

import java.util.List;

public class ApiPollyStack extends Stack {
    public ApiPollyStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ApiPollyStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        RestApi api = RestApi.Builder.create(this, "MyApi")
                .restApiName("api-polly-example")
                .binaryMediaTypes(List.of("audio/mpeg"))
                .build();

        Role role = buildPollyRole();
        buildMethod(api, "getVoicesMethod", role, "GET", api.getRoot(), "voices");
        buildMethod(api, "postSpeechMethod", role, "POST", api.getRoot(), "speech");
    }

    private Role buildPollyRole() {
        return Role.Builder.create(this, "MyPollyRole")
                .assumedBy(ServicePrincipal.Builder.create("apigateway.amazonaws.com")
                        .build())
                .managedPolicies(List.of(ManagedPolicy.fromAwsManagedPolicyName("AmazonPollyFullAccess")))
                .build();
    }

    private Method buildMethod(RestApi api, String name, Role role, String method, IResource resource, String action) {
        return Method.Builder.create(api, name)
                .httpMethod(method)
                .resource(resource)
                .options(MethodOptions.builder()
                        .authorizationType(AuthorizationType.NONE)
                        .methodResponses(buildMethodResponses())
                        .build())
                .integration(Integration.Builder.create()
                        .type(IntegrationType.AWS)
                        .integrationHttpMethod(method)
                        .uri(Fn.sub("arn:aws:apigateway:${AWS::Region}:polly:path/v1/" + action))
                        .options(IntegrationOptions.builder()
                                .credentialsRole(role)
                                .integrationResponses(buildIntegrationResponses())
                                .build())
                        .build())
                .build();
    }

    private List buildMethodResponses() {
        return List.of(
                MethodResponse.builder().statusCode("200").build(),
                MethodResponse.builder().statusCode("400").build(),
                MethodResponse.builder().statusCode("500").build());
    }

    private List buildIntegrationResponses() {
        return List.of(
                IntegrationResponse.builder().statusCode("200").build(),
                IntegrationResponse.builder().statusCode("400").selectionPattern("4\\d{2}").build(),
                IntegrationResponse.builder().statusCode("500").selectionPattern("5\\d{2}").build());
    }
}
