package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.ScheduleEntity;

import java.util.List;

/**
 * 日程安排
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface ScheduleService extends IService<ScheduleEntity> {

    /**
     * 列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    List<ScheduleEntity> getList(String startTime, String endTime);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    ScheduleEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(ScheduleEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ScheduleEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, ScheduleEntity entity);
}
