package smart.base.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import smart.base.ActionResult;
import smart.base.model.dbtable.DbTableDataForm;
import smart.base.model.dbtable.DbTableFieldModel;
import smart.base.model.dbtable.DbTableModel;
import smart.base.service.DbTableService;
import smart.base.service.DblinkService;
import smart.util.JdbcUtil;
import smart.util.PageUtil;
import smart.util.StringUtil;
import smart.base.entity.DbLinkEntity;
import smart.exception.DataException;
import smart.config.ConfigValueUtil;
import smart.util.DataSourceUtil;
import smart.base.util.DbUtil;
import smart.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据管理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class DbTableServiceImpl implements DbTableService {

    @Autowired
    private DblinkService dblinkService;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Override
    public List<DbTableModel> getList(String dbId) throws DataException {
        DbLinkEntity link = dblinkService.getInfo(dbId);
        List<DbTableModel> list = new ArrayList<>();
        String tenSource = "";
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenSource = userProvider.get().getTenantDbConnectionString();
        }
        if (link != null) {
            String dnname = link.getServiceName();
            Connection conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                if (link.getDbType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    list = DbUtil.mysqlgetList(conn, dnname);
                } else if (link.getDbType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    list = DbUtil.sqlServergetList(conn, dnname);
                } else if (link.getDbType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    list = DbUtil.orcalgetList(conn, dnname);
                }
            }
        } else {
            String dbName = dataSourceUtils.getDbName();
            String urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
            if (StringUtils.isNotEmpty(tenSource)) {
                urll = dataSourceUtils.getUrl().replace("{dbName}", tenSource);
                dbName = tenSource;
            }
            Connection conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), urll);
            if (conn != null) {
                if (dataSourceUtils.getUrl().contains(smart.emnus.DbType.MYSQL.getMessage())) {
                    list = DbUtil.mysqlgetList(conn, dbName);
                } else if (dataSourceUtils.getUrl().contains(smart.emnus.DbType.SQLSERVER.getMessage())) {
                    list = DbUtil.sqlServergetList(conn, dbName);
                } else if (dataSourceUtils.getUrl().contains(smart.emnus.DbType.ORACLE.getMessage())) {
                    list = DbUtil.orcalgetList(conn, dbName);
                }
            }
        }
        return list;
    }

    @Override
    public List<DbTableFieldModel> getFieldList(String dbId, String table) throws DataException {
        DbLinkEntity link = dblinkService.getInfo(dbId);
        List<DbTableFieldModel> list = new ArrayList<>();
        String tenSource = "";
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenSource = userProvider.get().getTenantDbConnectionString();
        }
        if (link != null) {
            Connection conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                if (link.getDbType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    list = DbUtil.getMysqlFieldList(conn, link.getServiceName(), table);
                } else if (link.getDbType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    list = DbUtil.getSqlserverFieldList(conn, table);
                } else if (link.getDbType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    list = DbUtil.getorcalFieldList(conn, table);
                }
            }
        } else {
            String urll = dataSourceUtils.getUrl().replace("{dbName}", tenSource);
            if (StringUtils.isEmpty(tenSource)) {
                String dbName = dataSourceUtils.getDbName();
                urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
                tenSource = dataSourceUtils.getDbName();
            }
            Connection conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), urll);
            if (conn != null) {
                if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.MYSQL.getDb())) {
                    list = DbUtil.getMysqlFieldList(conn, tenSource, table);
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.SQL_SERVER.getDb())) {
                    list = DbUtil.getSqlserverFieldList(conn, table);
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.ORACLE.getDb())) {
                    list = DbUtil.getorcalFieldList(conn, table);
                }
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getData(DbTableDataForm dbTableDataForm, String dbId, String table) {
        String dbName = "";
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            dbName = userProvider.get().getTenantDbConnectionString();
        }
        DbLinkEntity link = dblinkService.getInfo(dbId);
        List<Map<String, Object>> list = new ArrayList();
        if (link != null) {
            Connection conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                if (link.getDbType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    list = DbUtil.mysqlGetData(conn, table, dbTableDataForm);
                } else if (link.getDbType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    list = DbUtil.sqlserverGetData(conn, table, dbTableDataForm);
                } else if (link.getDbType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    list = DbUtil.orcalGetData(conn, table, dbTableDataForm);
                }
            }
        } else {
            String urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
            if (StringUtils.isEmpty(dbName)) {
                dbName = dataSourceUtils.getDbName();
                urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
            }
            Connection conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), urll);
            List<Map<String, Object>> GetList;
            StringBuilder sql = new StringBuilder();
            if (conn != null) {
                if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.MYSQL.getDb())) {
                    sql.append("select * from " + dbName + "." + table);
                    if (!StringUtil.isEmpty(dbTableDataForm.getKeyword()) && !StringUtil.isEmpty(dbTableDataForm.getField())) {
                        sql.append(" where " + dbTableDataForm.getField() + " like '%" + dbTableDataForm.getKeyword() + "%'");
                    }
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.SQL_SERVER.getDb())) {
                    sql.append("select * from " + dbName + ".dbo." + table);
                    if (!StringUtil.isEmpty(dbTableDataForm.getKeyword()) && !StringUtil.isEmpty(dbTableDataForm.getField())) {
                        sql.append(" where " + dbTableDataForm.getField() + " like '%" + dbTableDataForm.getKeyword() + "%'");
                    }
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.ORACLE.getDb())) {
                    sql.append("select * from " + dataSourceUtils.getUserName().split(" ")[0] + "." + table);
                    if (!StringUtil.isEmpty(dbTableDataForm.getKeyword()) && !StringUtil.isEmpty(dbTableDataForm.getField())) {
                        sql.append(" where " + dbTableDataForm.getField() + " like '%" + dbTableDataForm.getKeyword() + "%'");
                    }
                }
                ResultSet query = null;
                try {
                    query = JdbcUtil.query(conn, sql.toString());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                GetList = JdbcUtil.convertListString(query);
                for (Map<String, Object> mapList : GetList) {
                    Map<String, Object> objectMap = new HashMap<>();
                    for (String key : mapList.keySet()) {
                        objectMap.put(key.toLowerCase(), mapList.get(key));
                    }
                    list.add(objectMap);
                }
            }
        }
        return dbTableDataForm.setData(PageUtil.getListPage((int) dbTableDataForm.getCurrentPage(), (int) dbTableDataForm.getPageSize(), list), list.size());
    }

    @Override
    public boolean isExistByFullName(String dbId, String table, String oldTable) throws DataException {
        List<DbTableModel> data = this.getList(dbId).stream().filter(m -> m.getTable().equals(table)).collect(Collectors.toList());
        if (!StringUtils.isEmpty(oldTable)) {
            data = data.stream().filter(m -> !m.getTable().equals(oldTable)).collect(Collectors.toList());
        }
        return data.size() > 0;
    }

    @Override
    public String getDbTime(String dbId) throws DataException {
        DbLinkEntity link = dblinkService.getInfo(dbId);
        String time = "";
        Connection conn = null;
        if (link != null) {
            conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                if (link.getDbType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    StringBuilder sql = new StringBuilder();
                    sql.append("SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') as TIME");
                    ResultSet result = JdbcUtil.query(conn, sql.toString());
                    try {
                        while (result.next()) {
                            time = result.getString("TIME");
                        }
                        return time;
                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else if (link.getDbType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    StringBuilder sql = new StringBuilder();
                    sql.append("Select CONVERT(varchar(100), GETDATE(), 120) as TIME");
                    ResultSet result = JdbcUtil.query(conn, sql.toString());
                    try {
                        while (result.next()) {
                            time = result.getString("TIME");
                        }
                        return time;
                    } catch (Exception e) {
                        e.getMessage();
                    }
                } else if (link.getDbType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    StringBuilder sql = new StringBuilder();
                    sql.append("select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') as TIME from dual");
                    ResultSet result = JdbcUtil.query(conn, sql.toString());
                    try {
                        while (result.next()) {
                            time = result.getString("TIME");
                        }
                        return time;
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        }
        return time;
    }

    @Override
    public int executeSql(String dbId, String strSql) throws DataException {
        DbLinkEntity link = dblinkService.getInfo(dbId);
        int result = 0;
        if (link != null) {
            Connection conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                result = JdbcUtil.custom(conn, strSql);
            }
        }
        return result;
    }

    @Override
    public void delete(String dbId, String table) throws DataException {
        String tenSource = "";
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenSource = userProvider.get().getTenantDbConnectionString();
        }
        DbLinkEntity link = dblinkService.getInfo(dbId);
        if (link != null) {
            Connection conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                StringBuilder sql = new StringBuilder();
                sql.append("drop table " + table);
                JdbcUtil.custom(conn, sql.toString());
            }
        } else {
            String urll = dataSourceUtils.getUrl().replace("{dbName}", tenSource);
            if (StringUtils.isEmpty(tenSource)) {
                String dbName = dataSourceUtils.getDbName();
                urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
            }
            Connection conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), urll);
            if (conn != null) {
                StringBuilder sql = new StringBuilder();
                sql.append("drop table " + table);
                JdbcUtil.custom(conn, sql.toString());
            }
        }
    }

    @Override
    public ActionResult create(String dbId, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList) throws DataException {
        DbLinkEntity link = dblinkService.getInfo(dbId);
        String tenSource = "";
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenSource = userProvider.get().getTenantDbConnectionString();
        }
        if (link != null) {
            Connection conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                if (link.getDbType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    DbUtil.createMySqlTable(conn, dbTableModel, tableFieldList);
                } else if (link.getDbType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    DbUtil.createSqlserverTable(conn, dbTableModel, tableFieldList);
                } else if (link.getDbType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    DbUtil.createOrcalTable(conn, dbTableModel, tableFieldList,dataSourceUtils.getTableSpace());
                }

            } else {
                return ActionResult.fail("连接失败");
            }
        } else {
            String urll = dataSourceUtils.getUrl().replace("{dbName}", tenSource);
            if (StringUtils.isEmpty(tenSource)) {
                String dbName = dataSourceUtils.getDbName();
                urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
            }
            Connection conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), urll);
            if (conn != null) {
                if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.MYSQL.getDb())) {
                    DbUtil.createMySqlTable(conn, dbTableModel, tableFieldList);
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.SQL_SERVER.getDb())) {
                    DbUtil.createSqlserverTable(conn, dbTableModel, tableFieldList);
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.ORACLE.getDb())) {
                    DbUtil.createOrcalTable(conn, dbTableModel, tableFieldList, dataSourceUtils.getTableSpace());
                }
            } else {
                return ActionResult.fail("连接失败");
            }
        }
        return ActionResult.success("新建成功");
    }

    @Override
    public void update(String dbId, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList) throws DataException {
        String tenSource = "";
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenSource = userProvider.get().getTenantDbConnectionString();
        }
        DbLinkEntity link = dblinkService.getInfo(dbId);
        if (link != null) {
            Connection conn = JdbcUtil.getConn(link.getDbType().toLowerCase(), link.getUserName(), link.getPassword(), link.getHost(), link.getPort(), link.getServiceName());
            if (conn != null) {
                if (link.getDbType().toLowerCase().equals(DbType.MYSQL.getDb())) {
                    DbUtil.updateMySqlTable(conn, dbTableModel, tableFieldList);
                } else if (link.getDbType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
                    DbUtil.updateSqlserverTable(conn, dbTableModel, tableFieldList);
                } else if (link.getDbType().toLowerCase().equals(DbType.ORACLE.getDb())) {
                    DbUtil.updateOrcalTable(conn, dbTableModel, tableFieldList,dataSourceUtils.getTableSpace());
                }
            }
        } else {
            String urll = dataSourceUtils.getUrl().replace("{dbName}", tenSource);
            if (StringUtils.isEmpty(tenSource)) {
                String dbName = dataSourceUtils.getDbName();
                urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
            }
            Connection conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), urll);
            if (conn != null) {
                if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.MYSQL.getDb())) {
                    DbUtil.updateMySqlTable(conn, dbTableModel, tableFieldList);
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.SQL_SERVER.getDb())) {
                    DbUtil.updateSqlserverTable(conn, dbTableModel, tableFieldList);
                } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.ORACLE.getDb())) {
                    DbUtil.updateOrcalTable(conn, dbTableModel, tableFieldList,dataSourceUtils.getTableSpace());
                }
            }
        }
    }

}
