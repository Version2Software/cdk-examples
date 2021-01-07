package com.version2software.appsyncddb;

import com.version2software.appsyncddb.cdk.AppSyncDdbStack;
import org.junit.Test;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.cxapi.CloudAssembly;
import software.amazon.awscdk.cxapi.CloudFormationStackArtifact;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AppSyncDdbTest {

    @Test
    public void testStack() {
        App app = new App();
        AppSyncDdbStack stack = new AppSyncDdbStack(app, "test");

        CloudAssembly cloudAssembly = app.synth();
        CloudFormationStackArtifact artifact = cloudAssembly.getStackArtifact("test");

        Map<String, Map> map = (Map) artifact.getTemplate();

        Map resources = map.get("Resources");
        Map outputs = map.get("Outputs");

        assertEquals("test", stack.getStackName());
        assertEquals(22, resources.size());
        assertEquals(2, outputs.size());
    }
}
