package com.version2software.appsyncddb.cdk;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.appsync.AppsyncFunction;
import software.amazon.awscdk.services.appsync.AuthorizationConfig;
import software.amazon.awscdk.services.appsync.AuthorizationMode;
import software.amazon.awscdk.services.appsync.AuthorizationType;
import software.amazon.awscdk.services.appsync.BaseResolverProps;
import software.amazon.awscdk.services.appsync.DynamoDbDataSource;
import software.amazon.awscdk.services.appsync.GraphqlApi;
import software.amazon.awscdk.services.appsync.MappingTemplate;
import software.amazon.awscdk.services.appsync.Resolver;
import software.amazon.awscdk.services.appsync.Schema;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.ITable;
import software.amazon.awscdk.services.dynamodb.Table;

import java.util.List;

public class AppSyncDdbStack extends Stack {
    public AppSyncDdbStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AppSyncDdbStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        GraphqlApi api = GraphqlApi.Builder.create(this, "MyApi")
                .name("appsync-ddb-example")
                .schema(Schema.fromAsset("assets/schema/schema.graphql"))
                .authorizationConfig(AuthorizationConfig.builder()
                        .defaultAuthorization(AuthorizationMode.builder()
                                .authorizationType(AuthorizationType.API_KEY)
                                .build())
                        .build())
                .build();

        ITable table = Table.Builder.create(this, "MyTable")
                .tableName("appsync-ddb-example-table")
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .removalPolicy(RemovalPolicy.DESTROY)
                .partitionKey(Attribute.builder()
                        .name("PK")
                        .type(AttributeType.STRING)
                        .build())
                .sortKey(Attribute.builder()
                        .name("SK")
                        .type(AttributeType.STRING)
                        .build())
                .build();

        DynamoDbDataSource dataSource = api.addDynamoDbDataSource("MyDataSource", table);

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Query")
                .fieldName("invoice")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/invoiceRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/invoiceResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Invoice")
                .fieldName("lineItems")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/lineItemRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/lineItemResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Invoice")
                .fieldName("customer")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/customerRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/customerResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Customer")
                .fieldName("shipAddress")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/shipAddressRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/shipAddressResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Customer")
                .fieldName("billAddress")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/billAddressRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/billAddressResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("LineItem")
                .fieldName("product")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/productRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/productResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Mutation")
                .fieldName("addInvoice")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/addInvoiceRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/addInvoiceResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Mutation")
                .fieldName("addLineItem")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/addLineItemRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/addLineItemResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Mutation")
                .fieldName("addProduct")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/addProductRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/addProductResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Mutation")
                .fieldName("updateProductPrice")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/updateProductPriceRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/updateProductPriceResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Mutation")
                .fieldName("addCustomer")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/addCustomerRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/addCustomerResponse.vm"))
                .build());

        dataSource.createResolver(BaseResolverProps.builder()
                .typeName("Mutation")
                .fieldName("addAddress")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/addAddressRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/addAddressResponse.vm"))
                .build());

        // Implement a cascade delete of invoice and line items using a pipeline.
        // Build the pipeline by first getting line item ids (see deleteInvoiceQueryFunctionRequest.vm) and then
        // calling BatchDeleteItem (see deleteInvoiceMutationFunctionRequest.vm).
        AppsyncFunction deleteInvoiceQueryFunction = AppsyncFunction.Builder.create(this, "deleteInvoiceQueryFunction")
                .name("deleteInvoiceQueryFunction")
                .dataSource(dataSource)
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/deleteInvoiceQueryFunctionRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/deleteInvoiceQueryFunctionResponse.vm"))
                .api(api)
                .build();

        AppsyncFunction deleteInvoiceMutationFunction = AppsyncFunction.Builder.create(this, "deleteInvoiceMutationFunction")
                .name("deleteInvoiceMutationFunction")
                .dataSource(dataSource)
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/deleteInvoiceMutationFunctionRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/deleteInvoiceMutationFunctionResponse.vm"))
                .api(api)
                .build();

//        new Resolver().createResolver(BaseResolverProps.builder()
        Resolver.Builder.create(this, "deleteInvoicePipeline")
                .typeName("Mutation")
                .fieldName("deleteInvoice")
                .requestMappingTemplate(MappingTemplate.fromFile("assets/velocity/deleteInvoiceRequest.vm"))
                .responseMappingTemplate(MappingTemplate.fromFile("assets/velocity/deleteInvoiceResponse.vm"))
                .pipelineConfig(List.of(deleteInvoiceQueryFunction, deleteInvoiceMutationFunction))
                .api(api)
                .build();
        // End of pipeline
/*
const pipelineResolver = new appsync.Resolver(stack, 'pipeline', {
  name: 'pipeline_resolver',
  api: api,
  dataSource: apiDataSource,
  requestMappingTemplate: appsync.MappingTemplate.fromFile('beforeRequest.vtl'),
  pipelineConfig: [appsyncFunction],
  responseMappingTemplate: appsync.MappingTemplate.fromFile('afterResponse.vtl'),
});
 */
        CfnOutput.Builder.create(this, "GraphQlUrl")
                .value(api.getGraphqlUrl())
                .build();

        CfnOutput.Builder.create(this, "ApiKey")
                .value(api.getApiKey())
                .build();
    }
}
