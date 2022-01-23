package smart.util;

import com.google.common.base.Joiner;
import smart.emnus.DbDriverEnum;
import smart.emnus.DbType;
import smart.exception.DataException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Slf4j
public class JdbcUtil {

    //-----------------------------jdbc工具类----------------------------------------------------

    /**
     * 自定义sql语句(适合增、删、改)
     */
    public static int custom(Connection conn, String sql) throws DataException {
        int result = 0;
        try {
            String dbType = conn.getMetaData().getDatabaseProductName().trim().toLowerCase();
            if (DbType.ORACLE.getMessage().equals(dbType)) {
                //开启事务
                conn.setAutoCommit(false);
                String[] sqls = sql.split(";");
                PreparedStatement preparedStatement;
                for (String sqlOne : sqls) {
                    sqlOne=sqlOne.toLowerCase();
                    if (sqlOne.contains("insert") && sqlOne.trim().contains("),(")) {
                        List<String> chilSqls=new ArrayList<>();
                        String[] splitSql=sqlOne.split("\\),\\(");
                        String headerSql="insert all ";
                        String centerSql=splitSql[0].split("VALUES")[0].split("insert")[1];
                        for(int i=0;i<splitSql.length;i++){
                            if(i==0){
                                chilSqls.add( splitSql[i].split("insert")[1]+")" );
                            }else if(i==splitSql.length-1){
                                chilSqls.add( centerSql+"("+splitSql[i] );
                            }else{
                                chilSqls.add( centerSql+"("+splitSql[i]+")" );
                            }
                        }
                        String insertSql= headerSql+Joiner.on(" ").join(chilSqls);
                        System.out.println(insertSql);
                    }else{
                        preparedStatement = conn.prepareStatement(sqlOne);
                        preparedStatement.executeUpdate();
                        result++;
                    }
                }
            } else {
                //开启事务
                conn.setAutoCommit(false);
                String[] sqls = sql.split(";");
                PreparedStatement preparedStatement;
                for (String sqlOne : sqls) {
                    preparedStatement = conn.prepareStatement(sqlOne);
                    preparedStatement.executeUpdate();
                    result++;
                }
            }
            //提交事务
            conn.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            try{
                conn.rollback();
            }catch (SQLException q){
                throw new DataException("数据错误:" + q.getMessage());
            }

            throw new DataException("数据错误:" + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new DataException("数据错误:" + e.getMessage());
            }
        }
    }

    /**
     * 自定义sql语句(适合增、删、改)
     */
    public static int getCustom(Connection conn, String sql) throws DataException {
        int result = 0;
        try {
            String dbType = conn.getMetaData().getDatabaseProductName().trim().toLowerCase();
            if (DbType.ORACLE.getMessage().equals(dbType)) {
                //开启事务
                conn.setAutoCommit(false);
                String[] sqls = sql.split(";");
                PreparedStatement preparedStatement;
                for (String sqlOne : sqls) {
                    sqlOne=sqlOne.toLowerCase();
                    if (sqlOne.contains("insert") && sqlOne.trim().contains("),(")) {
                        List<String> chilSqls=new ArrayList<>();
                        String[] splitSql=sqlOne.split("\\),\\(");
                        String headerSql="insert all ";
                        String centerSql=splitSql[0].split("VALUES")[0].split("insert")[1];
                        for(int i=0;i<splitSql.length;i++){
                            if(i==0){
                                chilSqls.add( splitSql[i].split("insert")[1]+")" );
                            }else if(i==splitSql.length-1){
                                chilSqls.add( centerSql+"("+splitSql[i] );
                            }else{
                                chilSqls.add( centerSql+"("+splitSql[i]+")" );
                            }
                        }
                        String insertSql= headerSql+ Joiner.on(" ").join(chilSqls);
                        System.out.println(insertSql);
                    }else{
                        preparedStatement = conn.prepareStatement(sqlOne);
                        preparedStatement.executeUpdate();
                        result++;
                    }
                }
            } else {
                //开启事务
                conn.setAutoCommit(false);
                String[] sqls = sql.split(";");
                PreparedStatement preparedStatement;
                for (String sqlOne : sqls) {
                    preparedStatement = conn.prepareStatement(sqlOne);
                    preparedStatement.executeUpdate();
                    result++;
                }
            }
            //提交事务
            conn.commit();
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataException("数据错误:" + e.getMessage());
        }
    }

