package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.MPMessageEntity;
import smart.exception.WxErrorException;
import smart.model.mpmessage.PaginationMPMessage;

import java.util.List;

/**
 * 公众号群发消息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface MPMessageService extends IService<MPMessageEntity> {

    /**
     * 列表
     *
     * @param paginationMPMessage
     * @return
     */
    List<MPMessageEntity> getList(PaginationMPMessage paginationMPMessage);

    /**
     * 预览
     *
     * @param openId 公众号用户Id
     * @param entity 实体对象
     */
    void Preview(String openId, MPMessageEntity entity) throws WxErrorException;

    /**
     * 根据标签进行群发(全部成员)
     *
     * @param entity 实体对象
     */
    void SendGroupMessageByTagId(MPMessageEntity entity) throws WxErrorException;
}
