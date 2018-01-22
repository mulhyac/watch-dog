package com.mulhyac.netty.http.config;

import java.util.Map;

public interface StatusInitializer {

    /**
     * 初始化错误代码
     * @return 错误代码与提示信息关系
     */
    public Map<String, String> init();

}
