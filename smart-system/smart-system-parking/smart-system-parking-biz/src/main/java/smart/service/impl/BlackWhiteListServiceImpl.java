package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.entity.BlackWhiteListEntity;
import smart.mapper.BlackWhiteListMapper;
import smart.model.blackwhitelist.BlackWhiteListPagination;
import smart.service.BlackWhiteListService;
import smart.util.StringUtil;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * p_black_white_list
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-12-17 17:24:37
 */
@Service

public class BlackWhiteListServiceImpl extends ServiceImpl<BlackWhiteListMapper, BlackWhiteListEntity> implements BlackWhiteListService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<BlackWhiteListEntity> getList(BlackWhiteListPagination blackWhiteListPagination) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<BlackWhiteListEntity> queryWrapper = new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if (!"null".equals(String.valueOf(blackWhiteListPagination.getListtype()))) {
            queryWrapper.lambda().and(t -> t.like(BlackWhiteListEntity::getListtype, blackWhiteListPagination.getListtype()));
        }

        if (!"null".equals(String.valueOf(blackWhiteListPagination.getPids()))) {
            queryWrapper.lambda().and(t -> t.like(BlackWhiteListEntity::getPids, blackWhiteListPagination.getPids()));
        }

        if (!"null".equals(String.valueOf(blackWhiteListPagination.getPlatenumber()))) {
            queryWrapper.lambda().and(t -> t.like(BlackWhiteListEntity::getPlatenumber, blackWhiteListPagination.getPlatenumber()));
        }

        //排序
        if (StringUtil.isEmpty(blackWhiteListPagination.getSidx())) {
            queryWrapper.lambda().orderByDesc(BlackWhiteListEntity::getCreatortime);
        } else {
            queryWrapper = "asc".equals(blackWhiteListPagination.getSort().toLowerCase()) ? queryWrapper.orderByAsc(blackWhiteListPagination.getSidx()) : queryWrapper.orderByDesc(blackWhiteListPagination.getSidx());
        }
        Page<BlackWhiteListEntity> page = new Page<>(blackWhiteListPagination.getCurrentPage(), blackWhiteListPagination.getPageSize());
        IPage<BlackWhiteListEntity> userIPage = this.page(page, queryWrapper);
        return blackWhiteListPagination.setData(userIPage.getRecords(), userIPage.getTotal());
    }

    @Override
    public List<BlackWhiteListEntity> getTypeList(BlackWhiteListPagination blackWhiteListPagination, String dataType) {
        QueryWrapper<BlackWhiteListEntity> queryWrapper = new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if (!"null".equals(String.valueOf(blackWhiteListPagination.getListtype()))) {
            queryWrapper.lambda().and(t -> t.like(BlackWhiteListEntity::getListtype, blackWhiteListPagination.getListtype()));
        }

        if (!"null".equals(String.valueOf(blackWhiteListPagination.getPids()))) {
            queryWrapper.lambda().and(t -> t.like(BlackWhiteListEntity::getPids, blackWhiteListPagination.getPids()));
        }

        if (!"null".equals(String.valueOf(blackWhiteListPagination.getPlatenumber()))) {
            queryWrapper.lambda().and(t -> t.like(BlackWhiteListEntity::getPlatenumber, blackWhiteListPagination.getPlatenumber()));
        }

        //排序
        if (StringUtil.isEmpty(blackWhiteListPagination.getSidx())) {
            queryWrapper.lambda().orderByDesc(BlackWhiteListEntity::getCreatortime);
        } else {
            queryWrapper = "asc".equals(blackWhiteListPagination.getSort().toLowerCase()) ? queryWrapper.orderByAsc(blackWhiteListPagination.getSidx()) : queryWrapper.orderByDesc(blackWhiteListPagination.getSidx());
        }
        if ("0".equals(dataType)) {
            Page<BlackWhiteListEntity> page = new Page<>(blackWhiteListPagination.getCurrentPage(), blackWhiteListPagination.getPageSize());
            IPage<BlackWhiteListEntity> userIPage = this.page(page, queryWrapper);
            return blackWhiteListPagination.setData(userIPage.getRecords(), userIPage.getTotal());
        } else {
            return this.list(queryWrapper);
        }
    }

    @Override
    public BlackWhiteListEntity getInfo(String id) {
        QueryWrapper<BlackWhiteListEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BlackWhiteListEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(BlackWhiteListEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, BlackWhiteListEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(BlackWhiteListEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法
}
