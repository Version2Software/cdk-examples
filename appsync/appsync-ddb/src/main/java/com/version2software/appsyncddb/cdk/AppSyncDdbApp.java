package com.version2software.appsyncddb.cdk;

import software.amazon.awscdk.core.App;

public class AppSyncDdbApp {
    public static void main(final String[] args) {
        App app = new App();

        new AppSyncDdbStack(app, "appsync-ddb-example");

        app.synth();
    }
}
