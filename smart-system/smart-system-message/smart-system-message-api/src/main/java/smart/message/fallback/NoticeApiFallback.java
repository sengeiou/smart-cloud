package smart.message.fallback;

import smart.base.ActionResult;
import smart.message.NoticeApi;
import smart.message.entity.MessageEntity;
import smart.message.model.MessageFlowForm;
import smart.message.model.SentMessageModel;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 调用系统消息Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class NoticeApiFallback implements NoticeApi {


    @Override
    public ActionResult<String> sentMessage(MessageFlowForm messageFlowForm) {
        return null;
    }

    @Override
    public List<MessageEntity> getNoticeList() {
        return null;
    }

    @Override
    public void sentMessage(SentMessageModel sentMessageModel) {

    }
}

