package com.version2software.apiddb;

import software.amazon.awscdk.core.App;

public class ApiDdbApp {
    public static void main(final String[] args) {
        App app = new App();

        new ApiDdbStack(app, "api-ddb-example");

        app.synth();
    }
}
