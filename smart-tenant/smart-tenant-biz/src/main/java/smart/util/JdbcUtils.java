package smart.util;


import smart.model.DbTableModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
public class JdbcUtils {
    /**
     * 获取表
     * @param conn
     * @param dnname
     * @return
     * @throws Exception
     */
    public static List<DbTableModel> mysqlgetList(Connection conn, String dnname) throws Exception {
        List<DbTableModel> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT table_name F_TABLE,table_rows F_SUM, data_length F_SIZE, table_comment F_TABLENAME, ");
        sql.append("CONCAT(table_name,'(',table_comment,')') as 'F_DESCRIPTION' FROM information_schema.TABLES WHERE ");
        sql.append("TABLE_SCHEMA = '" + dnname + "'");
        ResultSet result = JdbcUtil.query(conn, sql.toString());
        try {
            while (result.next()) {
                DbTableModel model = new DbTableModel();
                model.setTable(result.getString("F_TABLE"));
                model.setTableName(result.getString("F_TABLENAME"));
                model.setSum(result.getInt("F_SUM"));
                model.setDescription(result.getString("F_TABLE") + "(" + result.getString("F_TABLENAME") + ")");
                model.setPrimaryKey("F_Id");
                list.add(model);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    public static List<DbTableModel> sqlServergetList(Connection conn, String dnname) throws Exception {
        List<DbTableModel> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SET NOCOUNT ON DECLARE @TABLEINFO TABLE ( NAME VARCHAR(50) , SUMROWS VARCHAR(11) , RESERVED VARCHAR(50) , DATA VARCHAR(50) , INDEX_SIZE VARCHAR(50) , UNUSED VARCHAR(50) , PK VARCHAR(50) ) DECLARE @TABLENAME TABLE ( NAME VARCHAR(50) ) DECLARE @NAME VARCHAR(50) DECLARE @PK VARCHAR(50) INSERT INTO @TABLENAME ( NAME ) SELECT O.NAME FROM SYSOBJECTS O , SYSINDEXES I WHERE O.ID = I.ID AND O.XTYPE = 'U' AND I.INDID < 2 ORDER BY I.ROWS DESC , O.NAME WHILE EXISTS ( SELECT 1 FROM @TABLENAME ) BEGIN SELECT TOP 1 @NAME = NAME FROM @TABLENAME DELETE @TABLENAME WHERE NAME = @NAME DECLARE @OBJECTID INT SET @OBJECTID = OBJECT_ID(@NAME) SELECT @PK = COL_NAME(@OBJECTID, COLID) FROM SYSOBJECTS AS O INNER JOIN SYSINDEXES AS I ON I.NAME = O.NAME INNER JOIN SYSINDEXKEYS AS K ON K.INDID = I.INDID WHERE O.XTYPE = 'PK' AND PARENT_OBJ = @OBJECTID AND K.ID = @OBJECTID INSERT INTO @TABLEINFO ( NAME , SUMROWS , RESERVED , DATA , INDEX_SIZE , UNUSED ) EXEC SYS.SP_SPACEUSED @NAME UPDATE @TABLEINFO SET PK = @PK WHERE NAME = @NAME END SELECT cast(F.NAME AS varchar(50)) F_TABLE,cast(ISNULL( P.TDESCRIPTION, F.NAME )  AS varchar(50)) F_TABLENAME,cast(F.RESERVED AS varchar(50)) F_SIZE,cast(RTRIM( F.SUMROWS ) AS varchar(50)) F_SUM,cast(F.PK AS varchar(50)) F_PRIMARYKEY FROM @TABLEINFO F LEFT JOIN ( SELECT NAME = CASE WHEN A.COLORDER = 1 THEN D.NAME ELSE '' END , TDESCRIPTION = CASE WHEN A.COLORDER = 1 THEN ISNULL(F.VALUE, '') ELSE '' END FROM SYSCOLUMNS A LEFT JOIN SYSTYPES B ON A.XUSERTYPE = B.XUSERTYPE INNER JOIN SYSOBJECTS D ON A.ID = D.ID AND D.XTYPE = 'U' AND D.NAME <> 'DTPROPERTIES' LEFT JOIN SYS.EXTENDED_PROPERTIES F ON D.ID = F.MAJOR_ID WHERE A.COLORDER = 1 AND F.MINOR_ID = 0 ) P ON F.NAME = P.NAME WHERE 1 = 1 ORDER BY F_TABLE");
        ResultSet result = JdbcUtil.query(conn, sql.toString());
        try {
            while (result.next()) {
                if (!"Base_Tenant".equals(result.getString("F_TABLE")) && !"Base_TenantLog".equals(result.getString("F_TABLE"))) {
                    DbTableModel model = new DbTableModel();
                    model.setTable(result.getString("F_TABLE"));
                    model.setTableName(result.getString("F_TABLENAME"));
                    model.setDescription(result.getString("F_TABLE") + "(" + result.getString("F_TABLENAME") + ")");
                    model.setSize(result.getString("F_SIZE"));
                    model.setSum(result.getInt("F_SUM"));
                    model.setPrimaryKey(result.getString("F_PRIMARYKEY"));
                    list.add(model);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

}
