package smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.entity.BigDataEntity;

/**
 * 大数据测试
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface BigDataMapper extends BaseMapper<BigDataEntity> {

    Integer maxCode();

}
