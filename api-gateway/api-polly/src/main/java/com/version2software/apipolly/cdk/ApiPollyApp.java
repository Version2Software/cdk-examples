package com.version2software.apipolly.cdk;

import software.amazon.awscdk.core.App;

public class ApiPollyApp {
    public static void main(final String[] args) {
        App app = new App();

        new ApiPollyStack(app, "api-polly-example");

        app.synth();
    }
}
