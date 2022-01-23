package smart.message;

import smart.message.fallback.NoticeApiFallback;
import smart.message.entity.MessageEntity;
import smart.message.model.SentMessageModel;
import smart.utils.FeignName;
import smart.base.ActionResult;
import smart.message.model.MessageFlowForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
/**
 * 调用系统消息Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME, fallback = NoticeApiFallback.class, path = "/Message")
public interface NoticeApi {

    /**
     * 工作流发送消息
     * @param messageFlowForm
     * @return
     */
    @GetMapping("/flow/sentMessage")
    ActionResult<String> sentMessage(MessageFlowForm messageFlowForm);

    /**
     * 列表（通知公告）
     *
     * @param
     * @return
     */
    @GetMapping("/GetNoticeList")
    List<MessageEntity> getNoticeList();

    /**
     * 发送消息
     * @param sentMessageModel
     * @return
     */
    @PostMapping("/SentMessage")
    void sentMessage(@RequestBody SentMessageModel sentMessageModel);
}
