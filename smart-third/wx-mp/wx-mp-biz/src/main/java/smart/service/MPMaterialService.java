package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.MPMaterialEntity;
import smart.exception.WxErrorException;

import java.util.List;

/**
 * 公众号素材
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface MPMaterialService extends IService<MPMaterialEntity> {

    /**
     * 列表
     *
     * @param type      素材类型
     * @param pagination 请求参数
     * @return
     */
    List<MPMaterialEntity> getList(int type, Pagination pagination);

    /**
     * 素材信息
     *
     * @param mediaId 公众号素材Id
     * @return
     */
    MPMaterialEntity getInfo(String mediaId);

    /**
     * 上传永久素材(图片（image）、语音（voice）和缩略图（thumb）
     *
     * @param entity 实体对象
     * @return
     */
    String UploadForeverMedia(MPMaterialEntity entity) throws WxErrorException;

    /**
     * 上传永久视频素材
     *
     * @param entity 实体对象
     * @return
     */
    String UploadForeverVideo(MPMaterialEntity entity) throws WxErrorException;

    /**
     * 上传永久图文素材
     *
     * @param entity 实体对象
     * @return
     */
    String UploadNews(MPMaterialEntity entity) throws WxErrorException;


    /**
     * 删除永久素材
     *
     * @param entity 实体对象
     * @throws WxErrorException
     */
    boolean DeleteForeverMedia(MPMaterialEntity entity) throws WxErrorException;
}
