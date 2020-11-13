package com.version2software.apis3.cdk;

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
import software.amazon.awscdk.services.apigateway.PassthroughBehavior;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;

import java.util.List;
import java.util.Map;

public class ApiS3Stack extends Stack {
    public ApiS3Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ApiS3Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        RestApi restApi = buildApi();
        Resource folderResource = restApi.getRoot().addResource("{folder}");
        Resource itemResource = folderResource.addResource("{item}");

        Role role = buildRole();

        buildRootMethod(restApi, "Root", role, "GET", restApi.getRoot(), "/");

        buildFolderMethod(restApi, "Folder", role, "GET", folderResource, "{bucket}");
        // When using PUT, LocationConstraint only works for non-default regions
        buildFolderMethod(restApi, "FolderPut", role, "PUT", folderResource, "{bucket}");
        buildFolderMethod(restApi, "FolderDelete", role, "DELETE", folderResource, "{bucket}");

        buildItemMethod(restApi, "Item", role, "GET", itemResource, "{bucket}/{object}");
        buildItemMethod(restApi, "ItemPut", role, "PUT", itemResource, "{bucket}/{object}");
        buildItemMethod(restApi, "ItemDelete", role, "DELETE", itemResource, "{bucket}/{object}");
    }

    private RestApi buildApi() {
        return RestApi.Builder.create(this, "MyApi")
                .restApiName("apis3demo")
                .binaryMediaTypes(List.of("*/*")) // This enables all binary data types to go to s3 correctly
                .build();
    }

    private Role buildRole() {
        return Role.Builder.create(this, "MyRole")
                .assumedBy(ServicePrincipal.Builder.create("apigateway.amazonaws.com")
                        .build())
                .managedPolicies(List.of(ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")))
                .build();
    }

    private Method buildRootMethod(RestApi restApi, String name, Role role, String method, IResource resource, String pathOverride) {
        return Method.Builder.create(restApi, name)
                .httpMethod(method)
                .resource(resource)
                .integration(Integration.Builder.create()
                        .type(IntegrationType.AWS)
                        .integrationHttpMethod(method)
                        .uri(Fn.sub("arn:aws:apigateway:${AWS::Region}:s3:path/"+pathOverride))
                        .options(IntegrationOptions.builder()
                                .credentialsRole(role)
                                .integrationResponses(buildIntegrationResponses())
                                .build())
                        .build())
                .options(MethodOptions.builder()
                        .authorizationType(AuthorizationType.IAM)
                        .methodResponses(buildMethodResponses())
                        .build())
                .build();
    }

    private Method buildFolderMethod(RestApi restApi, String name, Role role, String method, IResource resource, String pathOverride) {
        return Method.Builder.create(restApi, name)
                .httpMethod(method)
                .resource(resource)
                .options(MethodOptions.builder()
                        .authorizationType(AuthorizationType.IAM)
                        .requestParameters(Map.of("method.request.path.folder", true)) // This was the culprit
                        .methodResponses(buildMethodResponses())
                        .build())
                .integration(Integration.Builder.create()
                        .type(IntegrationType.AWS)
                        .integrationHttpMethod(method)
                        .uri(Fn.sub("arn:aws:apigateway:${AWS::Region}:s3:path/"+pathOverride))
                        .options(IntegrationOptions.builder()
                                .credentialsRole(role)
                                .passthroughBehavior(PassthroughBehavior.WHEN_NO_MATCH)
                                .requestParameters(Map.of("integration.request.path.bucket", "method.request.path.folder"))
                                .integrationResponses(buildIntegrationResponses())
                                .build())
                        .build())
                .build();

    }

    private Method buildItemMethod(RestApi restApi, String name, Role role, String method, IResource resource, String pathOverride) {
        return Method.Builder.create(restApi, name)
                .httpMethod(method)
                .resource(resource)
                .options(MethodOptions.builder()
                        .authorizationType(AuthorizationType.IAM)
                        .requestParameters(Map.of(
                                "method.request.path.folder", true,
                                "method.request.path.item", true))
                        .methodResponses(buildMethodResponses())
                        .build())
                .integration(Integration.Builder.create()
                        .type(IntegrationType.AWS)
                        .integrationHttpMethod(method)
                        .uri(Fn.sub("arn:aws:apigateway:${AWS::Region}:s3:path/"+pathOverride))
                        .options(IntegrationOptions.builder()
                                .credentialsRole(role)
                                .passthroughBehavior(PassthroughBehavior.WHEN_NO_MATCH)
                                .requestParameters(Map.of(
                                        "integration.request.path.bucket", "method.request.path.folder",
                                        "integration.request.path.object", "method.request.path.item"))
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
