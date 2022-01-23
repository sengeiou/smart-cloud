package smart.base.util;

import smart.util.JdbcUtil;
import smart.util.RandomUtil;
import smart.base.model.dbtable.DbTableDataForm;
import smart.base.model.dbtable.DbTableFieldModel;
import smart.base.model.dbtable.DbTableModel;
import smart.exception.DataException;
import smart.util.FileUtil;
import smart.util.StringUtil;
import smart.util.DataSourceUtil;
import smart.util.context.SpringContext;
import smart.util.dbcolum.ColumnType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * 数据库操作工具类
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
public class DbUtil {

    /**
     * 获取表
     * @param conn
     * @param dnname
     * @return
     * @throws DataException
     */
    public static List<DbTableModel> mysqlgetList(Connection conn, String dnname) throws DataException {
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
                model.setSize(FileUtil.getSize(result.getString("F_SIZE")));
                model.setSum(result.getInt("F_SUM"));
                model.setDescription(result.getString("F_TABLE") + "(" + result.getString("F_TABLENAME") + ")");
                list.add(model);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    public static List<DbTableModel> sqlServergetList(Connection conn, String dnname) throws DataException {
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
                    list.add(model);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    public static List<DbTableModel> orcalgetList(Connection conn, String dnname) throws DataException {
        List<DbTableModel> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        DataSourceUtil dataSourceUtil = SpringContext.getBean(DataSourceUtil.class);
        sql.append("SELECT " +
                "a.TABLE_NAME F_TABLE, " +
                "b.COMMENTS F_TABLENAME, " +
                "a.num_rows F_SUM " +
                "FROM " +
                "user_tables a, " +
                "user_tab_comments b " +
                "WHERE " +
                "a.TABLE_NAME = b.TABLE_NAME " +
                "and a.TABLESPACE_NAME='"+dataSourceUtil.getTableSpace()+"'");
        ResultSet result = JdbcUtil.query(conn, sql.toString());
        try {
            while (result.next()) {
                DbTableModel model = new DbTableModel();
                model.setTable(result.getString("F_TABLE"));
                model.setTableName(result.getString("F_TABLENAME"));
                model.setSum(result.getInt("F_SUM"));
                model.setDescription(result.getString("F_TABLE") + "(" + result.getString("F_TABLENAME") + ")");
                list.add(model);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    //=================================================================================================

    /**
     * 获取表字段信息
     * @param conn
     * @param dbname
     * @param table
     * @return
     * @throws DataException
     */
    public static List<DbTableFieldModel> getMysqlFieldList(Connection conn, String dbname, String table) throws DataException {
        List<DbTableFieldModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COLUMN_NAME F_FIELD,data_type F_DATATYPE,CHARACTER_MAXIMUM_LENGTH F_DATALENGTH, NUMERIC_PRECISION 精度,NUMERIC_SCALE 小数位数, ");
        sql.append("IF ( IS_NULLABLE = 'YES', '1', '0' ) F_ALLOWNULL, COLUMN_COMMENT F_FIELDNAME,");
        sql.append("IF ( COLUMN_KEY = 'PRI', '1', '0' ) F_PRIMARYKEY, ");
        sql.append("column_default F_DEFAULTS,");
        sql.append("CONCAT(upper(COLUMN_NAME),'(',COLUMN_COMMENT,')') as 'F_DESCRIPTION' ");
        sql.append("FROM INFORMATION_SCHEMA.COLUMNS ");
        sql.append("WHERE TABLE_NAME = '" + table + "'AND TABLE_SCHEMA='" + dbname + "'");
        ResultSet result = JdbcUtil.query(conn, sql.toString());
        try {
            while (result.next()) {
                DbTableFieldModel model = new DbTableFieldModel();
                model.setField(result.getString("F_FIELD"));
                model.setDescription(result.getString("F_DESCRIPTION"));
                model.setDataType(result.getString("F_DATATYPE"));
                if (!StringUtil.isEmpty(result.getString("F_DATALENGTH"))) {
                    model.setDataLength(result.getString("F_DATALENGTH"));
                } else if (!StringUtil.isEmpty(result.getString("精度"))) {
                    model.setDataLength(result.getString("精度") + "," + result.getString("小数位数"));
                }
                model.setAllowNull(result.getInt("F_ALLOWNULL"));
                model.setFieldName(result.getString("F_FIELDNAME"));
                model.setDefaults(result.getString("F_DEFAULTS"));
                model.setPrimaryKey(result.getInt("F_PRIMARYKEY"));
                if (!list.contains(model)) {
                    list.add(model);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    public static List<DbTableFieldModel> getSqlserverFieldList(Connection conn, String table) throws DataException {
        List<DbTableFieldModel> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT cast(a.name as varchar(50)) F_FIELD ," +
                " cast(case when exists(SELECT 1 FROM sysobjects where xtype='PK' and name in ( " +
                " SELECT name FROM sysindexes WHERE indid in( " +
                " SELECT indid FROM sysindexkeys WHERE id = a.id AND colid=a.colid ))) " +
                " then '1' else '0' end as varchar(50)) F_PRIMARYKEY, " +
                " cast(b.name as varchar(50)) F_DATATYPE, " +
                " cast(COLUMNPROPERTY(a.id,a.name,'PRECISION') as varchar(50)) F_DATALENGTH, " +
                " cast(case when a.isnullable=0 then '0'else '1' end as varchar(50)) F_ALLOWNULL, " +
                " cast(isnull(e.text,'') as varchar(50)) F_DEFAULTS, " +
                " cast(isnull(g.[value],'') as varchar(50)) F_FIELDNAME " +
                "FROM syscolumns a " +
                "left join systypes b on a.xusertype=b.xusertype " +
                "inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties' " +
                "left join syscomments e on a.cdefault=e.id " +
                "left join sys.extended_properties g on a.id=g.major_id and a.colid=g.minor_id " +
                "left join sys.extended_properties f on d.id=f.major_id and f.minor_id=0 " +
                "where d.name='" + table + "'" +
                "order by a.id,a.colorder");
        ResultSet result = JdbcUtil.query(conn, sql.toString());
        try {
            while (result.next()) {
                DbTableFieldModel model = new DbTableFieldModel();
                model.setField(result.getString("F_FIELD"));
                model.setDescription(result.getString("F_FIELD") + "(" + result.getString("F_FIELDNAME") + ")");
                model.setDataType(result.getString("F_DATATYPE"));
                model.setDataLength(result.getString("F_DATALENGTH"));
                model.setAllowNull(result.getInt("F_ALLOWNULL"));
                model.setFieldName(result.getString("F_FIELDNAME"));
                model.setDefaults(result.getString("F_DEFAULTS"));
                model.setPrimaryKey(result.getInt("F_PRIMARYKEY"));
                if (!list.contains(model)) {
                    list.add(model);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    public static List<DbTableFieldModel> getorcalFieldList(Connection conn, String table) throws DataException {
        List<DbTableFieldModel> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT " +
                "A.column_name AS F_FIELD, " +
                "A.data_type AS F_DATATYPE, " +
                "A.CHAR_COL_DECL_LENGTH AS F_DATALENGTH, " +
                "CASE " +
                "A.nullable  " +
                "WHEN 'N' THEN " +
                "'0' ELSE '1'  " +
                "END AS F_ALLOWNULL, " +
                "CASE " +
                "A.nullable  " +
                "WHEN 'N' THEN " +
                "'1' ELSE '0'  " +
                "END AS F_PRIMARYKEY, " +
                "B.comments AS F_FIELDNAME  " +
                "FROM " +
                "user_tab_columns A, " +
                "user_col_comments B, " +
                "all_cons_columns C, " +
                "USER_TAB_COMMENTS D  " +
                "WHERE " +
                "a.COLUMN_NAME = b.column_name  " +
                "AND A.Table_Name = B.Table_Name  " +
                "AND A.Table_Name = D.Table_Name  " +
                "AND ( A.TABLE_NAME = c.table_name )  " +
                "AND A.Table_Name = '" + table + "'");
        ResultSet result = JdbcUtil.query(conn, sql.toString());
        try {
            while (result.next()) {
                DbTableFieldModel model = new DbTableFieldModel();
                model.setField(result.getString("F_FIELD"));
                model.setDescription(result.getString("F_FIELD") + "(" + result.getString("F_FIELDNAME") + ")");
                switch (result.getString("F_DATATYPE")){
                    case ColumnType.ORACLE_NVARCHAR:
                        model.setDataType("varchar");
                        break;
                    case ColumnType.ORACLE_DATE:
                        model.setDataType("datetime");
                        break;
                    case ColumnType.ORACLE_DECIMAL:
                        model.setDataType("decimal");
                        break;
                    case ColumnType.ORACLE_CLOB:
                        model.setDataType("text");
                        break;
                    default:
                        model.setDataType("int");
                }
                model.setDataLength(result.getString("F_DATALENGTH"));
                model.setAllowNull(result.getInt("F_ALLOWNULL"));
                model.setFieldName(result.getString("F_FIELDNAME"));
                model.setPrimaryKey(result.getInt("F_PRIMARYKEY"));
                if (!list.contains(model)) {
                    list.add(model);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    //=================================================================================================

    /**
     * 获取表数据
     * @param conn
     * @param table
     * @param dbTableDataForm
     * @return
     */
    public static List<Map<String, Object>> mysqlGetData(Connection conn, String table, DbTableDataForm dbTableDataForm) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from " + table);
            if (!StringUtil.isEmpty(dbTableDataForm.getKeyword()) && !StringUtil.isEmpty(dbTableDataForm.getField())) {
                sql.append(" where " + dbTableDataForm.getField() + " like '%" + dbTableDataForm.getKeyword() + "%'");
            }
            ResultSet result = JdbcUtil.query(conn, sql.toString());
            ResultSetMetaData md = result.getMetaData();
            int columnCount = md.getColumnCount();
            while (result.next()) {
                Map<String, Object> rowData = new HashMap();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i).toLowerCase(), result.getObject(i));
                }
                list.add(rowData);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    public static List<Map<String, Object>> sqlserverGetData(Connection conn, String table, DbTableDataForm dbTableDataForm) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from " + table);
            if (!StringUtil.isEmpty(dbTableDataForm.getKeyword()) && !StringUtil.isEmpty(dbTableDataForm.getField())) {
                sql.append(" where " + dbTableDataForm.getField() + " like '%" + dbTableDataForm.getKeyword() + "%'");
            }
            ResultSet result = JdbcUtil.query(conn, sql.toString());
            ResultSetMetaData md = result.getMetaData();
            int columnCount = md.getColumnCount();
            while (result.next()) {
                Map<String, Object> rowData = new HashMap();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i).toLowerCase(), result.getObject(i));
                }
                list.add(rowData);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    public static List<Map<String, Object>> orcalGetData(Connection conn, String table, DbTableDataForm dbTableDataForm) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from " + table);
            if (!StringUtil.isEmpty(dbTableDataForm.getKeyword()) && !StringUtil.isEmpty(dbTableDataForm.getField())) {
                sql.append(" where " + dbTableDataForm.getField() + " like '%" + dbTableDataForm.getKeyword() + "%'");
            }
            ResultSet result = JdbcUtil.query(conn, sql.toString());
            ResultSetMetaData md = result.getMetaData();
            int columnCount = md.getColumnCount();
            while (result.next()) {
                Map<String, Object> rowData = new HashMap();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i).toLowerCase(), result.getString(i));
                }
                list.add(rowData);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return list;
    }

    //=================================================================================================

    /**
     * 创建表
     * @param conn
     * @param dbTableModel
     * @param tableFieldList
     * @throws DataException
     */
    public static void createSqlserverTable(Connection conn, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList) throws DataException {
        StringBuilder sql = new StringBuilder();
        sql.append("create table " + dbTableModel.getNewTable() + " ");
        sql.append("( ");
        for (DbTableFieldModel item : tableFieldList) {
            sql.append(item.getField() + " " + item.getDataType());
            if (ColumnType.MYSQL_VARCHAR.equals(item.getDataType()) || ColumnType.MYSQL_DECIMAL.equals(item.getDataType())) {
                sql.append("(" + item.getDataLength() + ") ");
            }
            if ("1".equals(String.valueOf(item.getPrimaryKey()))) {
                sql.append(" NOT NULL PRIMARY KEY");
            } else if (item.getAllowNull().compareTo(0) == 0) {
                sql.append(" NOT NULL ");
            } else {
                sql.append(" NULL ");
            }
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(");");
        sql.append("declare @CurrentUser sysname\r\n");
        sql.append("select @CurrentUser = user_name()\r\n");
        sql.append("execute sp_addextendedproperty 'MS_Description', '" + dbTableModel.getTableName() + "','user', @CurrentUser, 'table', '" + dbTableModel.getNewTable() + "'\r\n");
        for (DbTableFieldModel item : tableFieldList) {
            sql.append("execute sp_addextendedproperty 'MS_Description', '" + item.getFieldName() + "', 'user', @CurrentUser, 'table', '" + dbTableModel.getNewTable() + "', 'column', '" + item.getField() + "'\r\n");
        }
        JdbcUtil.custom(conn, sql.toString());
    }

    public static void createMySqlTable(Connection conn, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList) throws DataException {
        StringBuilder sql = new StringBuilder();
        //CREATE TABLE `base_billrule`  (
        //  `F_Id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '自然主键',
        //  `F_FullName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单据名称',
        //  PRIMARY KEY (`F_Id`) USING BTREE ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '单据规则' ROW_FORMAT = Dynamic;
        sql.append("create table " + dbTableModel.getNewTable() + " ");
        sql.append("( ");
        for (DbTableFieldModel item : tableFieldList) {
            sql.append("`" + item.getField() + "` " + item.getDataType());
            if (ColumnType.MYSQL_VARCHAR.equals(item.getDataType()) || ColumnType.MYSQL_DECIMAL.equals(item.getDataType())) {
                sql.append("(" + item.getDataLength() + ") ");
            }


            if ("1".equals(String.valueOf(item.getPrimaryKey()))) {
                sql.append(" NOT NULL PRIMARY KEY COMMENT '" + item.getFieldName() + "',");
            } else if (item.getAllowNull().compareTo(0) == 0) {
                sql.append(" NOT NULL COMMENT '" + item.getFieldName() + "',");
            } else {
                sql.append(" NULL COMMENT '" + item.getFieldName() + "',");
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") COMMENT = '" + dbTableModel.getTableName() + "'");
        JdbcUtil.custom(conn, sql.toString());
    }

    public static void createOrcalTable(Connection conn, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList, String tableSpace) throws DataException {
        StringBuilder sql = new StringBuilder();
        sql.append("create table " + dbTableModel.getNewTable() + " ");
        sql.append("( ");
        for (DbTableFieldModel item : tableFieldList) {
            switch (item.getDataType()){
                case ColumnType.MYSQL_VARCHAR:
                    sql.append(item.getField() + " NVARCHAR2");
                    break;
                case ColumnType.MYSQL_DATETIME:
                    sql.append(item.getField() + " DATE");
                    break;
                case ColumnType.MYSQL_DECIMAL:
                    sql.append(item.getField()+" DECIMAL");
                    break;
                case ColumnType.MYSQL_TEXT:
                    sql.append(item.getField()+" CLOB");
                    break;
                default:
                    sql.append(item.getField()+" NUMBER");
            }
            if (ColumnType.MYSQL_VARCHAR.equals(item.getDataType()) || ColumnType.MYSQL_DECIMAL.equals(item.getDataType())) {
                sql.append("(" + item.getDataLength() + ") ");
            }
            if ("1".equals(String.valueOf(item.getPrimaryKey()))) {
                sql.append(" PRIMARY KEY");
            } else if (item.getAllowNull().compareTo(0) == 0) {
                sql.append(" NOT NULL ");
            } else {
                sql.append(" NULL ");
            }
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")\ntablespace "+tableSpace+";");
        //给表添加说明
        sql.append("COMMENT ON TABLE " + dbTableModel.getNewTable() + " IS '" + dbTableModel.getTableName() + "';");
        //给字段添加说明
        for (DbTableFieldModel item : tableFieldList) {
            sql.append("COMMENT ON COLUMN " + dbTableModel.getNewTable() + "." + item.getField() + " IS '" + item.getFieldName() + "';");
        }
        OracleUtil.oracleCustom(conn,sql.toString());
    }

    //=================================================================================================

    /**
     * 更新表
     * @param conn
     * @param dbTableModel
     * @param tableFieldList
     * @throws DataException
     */
    public static void updateSqlserverTable(Connection conn, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList) throws DataException {

        StringBuilder sql = new StringBuilder();
        sql.append("drop table " + dbTableModel.getTable() + ";");
        sql.append("; create table " + dbTableModel.getNewTable() + " ");
        sql.append("( ");
        for (DbTableFieldModel item : tableFieldList) {
            sql.append(item.getField() + " " + item.getDataType());
            if (ColumnType.MYSQL_VARCHAR.equals(item.getDataType()) || ColumnType.MYSQL_DECIMAL.equals(item.getDataType())) {
                sql.append("(" + item.getDataLength() + ") ");
            }
            if ("1".equals(String.valueOf(item.getPrimaryKey()))) {
                sql.append(" NOT NULL PRIMARY KEY");
            } else if (item.getAllowNull().compareTo(0) == 0) {
                sql.append(" NOT NULL ");
            } else {
                sql.append(" NULL ");
            }
            sql.append(",");
        }
        sql = sql.deleteCharAt(sql.length() - 1);
        sql.append(");");
        sql.append("declare @CurrentUser sysname\r\n");
        sql.append("select @CurrentUser = user_name()\r\n");
        sql.append("execute sp_addextendedproperty 'MS_Description', '" + dbTableModel.getTableName() + "','user', @CurrentUser, 'table', '" + dbTableModel.getNewTable() + "'\r\n");
        for (DbTableFieldModel item : tableFieldList) {
            sql.append("execute sp_addextendedproperty 'MS_Description', '" + item.getFieldName() + "', 'user', @CurrentUser, 'table', '" + dbTableModel.getNewTable() + "', 'column', '" + item.getField() + "'\r\n");
        }
        JdbcUtil.custom(conn, sql.toString());
    }

    public static void updateMySqlTable(Connection conn, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList) throws DataException {
        String delSql;
        StringBuilder crSql = new StringBuilder();
        //先随机生成一个表名
        String ramTable = dbTableModel.getNewTable() + "_" + RandomUtil.enUuId();
        delSql = "drop table IF EXISTS " + dbTableModel.getTable();
        crSql.append("create table " + ramTable + " ");
        crSql.append("( ");
        for (DbTableFieldModel item : tableFieldList) {
            crSql.append("`" + item.getField() + "` " + item.getDataType());
            if (ColumnType.MYSQL_VARCHAR.equals(item.getDataType()) || ColumnType.MYSQL_DECIMAL.equals(item.getDataType())) {
                crSql.append("(" + item.getDataLength() + ") ");
            }
            if ("1".equals(String.valueOf(item.getPrimaryKey()))) {
                crSql.append(" NOT NULL PRIMARY KEY COMMENT '" + item.getFieldName() + "',");
            } else if (item.getAllowNull().compareTo(0) == 0) {
                crSql.append(" NOT NULL COMMENT '" + item.getFieldName() + "',");
            } else {
                crSql.append(" NULL COMMENT '" + item.getFieldName() + "',");
            }
        }
        crSql = crSql.deleteCharAt(crSql.length() - 1);
        crSql.append(") COMMENT = '" + dbTableModel.getTableName() + "'");
        JdbcUtil.upTableFields(conn, delSql, crSql.toString(), ramTable, dbTableModel.getNewTable());
    }

    public static void updateOrcalTable(Connection conn, DbTableModel dbTableModel, List<DbTableFieldModel> tableFieldList, String tableSpace) throws DataException {
        StringBuilder sql = new StringBuilder();
        sql.append("drop table " + dbTableModel.getTable()+";");
        sql.append("create table " + dbTableModel.getNewTable() + " ");
        sql.append("( ");
        for (DbTableFieldModel item : tableFieldList) {
            switch (item.getDataType()){
                case ColumnType.MYSQL_VARCHAR:
                    sql.append(item.getField() + " NVARCHAR2");
                    break;
                case ColumnType.MYSQL_DATETIME:
                    sql.append(item.getField() + " DATE");
                    break;
                case ColumnType.MYSQL_DECIMAL:
                    sql.append(item.getField()+" DECIMAL");
                    break;
                case ColumnType.MYSQL_TEXT:
                    sql.append(item.getField()+" CLOB");
                    break;
                default:
                    sql.append(item.getField()+" NUMBER");
            }
            if (ColumnType.MYSQL_VARCHAR.equals(item.getDataType()) || ColumnType.MYSQL_DECIMAL.equals(item.getDataType())) {
                sql.append("(" + item.getDataLength() + ") ");
            }
            if ("1".equals(String.valueOf(item.getPrimaryKey()))) {
                sql.append(" PRIMARY KEY");
            } else if (item.getAllowNull().compareTo(0) == 0) {
                sql.append(" NOT NULL ");
            } else {
                sql.append(" NULL ");
            }
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")\ntablespace "+tableSpace+";");
        //给表添加说明
        sql.append("COMMENT ON TABLE " + dbTableModel.getNewTable() + " IS '" + dbTableModel.getTableName() + "';");
        //给字段添加说明
        for (DbTableFieldModel item : tableFieldList) {
            sql.append("COMMENT ON COLUMN " + dbTableModel.getNewTable() + "." + item.getField() + " IS '" + item.getFieldName() + "';");
        }
        OracleUtil.oracleCustom(conn,sql.toString());
    }

}
