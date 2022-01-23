package smart.emnus;

/**
 * 数据库类型枚举类
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public enum DbType {

    /**
     * mysql
     */
    MYSQL("mysql"),
    /**
     * oracle
     */
    ORACLE("oracle"),
    /**
     * sqlserver
     */
    SQLSERVER("sqlserver");

    private String message;

    DbType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
