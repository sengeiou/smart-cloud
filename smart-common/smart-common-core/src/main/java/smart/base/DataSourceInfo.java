package smart.base;

/**
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-04-07
 */
public class DataSourceInfo {
    public static final String mysqlUrl="jdbc:mysql://{host}:{port}/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC";

    public static final String mysqlDriver="com.mysql.cj.jdbc.Driver";



    public static final String oracleUrl="jdbc:oracle:thin:@{host}:{port}:{dbName}";

    public static final String oracleDriver="oracle.jdbc.OracleDriver";



    public static final String sqlserverUrl="jdbc:sqlserver://{host}:{port};Databasename={dbName}";

    public static final String sqlserverDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
}
