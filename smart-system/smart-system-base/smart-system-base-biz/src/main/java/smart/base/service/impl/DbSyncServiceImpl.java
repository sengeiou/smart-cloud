package smart.base.service.impl;

import smart.base.service.DbSyncService;
import smart.base.service.DblinkService;
import smart.util.JdbcUtil;
import smart.util.StringUtil;
import smart.base.entity.DbLinkEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * 数据同步
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class DbSyncServiceImpl implements DbSyncService {

    @Autowired
    private DblinkService dblinkService;

    @Override
    public String importTableData(String dbConnectionFrom, String dbConnectionTo, String table) {
        try {
            DbLinkEntity linkFrom = dblinkService.getInfo(dbConnectionFrom);
            DbLinkEntity linkTo = dblinkService.getInfo(dbConnectionTo);
            Connection connectionStringFrom = JdbcUtil.getConn(linkFrom.getDbType(), linkFrom.getUserName(), linkFrom.getPassword(), linkFrom.getHost(), linkFrom.getPort(), linkFrom.getServiceName());
            Connection connectionStringTo = JdbcUtil.getConn(linkTo.getDbType(), linkTo.getUserName(), linkTo.getPassword(), linkTo.getHost(), linkTo.getPort(), linkTo.getServiceName());
            if(connectionStringFrom==null){
                return "数据库"+linkFrom.getFullName()+"连接失败";
            }
            if(connectionStringTo==null){
                return "数据库"+linkTo.getFullName()+"连接失败";
            }
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM " + table);
            ResultSet result = JdbcUtil.query(connectionStringFrom, sql.toString());
            System.out.println(sql.toString());
            //获得结果集结构信息,元数据
            ResultSetMetaData md = result.getMetaData();
            //获得列数
            int columnCount = md.getColumnCount();
            StringBuilder insert = new StringBuilder();
            StringBuilder value = new StringBuilder("( ");
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object object = result.getObject(i) == null ? null : result.getObject(i);
                        if (i != columnCount) {
                            if(object!=null) {
                                value.append("'" + result.getObject(i) + "', ");
                            }else {
                                value.append(result.getObject(i) + ", ");
                            }
                        } else {
                            if(object!=null) {
                                value.append("'" + result.getObject(i) + "' ),(");
                            }else {
                                value.append(result.getObject(i) + " ),(");
                            }
                        }
                    }
                }
            value.deleteCharAt(value.length() - 1);
            value.deleteCharAt(value.length() - 1);
            if (StringUtil.isEmpty(value) || StringUtil.isBlank(value)){
                return "ok";
            }
            insert.append("INSERT INTO " + table + " VALUES " + value);
            JdbcUtil.custom(connectionStringTo, insert.toString());
            return "ok";
        } catch (Exception e) {
            return(e.getMessage());
        }
    }
}
