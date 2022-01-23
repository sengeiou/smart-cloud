package smart.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.DocumentEntity;
import smart.entity.DocumentShareEntity;

import java.util.List;

/**
 * 知识文档
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface DocumentService extends IService<DocumentEntity> {

    /**
     * 列表（全部文档）
     *
     * @return
     */
    List<DocumentEntity> getFolderList();

    /**
     * 列表（全部文档）
     *
     * @param parentId 文档父级
     * @return
     */
    List<DocumentEntity> getAllList(String parentId);

    /**
     * 列表（回收站）
     * @return
     */
    List<DocumentEntity> getTrashList();

    /**
     * 列表（我的共享）
     * @return
     */
    List<DocumentEntity> getShareOutList();

    /**
     * 列表（共享给我）
     * @return
     */
    List<DocumentEntity> getShareTomeList();

    /**
     * 列表（共享人员）
     *
     * @param documentId 文档主键
     * @return
     */
    List<DocumentShareEntity> getShareUserList(String documentId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    DocumentEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(DocumentEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(DocumentEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, DocumentEntity entity);

    /**
     * 共享文件（创建）
     *
     * @param documentId  文档主键
     * @param shareUserId 共享用户
     * @return
     */
    boolean sharecreate(String documentId, String[] shareUserId);

    /**
     * 共享文件（取消）
     *
     * @param documentId 文档主键
     * @return
     */
    boolean shareCancel(String documentId);

    /**
     * 回收站（删除）
     *
     * @param folderId 文件夹主键值
     * @return
     */
    void trashdelete(String folderId);

    /**
     * 回收站（还原）
     * @param id    主键值
     * @return
     */
    boolean trashRecovery(String id);

    /**
     * 文件/夹移动到
     *
     * @param id   主键值
     * @param toId 将要移动到Id
     * @return
     */
    boolean moveTo(String id, String toId);

    /**
     * 验证文件名是否重复
     *
     * @param id   主键值
     * @param fullName 文件夹名称
     * @return
     */
    boolean isExistByFullName(String fullName, String id);
}
