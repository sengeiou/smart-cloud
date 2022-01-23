package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.ActivityEntity;
import smart.model.activity.ActivityPagination;

import java.util.List;

/**
 * 活动管理
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-12-30 17:41:03
 */
public interface ActivityService extends IService<ActivityEntity> {

    List<ActivityEntity> getList(ActivityPagination activityPagination);

    List<ActivityEntity> getTypeList(ActivityPagination activityPagination, String dataType);


    ActivityEntity getInfo(String id);

    void delete(ActivityEntity entity);

    void create(ActivityEntity entity);

    boolean update(String id, ActivityEntity entity);

//  子表方法
}
