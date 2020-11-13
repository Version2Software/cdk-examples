package com.version2software.apis3;

import com.version2software.apis3.cdk.ApiS3Stack;
import org.junit.Test;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.cxapi.CloudAssembly;
import software.amazon.awscdk.cxapi.CloudFormationStackArtifact;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApiS3Test {

    @Test
    public void testStack() {
        App app = new App();
        ApiS3Stack stack = new ApiS3Stack(app, "test");

        CloudAssembly cloudAssembly = app.synth();
        CloudFormationStackArtifact artifact = cloudAssembly.getStackArtifact("test");

        Map<String, Map> map = (Map) artifact.getTemplate();

        Map resources = map.get("Resources");
        Map outputs = map.get("Outputs");

        assertEquals("test", stack.getStackName());
        assertEquals(15, resources.size());
        assertEquals(1, outputs.size());
    }
}
