package smart.base.service;

import smart.base.ActionResult;
import smart.base.model.dbtable.DbTableDataForm;
import smart.base.model.dbtable.DbTableFieldModel;
import smart.base.model.dbtable.DbTableModel;
import smart.exception.DataException;

import java.util.List;
import java.util.Map;

/**
 * 数据管理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface DbTableService {

    /**
     * 表列表
     * @param dbId  连接Id
     * @return
     * @throws DataException
     */
    List<DbTableModel> getList(String dbId) throws DataException;

    /**
     * 表字段
     * @param dbId  连接Id
     * @param table 表名
     * @return
     * @throws DataException
     */
    List<DbTableFieldModel> getFieldList(String dbId, String table) throws DataException;

    /**
     * 表数据
     *
     * @param dbTableDataForm 分页
     * @param dbId    连接Id
     * @param table     表名
     * @return
     */
    List<Map<String, Object>> getData(DbTableDataForm dbTableDataForm, String dbId, String table);

    /**
     * 验证名称
     *
     * @param dbId 连接Id
     * @param table  表名
     * @param oldTable     主键值
     * @return
     */
    boolean isExistByFullName(String dbId, String table, String oldTable) throws DataException;

    /**
     * 获取时间
     * @param dbId  连接Id
     * @return
     * @throws DataException
     */
    String getDbTime(String dbId) throws DataException;

    /**
     * 执行sql
     * @param dbId      连接Id
     * @param strSql    sql语句
     * @return
     * @throws DataException
     */
    int executeSql(String dbId, String strSql) throws DataException;

    /**
     * 删除表
     * @param dbId  连接Id
     * @param table 表名
     * @throws DataException
     */
    void delete(String dbId, String table) throws DataException;

    /**
     * 创建表
     * @param dbId                  连接Id
     * @param dbTableModel          表对象
     * @param dbTableFieldModels    字段对象
     * @return
     * @throws DataException
     */
    ActionResult create(String dbId, DbTableModel dbTableModel, List<DbTableFieldModel> dbTableFieldModels) throws DataException;

    /**
     * 修改表
     * @param dbId                  连接Id
     * @param dbTableModel          表对象
     * @param dbTableFieldModels    字段对象
     * @throws DataException
     */
    void update(String dbId, DbTableModel dbTableModel, List<DbTableFieldModel> dbTableFieldModels) throws DataException;
}
