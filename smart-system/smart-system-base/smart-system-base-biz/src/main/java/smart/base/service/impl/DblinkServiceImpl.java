package smart.base.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.mapper.DbLinkMapper;
import smart.base.service.DblinkService;
import smart.util.JdbcUtil;
import smart.util.RandomUtil;
import smart.base.entity.DbLinkEntity;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

/**
 * 数据连接
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class DblinkServiceImpl extends ServiceImpl<DbLinkMapper, DbLinkEntity> implements DblinkService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<DbLinkEntity> getList() {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(DbLinkEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DbLinkEntity> getList(String keyWord) {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.like(DbLinkEntity::getFullName, keyWord)
        );
        queryWrapper.lambda().orderByDesc(DbLinkEntity::getSortCode).orderByDesc(DbLinkEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public DbLinkEntity getInfo(String id) {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DbLinkEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DbLinkEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(DbLinkEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(DbLinkEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, DbLinkEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(DbLinkEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        DbLinkEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(DbLinkEntity::getSortCode, upSortCode)
                .orderByDesc(DbLinkEntity::getSortCode);
        List<DbLinkEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            this.updateById(downEntity.get(0));
            this.updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @Transactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        DbLinkEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(DbLinkEntity::getSortCode, upSortCode)
                .orderByAsc(DbLinkEntity::getSortCode);
        List<DbLinkEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            downEntity.setLastModifyTime(new Date());
            upEntity.get(0).setSortCode(temp);
            this.updateById(upEntity.get(0));
            this.updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public boolean testDbConnection(DbLinkEntity entity) {
        Connection conn = JdbcUtil.getConn(entity.getDbType(), entity.getUserName(), entity.getPassword(), entity.getHost(), entity.getPort(), entity.getServiceName());
        boolean flag = false;
        if (conn != null) {
            flag = true;
        }
        return flag;
    }
}
