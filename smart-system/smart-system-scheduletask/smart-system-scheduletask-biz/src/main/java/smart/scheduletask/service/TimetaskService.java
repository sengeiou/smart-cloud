package smart.scheduletask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.scheduletask.entity.TimeTaskEntity;
import smart.scheduletask.entity.TimeTaskLogEntity;

import java.util.List;

/**
 * 定时任务
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface TimetaskService extends IService<TimeTaskEntity> {

    /**
     * 列表
     *
     * @param  pagination 分页
     * @return
     */
    List<TimeTaskEntity> getList(Pagination pagination);

    /**
     * 列表（执行记录）
     *
     * @param pagination 分页
     * @param taskId    任务Id
     * @return
     */
    List<TimeTaskLogEntity> getTaskLogList(Pagination pagination, String taskId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    TimeTaskEntity getInfo(String id);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(TimeTaskEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, TimeTaskEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(TimeTaskEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     * @return
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return
     */
    boolean next(String id);

    /**
     * 执行记录
     *
     * @param entity 实体对象
     */
    void createTaskLog(TimeTaskLogEntity entity);

    /**
     * 启动所有任务
     */
    void startAll();
}
