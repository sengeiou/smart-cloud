package smart.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:47
 */
@Data
@Component
public class DataSourceUtil {

    /**
     * 数据库类型
     */
    @Value("${spring.dataType}")
    private String dataType;

    //-----------------------------------数据配置
    /**
     * 驱动包
     */
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    /**
     * 库名
     */
    @Value("${spring.datasource.dbname}")
    private String dbName;
    /**
     * 初始库名
     */
    @Value("${spring.datasource.dbinit:}")
    private String dbInit;
    /**
     * 空库名
     */
    @Value("${spring.datasource.dbnull:}")
    private String dbNull;
    /**
     * 数据连接字符串
     */
    @Value("${spring.datasource.url}")
    private String url;
    /**
     * 账号
     */
    @Value("${spring.datasource.username}")
    private String userName;
    /**
     * 密码
     */
    @Value("${spring.datasource.password}")
    private String password;

    //----------Oracle表空间(暂无)
    /**
     * 密码
     */
    @Value("${spring.tableSpace:JNPFCLOUD}")
    private String tableSpace;
}
