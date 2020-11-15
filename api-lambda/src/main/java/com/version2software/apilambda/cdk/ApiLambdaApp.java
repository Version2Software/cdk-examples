package com.version2software.apilambda.cdk;

import software.amazon.awscdk.core.App;

public class ApiLambdaApp {
    public static void main(final String[] args) {
        App app = new App();

        new ApiLambdaStack(app, "api-lambda-example");

        app.synth();
    }
}
