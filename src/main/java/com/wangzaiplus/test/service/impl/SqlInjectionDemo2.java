package com.wangzaiplus.test.service.impl;

public class SqlInjectionDemo2 extends SqlInjectionDemo {

    public static void main(String[] args) {
        SqlInjectionDemo2 demo = new SqlInjectionDemo2();
        
        // 正常登录
        // boolean result = demo.login("zhangsan", "123456");

        // =======================
        // SQL 注入攻击演示
        // =======================
        // 注入用户名：任意值' OR '1'='1
        // 密码随便填
        boolean result = demo.login("' OR '1'='1", "anything");

        System.out.println("登录结果：" + result); // 输出 true，登录成功！
    }
}
