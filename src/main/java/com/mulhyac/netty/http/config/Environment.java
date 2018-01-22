package com.mulhyac.netty.http.config;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    static Map<String, Object> environment = new HashMap<String, Object>();

    public static Object get(String key){
        return environment.get(key);
    }

}
