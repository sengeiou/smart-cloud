package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.exception.WxErrorException;
import smart.model.qymessage.PaginationQYMessage;
import smart.QYMessageEntity;

import java.util.List;

/**
 * 消息发送
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface QYMessageService extends IService<QYMessageEntity> {

    /**
     * 列表
     *
     * @param paginationQyMessage
     * @return
     */
    List<QYMessageEntity> getList(PaginationQYMessage paginationQyMessage);

    /**
     * 发送
     *
     * @param entity
     */
    void sent(QYMessageEntity entity) throws WxErrorException;
}
