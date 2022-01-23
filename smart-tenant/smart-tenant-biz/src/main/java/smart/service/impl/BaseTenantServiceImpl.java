package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.mapper.BaseTenantMapper;
import smart.service.BaseTenantService;
import smart.util.DataSourceUtil;
import smart.util.DateUtil;
import smart.util.JdbcUtil;
import smart.BaseTenantEntity;
import smart.model.BaseTenantDeForm;
import smart.model.BaseTenantPage;
import smart.model.DbTableModel;
import smart.util.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * baseTenant
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Service
@Slf4j
public class BaseTenantServiceImpl extends ServiceImpl<BaseTenantMapper, BaseTenantEntity> implements BaseTenantService {

    @Autowired
    private DataSourceUtil dbValue;

    @Value("${spring.datasource.dbinit}")
    private String initData;

    @Override
    public List<BaseTenantEntity> getList(BaseTenantPage baseTenantPage) {
        QueryWrapper<BaseTenantEntity> queryWrapper = new QueryWrapper<>();
        String startTime = baseTenantPage.getStartTime() != null ? baseTenantPage.getStartTime() : null;
        String endTime = baseTenantPage.getStartTime() != null ? baseTenantPage.getStartTime() : null;
        //时间范围
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(BaseTenantEntity::getExpiresTime, startTimes).le(BaseTenantEntity::getExpiresTime, endTimes);
        }
        //关键字
        if (StringUtils.isNotEmpty(baseTenantPage.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(BaseTenantEntity::getFullName, baseTenantPage.getKeyword())
                            .or().like(BaseTenantEntity::getEnCode, baseTenantPage.getKeyword())
                            .or().like(BaseTenantEntity::getComPanyName, baseTenantPage.getKeyword())
            );
        }
        Page<BaseTenantEntity> page = new Page<>(baseTenantPage.getCurrentPage(), baseTenantPage.getPageSize());
        IPage<BaseTenantEntity> userIpage = this.page(page, queryWrapper);
        return baseTenantPage.setData(userIpage.getRecords(), userIpage.getTotal());
    }

    @Override
    public BaseTenantEntity getInfo(String id) {
        QueryWrapper<BaseTenantEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BaseTenantEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public BaseTenantEntity getEnCode(String encode) {
        QueryWrapper<BaseTenantEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BaseTenantEntity::getEnCode, encode);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(BaseTenantEntity entity) {
        entity.setExpiresTime(DateUtil.dateAddDays(null, 30));
        this.save(entity);
    }

    @Override
    public boolean update(String id, BaseTenantEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void createDb(String dbName) {
        Connection conn = null;
        Connection dataConn = null;
        try {
            conn = JdbcUtil.getConn(dbValue.getDriverClassName(), dbValue.getUserName(), dbValue.getPassword(), dbValue.getUrl().replace("{dbName}", dbValue.getDbName()));
            String sql = "CREATE DATABASE " + dbName;
            JdbcUtil.customs(conn, sql);
            dataConn = JdbcUtil.getConn(dbValue.getDriverClassName(), dbValue.getUserName(), dbValue.getPassword(), dbValue.getUrl().replace("{dbName}", initData));
            List<DbTableModel> list = new ArrayList<>();
            if (JdbcUtil.getDbType(dbValue.getDriverClassName())) {
                list = JdbcUtils.mysqlgetList(dataConn, initData);
                for (DbTableModel model : list) {
                    StringBuffer into = new StringBuffer();
                    into.append("create table " + dbName + "." + model.getTable() + " as select * from " + model.getTable() + "; ");
                    into.append(" ALTER TABLE " + dbName + "." + model.getTable() + " ADD PRIMARY KEY ("+model.getPrimaryKey()+"); ");
                    try {
                        JdbcUtil.customs(dataConn, into.toString());
                    }catch (Exception e) {
                        log.error("mysql主键有问题:"+e.getMessage()+" 数据库表: "+model.getTable());
                    }
                }
            } else {
                list = JdbcUtils.sqlServergetList(dataConn, initData);
                for (DbTableModel model : list) {
                    StringBuffer into = new StringBuffer();
                    into.append("select * into " + dbName + ".dbo." + model.getTable() + " from " + initData + ".dbo." + model.getTable() + "; ");
                    into.append(" ALTER TABLE " + dbName + ".dbo." + model.getTable() + " ADD PRIMARY KEY ("+model.getPrimaryKey()+"); ");
                    try {
                        JdbcUtil.customs(dataConn, into.toString());
                    }catch (Exception e) {
                        log.error("sqlserver主键有问题:"+e.getMessage()+" 数据库表: "+model.getTable());
                    }
                }
            }
            dataConn.close();
            conn.close();
        } catch (Exception e) {
            try {
                JdbcUtil.customs(conn, "DROP DATABASE " + dbName);
                conn.close();
                dataConn.close();
            } catch (Exception exception) {

            }
            log.error("初始化数据库异常：" + e.getMessage());
        }
    }

    @Override
    public void delete(BaseTenantEntity entity, BaseTenantDeForm deForm) {
        if (entity != null) {
            if (deForm.getIsClear() == 1) {
                Connection conn = null;
                try {
                    conn = JdbcUtil.getConn(dbValue.getDriverClassName(), dbValue.getUserName(), dbValue.getPassword(), dbValue.getUrl().replace("{dbName}", entity.getDbserviceName()));
                    JdbcUtil.customs(conn, "DROP DATABASE " + entity.getDbserviceName());
                    conn.close();
                } catch (Exception e) {
                    log.error("删除数据库异常：" + e.getMessage());
                }
            }
            this.removeById(entity.getId());
        }
    }
}
