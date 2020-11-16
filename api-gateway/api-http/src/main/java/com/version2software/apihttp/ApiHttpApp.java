package com.version2software.apihttp;

import software.amazon.awscdk.core.App;

public class ApiHttpApp {
    public static void main(final String[] args) {
        App app = new App();

        new ApiHttpStack(app, "api-http-example");

        app.synth();
    }
}
