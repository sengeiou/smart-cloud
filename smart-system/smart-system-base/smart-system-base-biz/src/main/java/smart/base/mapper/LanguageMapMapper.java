package smart.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.base.entity.LanguageMapEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 翻译数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface LanguageMapMapper extends BaseMapper<LanguageMapEntity> {

    List<Map<String,Object>> getList(@Param("sql") String sql);
}
