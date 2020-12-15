package com.version2software.lambda.cdk;

import software.amazon.awscdk.core.App;

public class LambdaDockerJavaApp {
    public static void main(final String[] args) {
        App app = new App();

        new LambdaDockerJavaStack(app, "lambda-docker-java-example");

        app.synth();
    }
}
