package com.bstek.ureport.console.util;

import com.baomidou.mybatisplus.annotation.DbType;
import smart.util.data.DataSourceContextHolder;
import smart.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.*;

@Slf4j
public class JdbcUtil {

    /**
     * 连接Connection
     *
     * @param userName 用户名
     * @param password 密码
     * @param url      url
     * @return
     */
    public static Connection getConn(String userName, String password, String url) {
        final Connection[] conn = {null};
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                //执行耗时代码
                try {
                    if (url.contains(DbType.MYSQL.getDb())) {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        String connectionUrl = url;
                        conn[0] = DriverManager.getConnection(connectionUrl, userName, password);
                    } else if (url.contains(DbType.ORACLE.getDb())) {
                        Class.forName("oracle.jdbc.OracleDriver");
                        String connectionUrl = url;
                        conn[0] = DriverManager.getConnection(connectionUrl, userName, password);
                    } else if (url.contains(DbType.SQL_SERVER.getDb())) {
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        String connectionUrl = url;
                        conn[0] = DriverManager.getConnection(connectionUrl, userName, password);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                return "jdbc连接成功";
            }
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(task);
        try {
            //设置超时时间
            String rst = future.get(3L, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("连接数据库超时");
        } catch (Exception e) {
            log.error("获取数据异常254," + e.getStackTrace());
        } finally {
            executorService.shutdown();
        }

        return conn[0];
    }

    /**
     * 自定义sql语句(查)
     */
    public static ResultSet query(Connection conn, String sql) {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            //开启事务
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            //提交事务
            conn.commit();
            return resultSet;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    //判断当前使用数据链接
    public static Connection getConnection(String multiTenancy,DataSourceUtil dataSourceUtil){
        Connection conn = null;
        if (multiTenancy.equals("false")) {
            conn = getConn(dataSourceUtil.getUserName(), dataSourceUtil.getPassword(), dataSourceUtil.getUrl().replace("{dbName}", dataSourceUtil.getDbName()));
        } else {
            conn = getConn(dataSourceUtil.getUserName(), dataSourceUtil.getPassword(), dataSourceUtil.getUrl().replace("{dbName}", DataSourceContextHolder.getDatasourceName()));
        }
        return conn;
    }

}
