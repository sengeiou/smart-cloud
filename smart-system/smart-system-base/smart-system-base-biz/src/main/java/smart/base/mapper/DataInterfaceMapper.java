package smart.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.base.entity.DataInterfaceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据接口
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Mapper
public interface DataInterfaceMapper extends BaseMapper<DataInterfaceEntity> {

}
