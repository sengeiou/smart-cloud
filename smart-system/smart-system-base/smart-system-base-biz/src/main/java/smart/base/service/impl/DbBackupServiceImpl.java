package smart.base.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.base.mapper.DbBackupMapper;
import smart.base.service.DbBackupService;
import smart.emnus.FileTypeEnum;
import smart.file.FileApi;
import smart.util.*;
import smart.base.entity.DbBackupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 数据备份
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class DbBackupServiceImpl extends ServiceImpl<DbBackupMapper, DbBackupEntity> implements DbBackupService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private FileApi fileApi;

    @Override
    public List<DbBackupEntity> getList(Pagination pagination) {
        QueryWrapper<DbBackupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(DbBackupEntity::getSortCode);
        if(pagination.getKeyword()!=null){
            queryWrapper.lambda().and(
                    t->t.like(DbBackupEntity::getFileName,pagination.getKeyword())
                    .or().like(DbBackupEntity::getBackupDbName,pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByDesc(DbBackupEntity::getCreatorTime);
        Page<DbBackupEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DbBackupEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public DbBackupEntity getInfo(String id) {
        QueryWrapper<DbBackupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DbBackupEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(DbBackupEntity entity) {
        if (entity != null) {
            //删除文件
            FileUtil.deleteFile(fileApi.getPath(FileTypeEnum.DATABACKUP) + entity.getFileName());
            this.removeById(entity.getId());
        }
    }

    @Override
    public void create(DbBackupEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean dbBackup() {
        boolean flag =false;
        String path = fileApi.getPath(FileTypeEnum.DATABACKUP);
        if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.MYSQL.getDb())) {
            String fileName = RandomUtil.uuId() + ".sql";
            String url = dataSourceUtils.getUrl();
            String[] dataUrl=url.substring(0,url.lastIndexOf("?")).split("/");
            String host = dataUrl[2].split(":")[0];
            String dbName =dataUrl[3];
            //备份数据
            JdbcUtil.mysqlBackUp(host, dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), dbName, path, fileName);
            File file = new File(path + fileName);
            if(file.isFile()) {
                //备份记录
                DbBackupEntity entity = new DbBackupEntity();
                entity.setId(RandomUtil.uuId());
                entity.setFileName(fileName);
                entity.setFilePath(path + fileName);
                entity.setFileSize(FileUtil.getSize(String.valueOf(file.length())));
                entity.setCreatorUserId(userProvider.get().getUserId());
                this.save(entity);
                flag = true;
            }
        } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.SQL_SERVER.getDb())) {
            String fileName = RandomUtil.uuId() + ".bak";
            String url = dataSourceUtils.getUrl();
            String[] dataUrl=url.substring(0,url.lastIndexOf(";")).split("/");
            String host = dataUrl[2].split(":")[0];
            String post = dataUrl[2].split(":")[1];
            String dataseName=url.split("=")[1];
            //备份数据
            JdbcUtil.serverBackUp(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), host, post, path, fileName, dataseName);
            File file = new File(path + fileName);
            if(file.isFile()) {
                //备份记录
                DbBackupEntity entity = new DbBackupEntity();
                entity.setId(RandomUtil.uuId());
                entity.setFileName(fileName);
                entity.setFilePath(path + fileName);
                entity.setFileSize(FileUtil.getSize(String.valueOf(file.length())));
                entity.setCreatorUserId(userProvider.get().getUserId());
                this.save(entity);
                flag=true;
            }
        } else if (dataSourceUtils.getUrl().toLowerCase().contains(DbType.ORACLE.getDb())) {
            String fileName = RandomUtil.uuId() + ".dmp";
            String url = dataSourceUtils.getUrl();
            String[] dataUrl=url.split("@")[1].split(":");
            String host = dataUrl[0];
            String sid = dataUrl[2];
            //备份数据  最后一次心跳数据
            JdbcUtil.oracleBackUp(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), host, sid, path, fileName);
            File file = new File(path + fileName);
            if(file.isFile()) {
                //备份记录
                DbBackupEntity entity = new DbBackupEntity();
                entity.setId(RandomUtil.uuId());
                entity.setFileName(fileName);
                entity.setFilePath(path + fileName);
                entity.setFileSize(FileUtil.getSize(String.valueOf(file.length())));
                entity.setCreatorUserId(userProvider.get().getUserId());
                this.save(entity);
                flag=true;
            }
        }
        return flag;
    }
}
