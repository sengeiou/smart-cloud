package smart.config;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.seata.rm.datasource.DataSourceProxy;
import smart.exception.DataException;
import smart.model.DbTableConModel;
import smart.util.data.DataSourceContextHolder;
import smart.util.DataSourceUtil;
import smart.util.DbConUtil;
import smart.util.JdbcUtil;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Configuration
@MapperScan(basePackages = {"smart.mapper","smart.*.mapper"})
public class MybatisPlusConfig {

    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 对接数据库的实体层
     */
    private static final String ALIASES_PACKAGE = "smart.entity";

    @Primary
    @Bean(name = "dataSourceSystem")
    public DataSourceProxy dataSourceOne() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(dataSourceUtil.getUrl().replace("{dbName}",dataSourceUtil.getDbName()));
        druidDataSource.setUsername(dataSourceUtil.getUserName());
        druidDataSource.setPassword(dataSourceUtil.getPassword());
        druidDataSource.setDriverClassName(dataSourceUtil.getDriverClassName());
        return new DataSourceProxy(druidDataSource);
    }

    @Bean(name = "sqlSessionFactorySystem")
    public SqlSessionFactory sqlSessionFactoryOne(@Qualifier("dataSourceSystem") DataSourceProxy  dataSourceProxy ) throws Exception {
        return createSqlSessionFactory(dataSourceProxy);
    }

    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(new MybatisPlusMetaObjectHandler());
        //配置填充器
        bean.setGlobalConfig(globalConfig);

        bean.setVfs(SpringBootVFS.class);
        bean.setTypeAliasesPackage(ALIASES_PACKAGE);
        bean.setMapperLocations(resolveMapperLocations());
        bean.setConfiguration(configuration());
        return bean.getObject();
    }

    private MybatisConfiguration configuration() throws DataException {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(false);
        mybatisConfiguration.setCacheEnabled(false);
        mybatisConfiguration.addInterceptor(mybatisPlusInterceptor());
        mybatisConfiguration.setLogImpl(Slf4jImpl.class);
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);
        return mybatisConfiguration;
    }

    private Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:mapper/*.xml");
        mapperLocations.add("classpath*:mapper/*/*.xml");
        List<Resource> resources = new ArrayList();
        if (mapperLocations != null) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() throws DataException {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //判断是否多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {

            DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
            HashMap<String, TableNameHandler> map = new HashMap<>(150) ;
            String url=dataSourceUtil.getUrl().replace("{dbName}", dataSourceUtil.getDbInit());
            Connection conn = JdbcUtil.getConn(dataSourceUtil.getUserName(), dataSourceUtil.getPassword(), url);
            List<DbTableConModel> dbTableModels=new ArrayList<>();
            if (conn != null) {
                if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    dbTableModels = DbConUtil.mysqlgetList(conn, dataSourceUtil.getDbInit());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    dbTableModels = DbConUtil.sqlServergetList(conn, dataSourceUtil.getDbInit());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    dbTableModels = DbConUtil.orcalgetList(conn, dataSourceUtil.getDbInit());
                }
            }
            for(DbTableConModel dbTableModel:dbTableModels){
                if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    map.put(dbTableModel.getTable(), (sql, tableName) -> DataSourceContextHolder.getDatasourceName()+"."+dbTableModel.getTable());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    map.put(dbTableModel.getTable(), (sql, tableName) -> DataSourceContextHolder.getDatasourceName()+".dbo."+dbTableModel.getTable());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    map.put(dbTableModel.getTable().toLowerCase(), (sql, tableName) -> DataSourceContextHolder.getDatasourceName().toUpperCase()+"."+dbTableModel.getTable());
                }
            }
            dynamicTableNameInnerInterceptor.setTableNameHandlerMap(map);


            /**
             *3.5.0新写法
             */
            /*DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
            String url=dataSourceUtil.getUrl().replace("{dbName}", dataSourceUtil.getDbInit());
            Connection conn = JdbcUtil.getConn(dataSourceUtil.getUserName(), dataSourceUtil.getPassword(), url);
            List<DbTableConModel> dbTableModels=new ArrayList<>();
            if (conn != null) {
                if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    dbTableModels = DbConUtil.mysqlgetList(conn, dataSourceUtil.getDbInit());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    dbTableModels = DbConUtil.sqlServergetList(conn, dataSourceUtil.getDbInit());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    dbTableModels = DbConUtil.orcalgetList(conn, dataSourceUtil.getDbInit());
                }
            }
            for(DbTableConModel dbTableModel:dbTableModels){
                if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> DataSourceContextHolder.getDatasourceName()+"."+dbTableModel.getTable());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> DataSourceContextHolder.getDatasourceName()+".dbo."+dbTableModel.getTable());
                } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> DataSourceContextHolder.getDatasourceName().toUpperCase()+"."+dbTableModel.getTable());
                }
            }*/



            interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        }
        //新版本分页必须指定数据库，否则分页不生效
        if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.MYSQL.getDb())) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.SQL_SERVER));
        } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.ORACLE.getDb())) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE));
        }
        return interceptor;
    }


}
