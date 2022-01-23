package smart.base.util;




import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import smart.base.DataSourceInfo;
import smart.base.entity.DbLinkEntity;
import smart.util.StringUtil;
import smart.util.DataSourceUtil;
import smart.util.context.SpringContext;

public class SourceUtil {
    public  DataSourceConfig dbConfig(String dbName){
        DataSourceConfig dsc=new DataSourceConfig();
        DataSourceUtil dataSourceUtil = SpringContext.getBean(DataSourceUtil.class);
        if (dataSourceUtil.getUrl().contains(DbType.MYSQL.getDb())) {
            dsc.setDbType(DbType.MYSQL);
            dsc.setDriverName(DataSourceInfo.mysqlDriver);
        } else if (dataSourceUtil.getUrl().contains(DbType.ORACLE.getDb())) {
            dsc.setDbType(DbType.ORACLE);
            dsc.setDriverName(DataSourceInfo.oracleDriver);
        }  else if (dataSourceUtil.getUrl().contains(DbType.SQL_SERVER.getDb())) {
            dsc.setDbType(DbType.SQL_SERVER);
            dsc.setDriverName(DataSourceInfo.sqlserverDriver);
        }
        dsc.setUsername(dataSourceUtil.getUserName());
        dsc.setPassword(dataSourceUtil.getPassword());
        if(StringUtil.isEmpty(dbName)){
            dbName = dataSourceUtil.getDbName();
        }
        dsc.setUrl(dataSourceUtil.getUrl().replace("{dbName}" , dbName));
        return dsc;
    }

    public  DataSourceConfig dbConfig(DbLinkEntity linkEntity){
        DataSourceConfig dsc=new DataSourceConfig();
        if (linkEntity.getDbType().equalsIgnoreCase(DbType.MYSQL.getDb())) {
            dsc.setDbType(DbType.MYSQL);
            dsc.setDriverName(DataSourceInfo.mysqlDriver);
            dsc.setUrl(DataSourceInfo.mysqlUrl
                    .replace("{host}",linkEntity.getHost())
                    .replace("{port}",linkEntity.getPort())
                    .replace("{dbName}",linkEntity.getServiceName()));
        } else if (linkEntity.getDbType().equalsIgnoreCase(DbType.ORACLE.getDb())) {
            dsc.setDbType(DbType.ORACLE);
            dsc.setDriverName(DataSourceInfo.oracleDriver);
            dsc.setUrl(DataSourceInfo.oracleUrl
                    .replace("{host}",linkEntity.getHost())
                    .replace("{port}",linkEntity.getPort())
                    .replace("{dbName}",linkEntity.getServiceName()));
            //oracle 默认 schema=username
            dsc.setSchemaName(linkEntity.getUserName().toUpperCase());
        }  else if (linkEntity.getDbType().equalsIgnoreCase(DbType.SQL_SERVER.getDb())) {
            dsc.setDbType(DbType.SQL_SERVER);
            dsc.setDriverName(DataSourceInfo.sqlserverDriver);
            dsc.setUrl(DataSourceInfo.sqlserverUrl
                    .replace("{host}",linkEntity.getHost())
                    .replace("{port}",linkEntity.getPort())
                    .replace("{dbName}",linkEntity.getServiceName()));
        }
        dsc.setUsername(linkEntity.getUserName());
        dsc.setPassword(linkEntity.getPassword());
        return dsc;
    }
}
