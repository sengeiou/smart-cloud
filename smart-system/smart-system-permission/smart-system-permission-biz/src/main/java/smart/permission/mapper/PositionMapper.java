package smart.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.permission.entity.PositionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface PositionMapper extends BaseMapper<PositionEntity> {

    List<PositionEntity> getListByUserId(@Param("userId") String userId);
}
