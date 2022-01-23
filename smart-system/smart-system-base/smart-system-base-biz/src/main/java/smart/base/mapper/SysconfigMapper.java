package smart.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.base.entity.SysConfigEntity;


/**
 * 系统配置
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface SysconfigMapper extends BaseMapper<SysConfigEntity> {

    int deleteFig();

    int deleteMpFig();

    int deleteQyhFig();
}
