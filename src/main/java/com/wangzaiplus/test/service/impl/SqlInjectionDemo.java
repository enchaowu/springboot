package com.wangzaiplus.test.service.impl;

import java.sql.*;

public class SqlInjectionDemo {

    // 数据库连接（仅演示）
    private static final String URL = "jdbc:mysql://localhost:3306/testdb?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * 【存在高危 SQL 注入漏洞】
     * 直接把用户输入拼接到 SQL 字符串中，未做任何过滤/预编译
     */
    public boolean login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = '" + username + "' AND password = '" + password + "'";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("执行的SQL：" + sql); // 教学用：打印真实执行的SQL
            
            // 只要查到结果就认为登录成功
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SqlInjectionDemo demo = new SqlInjectionDemo();
        
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
