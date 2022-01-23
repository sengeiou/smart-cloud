package smart.service;

import smart.base.Pagination;
import smart.exception.WxErrorException;
import smart.model.mpuser.MPUserModel;

import java.util.List;

/**
 * 公众号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface MPUserService {

    /**
     * 列表
     *
     * @param pagination
     * @return
     */
    List<MPUserModel> getList(Pagination pagination) throws WxErrorException;

    /**
     * 列表
     *
     * @return
     */
    List<MPUserModel> getList() throws WxErrorException;

    /**
     * 获取标签下粉丝列表
     *
     * @param pagination
     * @param tagId     标签id
     * @return
     */
    List<MPUserModel> GetListByTagId(Pagination pagination, String tagId) throws WxErrorException;

    /**
     * 黑名单列表
     *
     * @param pagination
     * @return
     */
    List<MPUserModel> GetBlackList(Pagination pagination) throws WxErrorException;

    /**
     * 用户信息
     *
     * @param openId
     * @return
     */
    MPUserModel UserInfo(String openId) throws WxErrorException;

    /**
     * 修改关注者备注信息
     *
     * @param userModel
     */
    boolean UpdateRemark(MPUserModel userModel) throws WxErrorException;

    /**
     * 加入黑名单
     *
     * @param openId
     */
    boolean AddBatchBlack(String openId) throws WxErrorException;

    /**
     * 移除黑名单
     *
     * @param openId
     */
    boolean DeleteBatchUnBlack(String openId) throws WxErrorException;
}
