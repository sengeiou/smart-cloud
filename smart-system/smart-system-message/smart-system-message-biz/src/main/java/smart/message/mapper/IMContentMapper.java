package smart.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.message.entity.IMContentEntity;
import smart.message.model.IMUnreadNumModel;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;

/**
 * 聊天内容
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface IMContentMapper extends BaseMapper<IMContentEntity> {

    List<IMUnreadNumModel> getUnreadList(@Param("receiveUserId") String receiveUserId);

    List<IMUnreadNumModel> getUnreadLists(@Param("receiveUserId") String receiveUserId);

    int readMessage(@Param("map") Map<String, String> map);
}

