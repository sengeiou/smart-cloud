package smart.scheduletask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.scheduletask.entity.TimeTaskLogEntity;

import java.util.List;


/**
 * 执行记录
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface TimeTaskLogService extends IService<TimeTaskLogEntity> {

    /**
     * 获取记录总数
     * @param taskId 任务id
     * @return
     */
    List<TimeTaskLogEntity> getTaskList(String taskId);

    /**
     * 获取记录总数
     * @param taskId
     * @return
     */
    List<TimeTaskLogEntity> getTaskList(List<String> taskId);


}
