package com.version2software.appsynctrivial;

import software.amazon.awscdk.core.App;

public class AppSyncTrivialApp {
    public static void main(final String[] args) {
        App app = new App();

        new AppSyncTrivialStack(app, "appsync-trivial-example");

        app.synth();
    }
}
