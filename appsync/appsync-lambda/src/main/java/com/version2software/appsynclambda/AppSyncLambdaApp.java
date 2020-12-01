package com.version2software.appsynclambda;

import software.amazon.awscdk.core.App;

public class AppSyncLambdaApp {
    public static void main(final String[] args) {
        App app = new App();

        new AppSyncLambdaStack(app, "appsync-lambda-example");

        app.synth();
    }
}
