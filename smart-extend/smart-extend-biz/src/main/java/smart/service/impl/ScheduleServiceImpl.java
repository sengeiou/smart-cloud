package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.mapper.ScheduleMapper;
import smart.service.ScheduleService;
import smart.entity.ScheduleEntity;
import smart.util.DateUtil;
import smart.util.RandomUtil;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 日程安排
 *
 * @copyright 智慧停车公司
 * @author 开发平台组
 * @version V3.0.0
 * @date 2019年9月26日 上午9:18
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, ScheduleEntity> implements ScheduleService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ScheduleEntity> getList(String startTime, String endTime) {
        QueryWrapper<ScheduleEntity> queryWrapper = new QueryWrapper<>();
        Date startTimes = DateUtil.stringToDates(startTime);
        Date endTimes = DateUtil.stringToDates(endTime);
        queryWrapper.lambda().eq(ScheduleEntity::getCreatorUserId,userProvider.get().getUserId())
                .ge(ScheduleEntity::getStartTime,startTimes)
                .le(ScheduleEntity::getEndTime,endTimes)
                .orderByAsc(ScheduleEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public ScheduleEntity getInfo(String id) {
        QueryWrapper<ScheduleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ScheduleEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(ScheduleEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(ScheduleEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ScheduleEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }
}
