package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.entity.ActivityEntity;
import smart.mapper.ActivityMapper;
import smart.model.activity.ActivityPagination;
import smart.service.ActivityService;
import smart.util.DateUtil;
import smart.util.StringUtil;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 活动管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-12-30 17:41:03
 */
@Service

public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, ActivityEntity> implements ActivityService {

    @Autowired
    private UserProvider userProvider;


    @Override
    public List<ActivityEntity> getList(ActivityPagination activityPagination) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<ActivityEntity> queryWrapper = new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if (!"null".equals(String.valueOf(activityPagination.getName()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getName, activityPagination.getName()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getActivitytype()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getActivitytype, activityPagination.getActivitytype()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getStarttime()))) {
            //起始日期-结束日期
            String datefield = DateUtil.daFormat(activityPagination.getStarttime());
            Date startTimes = DateUtil.stringToDate(datefield + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(datefield + " 23:59:59");
            queryWrapper.lambda().ge(ActivityEntity::getStarttime, startTimes).le(ActivityEntity::getStarttime, endTimes);
        }

        if (!"null".equals(String.valueOf(activityPagination.getEndtime()))) {
            //起始日期-结束日期
            String datefield = DateUtil.daFormat(activityPagination.getEndtime());
            Date startTimes = DateUtil.stringToDate(datefield + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(datefield + " 23:59:59");
            queryWrapper.lambda().ge(ActivityEntity::getEndtime, startTimes).le(ActivityEntity::getEndtime, endTimes);
        }

        if (!"null".equals(String.valueOf(activityPagination.getStatus()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getStatus, activityPagination.getStatus()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getOriginator()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getOriginator, activityPagination.getOriginator()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getEnabledmark()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getEnabledmark, activityPagination.getEnabledmark()));
        }

        //排序
        if (StringUtil.isEmpty(activityPagination.getSidx())) {
            queryWrapper.lambda().orderByDesc(ActivityEntity::getCreatortime);
        } else {
            queryWrapper = "asc".equals(activityPagination.getSort().toLowerCase()) ? queryWrapper.orderByAsc(activityPagination.getSidx()) : queryWrapper.orderByDesc(activityPagination.getSidx());
        }
        Page<ActivityEntity> page = new Page<>(activityPagination.getCurrentPage(), activityPagination.getPageSize());
        IPage<ActivityEntity> userIPage = this.page(page, queryWrapper);
        return activityPagination.setData(userIPage.getRecords(), userIPage.getTotal());
    }

    @Override
    public List<ActivityEntity> getTypeList(ActivityPagination activityPagination, String dataType) {
        QueryWrapper<ActivityEntity> queryWrapper = new QueryWrapper<>();
        //关键字（账户、姓名、手机）
        if (!"null".equals(String.valueOf(activityPagination.getName()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getName, activityPagination.getName()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getActivitytype()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getActivitytype, activityPagination.getActivitytype()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getStarttime()))) {
            //起始日期-结束日期
            String datefield = DateUtil.daFormat(activityPagination.getStarttime());
            Date startTimes = DateUtil.stringToDate(datefield + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(datefield + " 23:59:59");
            queryWrapper.lambda().ge(ActivityEntity::getStarttime, startTimes).le(ActivityEntity::getStarttime, endTimes);
        }

        if (!"null".equals(String.valueOf(activityPagination.getEndtime()))) {
            //起始日期-结束日期
            String datefield = DateUtil.daFormat(activityPagination.getEndtime());
            Date startTimes = DateUtil.stringToDate(datefield + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(datefield + " 23:59:59");
            queryWrapper.lambda().ge(ActivityEntity::getEndtime, startTimes).le(ActivityEntity::getEndtime, endTimes);
        }

        if (!"null".equals(String.valueOf(activityPagination.getStatus()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getStatus, activityPagination.getStatus()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getOriginator()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getOriginator, activityPagination.getOriginator()));
        }

        if (!"null".equals(String.valueOf(activityPagination.getEnabledmark()))) {
            queryWrapper.lambda().and(t -> t.like(ActivityEntity::getEnabledmark, activityPagination.getEnabledmark()));
        }

        //排序
        if (StringUtil.isEmpty(activityPagination.getSidx())) {
            queryWrapper.lambda().orderByDesc(ActivityEntity::getCreatortime);
        } else {
            queryWrapper = "asc".equals(activityPagination.getSort().toLowerCase()) ? queryWrapper.orderByAsc(activityPagination.getSidx()) : queryWrapper.orderByDesc(activityPagination.getSidx());
        }
        if ("0".equals(dataType)) {
            Page<ActivityEntity> page = new Page<>(activityPagination.getCurrentPage(), activityPagination.getPageSize());
            IPage<ActivityEntity> userIPage = this.page(page, queryWrapper);
            return activityPagination.setData(userIPage.getRecords(), userIPage.getTotal());
        } else {
            return this.list(queryWrapper);
        }
    }

    @Override
    public ActivityEntity getInfo(String id) {
        QueryWrapper<ActivityEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ActivityEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(ActivityEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, ActivityEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(ActivityEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法
}
