package smart.base.service;

/**
 * 数据同步
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface DbSyncService {

    /**
     * 批量写入
     *
     * @param dbConnectionFrom 数据库连接From
     * @param dbConnectionTo   数据库连接To
     * @param table            表
     * @return
     */
    String importTableData(String dbConnectionFrom, String dbConnectionTo, String table);
}
