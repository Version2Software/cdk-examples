package com.version2software.lambda.app;

import com.amazonaws.lambda.thirdparty.com.google.gson.Gson;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class Handler implements RequestHandler<Map, Map>{

    @Override
    public Map handleRequest(Map event, Context context) {
        System.out.println("event = " + event);

        Map r = (Map) event.get("requestContext");
        r.put("accountId", "REDACTED");

        Map map = new HashMap();
        map.put("statusCode", 200);
        map.put("body", new Gson().toJson(event));

        return map;
    }
}
