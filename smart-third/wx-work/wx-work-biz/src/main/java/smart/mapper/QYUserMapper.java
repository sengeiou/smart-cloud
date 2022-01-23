package smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.QYUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface QYUserMapper extends BaseMapper<QYUserEntity> {

    /**
     * 获取列表
     * @param orderBy
     * @return
     */
    List<QYUserEntity> getList(@Param("orderBy") String orderBy);
}