    /**
     * 自定义sql语句(查)
     */
    public static ResultSet query(Connection conn, String sql) throws DataException {
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
            throw new DataException("数据错误:" + e.getMessage());
        }
    }


    public static void main(String[] args) throws SQLException, DataException {
//       Connection conn= getConn("sqlserver","sa","JNPFjnpf2020","192.168.0.177","1433","new_vue");
        Connection conn = getConn("mysql", "jnpf_test", "MphCGJJe7yRcjEDy", "192.168.0.31", "3306", "jnpf_test");
    }

    /**
     * 连接Connection
     *
     * @param url      url
     * @param name     用户名
     * @param password 密码
     * @return
     */
    public static boolean getConn(String type, String url, String name, String password, String dbName) {

        Connection conn = null;
        try {
            if (type.toLowerCase().equals(DbType.MYSQL.getMessage())) {
                Class.forName(DbDriverEnum.MYSQL.getDbDriver());
                String connectionUrl = url.replace("{dbName}", dbName);
                conn = DriverManager.getConnection(connectionUrl, name, password);
            } else if (type.toLowerCase().equals(DbType.ORACLE.getMessage())) {
                Class.forName(DbDriverEnum.ORACLE.getDbDriver());
                String connectionUrl = url.replace("{dbName}", dbName);
                conn = DriverManager.getConnection(connectionUrl, name, password);
            } else if (type.toLowerCase().equals(DbType.SQLSERVER.getMessage())) {
                Class.forName(DbDriverEnum.SQLSERVER.getDbDriver());
                String connectionUrl = url.replace("{dbName}", dbName);
                conn = DriverManager.getConnection(connectionUrl, name, password);
            }
            conn.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 连接Connection
     *
     * @param userName 用户名
     * @param password 密码
     * @param host     ip地址
     * @param port     端口
     * @return
     */
    public static Connection getConn(String dataType, String userName, String password, String host, String port, String dbName) {
        final Connection[] conn = {null};
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                //执行耗时代码
                try {
                    if (dataType.toLowerCase().equals(DbType.MYSQL.getMessage())) {
                        Class.forName(DbDriverEnum.MYSQL.getDbDriver());
                        String connectionUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useUnicode=true&autoReconnect=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
                        conn[0] = DriverManager.getConnection(connectionUrl, userName, password);
                    } else if (dataType.toLowerCase().equals(DbType.ORACLE.getMessage())) {
                        Class.forName(DbDriverEnum.ORACLE.getDbDriver());
                        String connectionUrl = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;
                        conn[0] = DriverManager.getConnection(connectionUrl, userName, password);
                    } else if (dataType.toLowerCase().equals(DbType.SQLSERVER.getMessage())) {
                        Class.forName(DbDriverEnum.SQLSERVER.getDbDriver());
                        String connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName;
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
            log.error("连接数据库" + dbName + "超时");
        } catch (Exception e) {
            log.error("获取数据异常204," + e.getMessage());
        } finally {
            executorService.shutdown();
        }

        return conn[0];
    }

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
                    if (url.contains(DbType.MYSQL.getMessage())) {
                        Class.forName(DbDriverEnum.MYSQL.getDbDriver());
                        String connectionUrl = url;
                        conn[0] = DriverManager.getConnection(connectionUrl, userName, password);
                    } else if (url.contains(DbType.ORACLE.getMessage())) {
                        Class.forName(DbDriverEnum.ORACLE.getDbDriver());
                        String connectionUrl = url;
                        conn[0] = DriverManager.getConnection(connectionUrl, userName, password);
                    } else if (url.contains(DbType.SQLSERVER.getMessage())) {
                        Class.forName(DbDriverEnum.SQLSERVER.getDbDriver());
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
     * 获取url
     *
     * @param userName 用户名
     * @param password 密码
     * @param host     ip地址
     * @param port     端口
     * @param sid      sid
     * @return
     */
    public static String getUrl(String dataType, String userName, String password, String host, String port, String dbName, String sid) {
        String connectionUrl = null;
        if (dataType.toLowerCase().equals(DbType.MYSQL.getMessage())) {
            connectionUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useUnicode=true&autoReconnect=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC" + "." + userName + "." + password;
        } else if (dataType.toLowerCase().equals(DbType.ORACLE.getMessage())) {
            connectionUrl = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid + "." + userName + "." + password;
        } else if (dataType.toLowerCase().equals(DbType.SQLSERVER.getMessage())) {
            connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName + "." + userName + "." + password;
        }
        return connectionUrl;
    }


    //-----------------------------mysql----------------------------------------------------

    /**
     * mysql 备份命令
     *
     * @param root     账号
     * @param pwd      密码
     * @param host     ip
     * @param dbName   数据库
     * @param backPath 备份路径
     * @param backName 文件名称
     * @return
     */
    public static boolean mysqlBackUp(String host, String root, String pwd, String dbName, String backPath, String backName) {
        StringBuffer mysql = new StringBuffer();
        mysql.append("mysqldump");
        mysql.append(" -h" + host);
        mysql.append(" -u" + root);
        mysql.append(" -p" + pwd);
        mysql.append(" " + dbName);
        boolean result = backup(mysql.toString(), backPath + backName);
        return result;
    }

    /**
     * mysql的备份方法
     *
     * @param command  命令行
     * @param savePath 备份路径
     * @return
     */
    private static boolean backup(String command, String savePath) {
        boolean flag;
        // 获得与当前应用程序关联的Runtime对象
        Runtime r = Runtime.getRuntime();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            // 在单独的进程中执行指定的字符串命令
            Process p = r.exec(command);
            // 获得连接到进程正常输出的输入流，该输入流从该Process对象表示的进程的标准输出中获取数据
            InputStream is = p.getInputStream();
            // InputStreamReader是从字节流到字符流的桥梁：它读取字节，并使用指定的charset将其解码为字符
            InputStreamReader isr = new InputStreamReader(is, Constants.UTF8);
            //BufferedReader从字符输入流读取文本，缓冲字符，提供字符，数组和行的高效读取
            br = new BufferedReader(isr);
            String s;
            StringBuffer sb = new StringBuffer("");
            // 组装字符串
            while ((s = br.readLine()) != null) {
                sb.append(s + System.lineSeparator());
            }
            s = sb.toString();
            // 创建文件输出流
            FileOutputStream fos = new FileOutputStream(savePath);
            // OutputStreamWriter是从字符流到字节流的桥梁，它使用指定的charset将写入的字符编码为字节
            OutputStreamWriter osw = new OutputStreamWriter(fos, Constants.UTF8);
            // BufferedWriter将文本写入字符输出流，缓冲字符，以提供单个字符，数组和字符串的高效写入
            bw = new BufferedWriter(osw);
            bw.write(s);
            bw.flush();
            flag = true;
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            //由于输入输出流使用的是装饰器模式，所以在关闭流时只需要调用外层装饰类的close()方法即可，
            //它会自动调用内层流的close()方法
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    //-----------------------------sqlserver----------------------------------------------------

    /**
     * 数据库备份
     *
     * @param userName     用户名
     * @param password     密码
     * @param host         ip
     * @param port         端口
     * @param databaseName 数据库
     * @param path         备份路径
     * @param fileName     备份名称
     */
    public static void serverBackUp(String userName, String password, String host, String port, String path, String fileName, String databaseName) {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            File saveFile = new File(path);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            StringBuilder backup = new StringBuilder();
            backup.append("backup database ");
            backup.append(databaseName + " to disk='" + path + fileName + "' ");
            conn = getConn("SqlServer", userName, password, host, port, databaseName);
            stmt = conn.prepareStatement(backup.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    //-----------------------------oracle----------------------------------------------------

    /**
     * 备份指定用户数据库
     *
     * @param userName 用户名
     * @param password 密码
     * @param sid      用户所在的SID
     * @param host     ip
     * @param path     保存路径
     * @param fileName 保存名称
     */
    public static void oracleBackUp(String userName, String password, String host, String sid, String path, String fileName) {
        try {
            File saveFile = new File(path);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            StringBuffer exp = new StringBuffer();
            exp.append("exp ");
            exp.append(userName);
            exp.append("/");
            exp.append(password);
            exp.append("@");
            exp.append(host);
            exp.append("/");
            exp.append(sid);
            exp.append(" file=");
            exp.append(path + "/" + fileName);
            Process p = Runtime.getRuntime().exec(exp.toString());
            InputStreamReader isr = new InputStreamReader(p.getErrorStream());
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf("错误") != -1) {
                    break;
                }
            }
            p.destroy();
            p.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    //----------------------jdbc转成对象------------------

    /**
     * jdbc 多条数据查询转成list
     *
     * @param rs result 查询的结果
     * @return
     */
    public static List<Map<String, Object>> convertList(ResultSet rs) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>(16);
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return list;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * jdbc 单条数据查询转成map
     *
     * @param rs result 查询的结果
     * @return
     */
    public static Map<String, Object> convertMap(ResultSet rs) {
        Map<String, Object> map = new TreeMap<>();
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    map.put(md.getColumnName(i), rs.getObject(i));
                }
            }
            return map;
        } catch (SQLException e) {
            e.printStackTrace();
            return map;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------jdbc转成对象String------------------

    /**
     * jdbc 多条数据查询转成list
     *
     * @param rs result 查询的结果
     * @return
     */
    public static List<Map<String, Object>> convertListString(ResultSet rs) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>(16);
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getString(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return list;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * jdbc 单条数据查询转成map
     *
     * @param rs result 查询的结果
     * @return
     */
    public static Map<String, Object> convertMapString(ResultSet rs) {
        Map<String, Object> map = new TreeMap<>();
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    map.put(md.getColumnName(i), rs.getString(i));
                }
            }
            return map;
        } catch (SQLException e) {
            e.printStackTrace();
            return map;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * jdbc 多条数据查询转成list
     *
     * @param rs result 查询的结果，datetime转为时间戳
     * @return
     */
    public static List<Map<String, Object>> convertList2(ResultSet rs) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>(16);
                for (int i = 1; i <= columnCount; i++) {
                    if (rs.getObject(i) != null) {
                        if (md.getColumnType(i) != 93) {
                            rowData.put(md.getColumnLabel(i), String.valueOf(rs.getObject(i)));
                        } else {
                            rowData.put(md.getColumnLabel(i), DateUtil.stringToDate(String.valueOf(rs.getObject(i))).getTime());
                        }
                    }
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return list;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;

    }
    /**
     * 数据建模更新表字段
     */
    public static int upTableFields(Connection conn, String delSql, String crSql,String ramTable,String newTable) throws DataException{
        int result;
        try {
            //开启事务
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement = conn.prepareStatement(crSql);
            preparedStatement.executeUpdate();
            //提交事务
            conn.commit();
            PreparedStatement preparedStatementdrop = conn.prepareStatement(delSql+"; alter table "+ramTable+" rename to "+newTable+";");
            result = preparedStatementdrop.executeUpdate();
            //提交事务
            conn.commit();
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataException("数据错误:" + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new DataException("数据错误:" + e.getMessage());
            }
        }
    }

    /**
     * 连接Connection
     *
     * @return
     */
    public static Connection getConn(String driver, String userName, String password, String url) {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return conn;
    }

    /**
     * 判断当前数据库类型
     * @param driver
     * @return
     */
    public static boolean getDbType(String driver){
        if (driver.toLowerCase().contains(DbType.MYSQL.getMessage())){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 自定义sql语句(适合增、删、改)
     */
    public static int customs(Connection conn, String sql) throws Exception {
        int result = 0;
        try {
            //开启事务
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            result = preparedStatement.executeUpdate();
            //提交事务
            conn.commit();
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("数据错误:" + e.getMessage());
        } finally {
            try {
                if (conn != null) {

                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new Exception("数据错误:" + e.getMessage());
            }
        }
    }

}
