package smart.base.util;

import smart.exception.DataException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Oracle数据建模使用
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-04-08
 */
@Slf4j
public class OracleUtil {
    /**
     * 自定义sql语句(适合增、删、改)  Oracle数据建模专用
     * 输入的语句需要全小写，除了数据
     */
    public static int oracleCustom(Connection conn, String sql) throws DataException {
        int result = 0;
        try {
            String dbType = conn.getMetaData().getDatabaseProductName().trim().toLowerCase();
            if ("oracle".equals(dbType)) {
                //关闭事务自动提交
                conn.setAutoCommit(false);
                String[] sqls = sql.split(";");
                @Cleanup PreparedStatement preparedStatement = null;
                for (String sqlOne : sqls) {
                    if (sqlOne.toLowerCase().contains("insert") && sqlOne.replaceAll(" ","").contains("),(")) {
                        String[] splitSql = sqlOne.split("\\),\\(");
                        //centerSql取出INTO TEST_DETAILS ( F_ID, F_RECEIVABLEID)
                        String centerSql = splitSql[0].split("VALUES")[0].split("INSERT")[1];
                        //for循环尾部
                        String lastSql=splitSql[splitSql.length-1];
                        splitSql[splitSql.length-1]=lastSql.substring(0,lastSql.length()-1);
                        for (int i = 0; i < splitSql.length; i++) {
                            //第一个语句INSERT INTO TEST_DETAILS ( F_ID, F_RECEIVABLEID) VALUES ( '71', '28bf3436e5d1'
                            //需要拼接成 INSERT INTO TEST_DETAILS ( F_ID, F_RECEIVABLEID) VALUES ( '71', '28bf3436e5d1'）
                            if(i==0){
                                System.out.println(splitSql[i]+")");
                                preparedStatement=conn.prepareStatement(splitSql[i]+")");
                                preparedStatement.execute();
                                preparedStatement.close();
                            }else{
                                preparedStatement=conn.prepareStatement("INSERT "+centerSql+"VALUES ("+splitSql[i]+")");
                                System.out.println("INSERT "+centerSql+"VALUES ("+splitSql[i]+")");
                                preparedStatement.execute();
                                preparedStatement.close();
                            }
                        }
                        result++;
                    } else {
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
            log.error(e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException q) {
                log.error("数据错误:" + q.getMessage());
            }
            throw new DataException("操作失败");
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

}

