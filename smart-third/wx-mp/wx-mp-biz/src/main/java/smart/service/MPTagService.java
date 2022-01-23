package smart.service;

import smart.exception.WxErrorException;
import smart.model.mptag.MPTagsModel;

import java.util.List;

/**
 * 公众号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface MPTagService {

    /**
     * 标签列表
     *
     * @return
     */
    List<MPTagsModel> GetTageList() throws WxErrorException;

    /**
     * 创建标签
     *
     * @param tagName 标签名
     */
    void CreateTag(String tagName) throws WxErrorException;

    /**
     * 编辑标签
     *
     * @param tagsModel 标签模型
     */
    void UpdateTag(MPTagsModel tagsModel) throws WxErrorException;

    /**
     * 删除标签
     *
     * @param id 标签Id
     */
    boolean DeleteTag(int id) throws WxErrorException;

    /**
     * 批量为用户打标签
     *
     * @param openid    用户id
     * @param tagId     标签Id
     */
    void BatchTagged(String[] openid, String tagId) throws WxErrorException;

    /**
     * 批量为用户取消标签
     *
     * @param openid    用户id
     * @param tagId     标签Id
     */
    void BatchUnTagged(String[] openid, String tagId) throws WxErrorException;
}
