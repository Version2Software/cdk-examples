package com.version2software.apipolly.cdk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Fn;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Resource;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.AuthorizationType;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.apigateway.Integration;
import software.amazon.awscdk.services.apigateway.IntegrationOptions;
import software.amazon.awscdk.services.apigateway.IntegrationResponse;
import software.amazon.awscdk.services.apigateway.IntegrationType;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.Method;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.MethodResponse;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.SingletonFunction;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.Source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApiPollyStack extends Stack {
    public ApiPollyStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ApiPollyStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        RestApi api = RestApi.Builder.create(this, "MyApi")
                .restApiName("ApiPolly")
                .binaryMediaTypes(List.of("*/*")) // This enables all binary data types to go to s3 correctly
                .build();

        Bucket bucket = Bucket.Builder.create(this, "MyBucket")
                .removalPolicy(RemovalPolicy.DESTROY)
                .publicReadAccess(true)
                .websiteIndexDocument("polly.html")
                .websiteErrorDocument("error.html")
                .build();

        BucketDeployment.Builder.create(this, "Deployment")
                .sources(List.of(Source.asset("assets")))
                .destinationBucket(bucket)
                .build();

        buildGetMethod(api, "rootGetMethod", buildRole(), "GET", api.getRoot(), bucket);
        buildPostMethod(api, "rootPostMethod", buildPollyRole(), "POST", api.getRoot());
    }

    private Role buildRole() {
        return Role.Builder.create(this, "MyRole")
                .assumedBy(ServicePrincipal.Builder.create("apigateway.amazonaws.com")
                        .build())
                .managedPolicies(List.of(ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")))
                .build();
    }
    private Role buildPollyRole() {
        return Role.Builder.create(this, "MyPollyRole")
                .assumedBy(ServicePrincipal.Builder.create("apigateway.amazonaws.com")
                        .build())
                .managedPolicies(List.of(ManagedPolicy.fromAwsManagedPolicyName("AmazonPollyFullAccess")))
                .build();
    }

    private Method buildGetMethod(RestApi restApi, String name, Role role, String method, IResource resource, IBucket bucket) {
        return Method.Builder.create(restApi, name)
                .httpMethod(method)
                .resource(resource)
                .integration(Integration.Builder.create()
                        .type(IntegrationType.HTTP_PROXY)
                        .integrationHttpMethod(method)
                        .uri(Fn.sub("${url}/polly.html", Map.of("url", bucket.getBucketWebsiteUrl())))
                        .options(IntegrationOptions.builder()
                                .credentialsRole(role)
                                .integrationResponses(buildIntegrationResponses())
                                .build())
                        .build())
                .options(MethodOptions.builder()
                        .authorizationType(AuthorizationType.NONE)
                        .methodResponses(buildMethodResponses())
                        .build())
                .build();
    }

    // post to lambda ?
    private Method buildPostMethod(RestApi api, String name, Role role, String method, IResource resource) {
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
                        .uri(Fn.sub("arn:aws:apigateway:${AWS::Region}:polly:path/v1/speech"))
                        .options(IntegrationOptions.builder()
//                                .requestTemplates(new HashMap<String,String>() {
//                                    {
//                                        put("application/json", "#set($inputRoot = $input.path('$'))\n" +
//                                                "{\n" +
//                                                "\"OutputFormat\": \"mp3\",\n" +
//                                                "\"Text\": \"$method.request.body.phrase\",\n" +
//                                                "\"VoiceId\": \"Amy\"\n" +
//                                                "}");
//                                    }
//                                })
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
