package smart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.mapper.EmailSendMapper;
import smart.service.EmailSendService;
import smart.entity.EmailSendEntity;
import org.springframework.stereotype.Service;


/**
 * 邮件发送
 *
 * @copyright 智慧停车公司
 * @author 开发平台组
 * @date 2019年9月26日 上午9:18
 */
@Service
public class EmailSendServiceImpl extends ServiceImpl<EmailSendMapper, EmailSendEntity> implements EmailSendService {

}
