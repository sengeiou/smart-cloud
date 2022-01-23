package smart.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.message.entity.MessageReceiveEntity;
import smart.message.mapper.MessagereceiveMapper;
import smart.message.service.MessagereceiveService;
import org.springframework.stereotype.Service;


/**
 * 消息接收 服务实现类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class MessagereceiveServiceImpl extends ServiceImpl<MessagereceiveMapper, MessageReceiveEntity> implements MessagereceiveService {

}
