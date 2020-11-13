package com.version2software.apiddb;

import org.junit.Test;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.cxapi.CloudAssembly;
import software.amazon.awscdk.cxapi.CloudFormationStackArtifact;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApiDdbTest {
    @Test
    public void testStack() {
        App app = new App();
        ApiDdbStack stack = new ApiDdbStack(app, "test");

        CloudAssembly cloudAssembly = app.synth();
        CloudFormationStackArtifact artifact = cloudAssembly.getStackArtifact("test");

        Map<String, Map> map = (Map) artifact.getTemplate();

        Map resources = map.get("Resources");
        Map outputs = map.get("Outputs");

        assertEquals("test", stack.getStackName());
        assertEquals(26, resources.size());
        assertEquals(2, outputs.size());
    }
}
