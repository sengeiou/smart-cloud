package smart.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.engine.entity.FlowTaskEntity;
import smart.engine.model.flowtask.FlowTaskWaitListModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 流程任务
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface FlowTaskMapper extends BaseMapper<FlowTaskEntity> {
    /**
     * 获取我的待办
     * @param map 参数
     * @return
     */
    List<FlowTaskEntity> getTrialList(@Param("map") Map<String, Object> map);

    /**
     * 抄送事宜
     * @param sql 自定义sql语句
     * @return
     */
    List<FlowTaskEntity> getCirculateList(@Param("sql") String sql);

    /**
     * 待办事宜
     * @param sql 自定义sql语句
     * @return
     */
    List<FlowTaskWaitListModel> getWaitList(@Param("sql") String sql);
}
