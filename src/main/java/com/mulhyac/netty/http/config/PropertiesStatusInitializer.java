package com.mulhyac.netty.http.config;

import java.util.HashMap;
import java.util.Map;

public class PropertiesStatusInitializer implements StatusInitializer {

    @Override
    public Map<String, String> init() {
        Map<String, String> errors = new HashMap<String, String>();
        errors.put("404", "Not found.");
        errors.put("500", "An internal error occurred on the report server.");
        errors.put("502", "Bad Gateway.");
        errors.put("200", "Ok.");
        errors.put("301", "Redirect.");
        errors.put("10001", "Invalid request json.");
        errors.put("10002", "Invalid request.");
        errors.put("10003", "Invalid request args.");
        errors.put("20000001", "用户退出成功.");
        errors.put("Y", "Y");
        errors.put("0000", "成功");
        errors.put("9999", "系统异常");
        errors.put("6666", "用户标记失效，请重新登录！");
        errors.put("200002", "失败，密码错误");
        errors.put("200003", "失败，短信验证码错误");
        errors.put("200004", "失败，短信验证码超时");
        errors.put("default", "对不起，系统繁忙，请稍候再试");
        errors.put("00000001", "客户端异常");
        errors.put("00000002", "失败，请求数据有误");
        errors.put("00000003", "红包过期");
        errors.put("00000004", "红包无效");
        errors.put("00000006", "通讯超时");
        errors.put("00000007", "通讯异常");
        errors.put("000000013", "客户端已是最高版本");
        return errors;
    }
}
