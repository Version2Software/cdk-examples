package com.version2software.apis3.cdk;

import software.amazon.awscdk.core.App;

public class ApiS3App {
    public static void main(final String[] args) {
        App app = new App();

        new ApiS3Stack(app, "ApiS3Demo");

        app.synth();
    }
}
