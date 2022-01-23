package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.entity.EmailConfigEntity;
import smart.entity.EmailReceiveEntity;
import smart.entity.EmailSendEntity;
import smart.base.PaginationTime;
import smart.exception.DataException;

import java.util.List;

/**
 * 邮件接收
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface EmailReceiveService extends IService<EmailReceiveEntity> {

    /**
     * 列表（收件箱）
     *
     * @param paginationTime 分页条件
     * @return
     */
    List<EmailReceiveEntity> getReceiveList(PaginationTime paginationTime);

    /**
     * 列表（收件箱）
     *
     * @return
     */
    List<EmailReceiveEntity> getReceiveList();

    /**
     * 列表（星标件）
     *
     * @param paginationTime 分页条件
     * @return
     */
    List<EmailReceiveEntity> getStarredList(PaginationTime paginationTime);

    /**
     * 列表（草稿箱）
     *
     * @param paginationTime 分页条件
     * @return
     */
    List<EmailSendEntity> getDraftList(PaginationTime paginationTime);

    /**
     * 列表（已发送）
     *
     * @param paginationTime 分页条件
     * @return
     */
    List<EmailSendEntity> getSentList(PaginationTime paginationTime);

    /**
     * 信息（配置）
     *
     * @return
     */
    EmailConfigEntity getConfigInfo();

    /**
     * 信息（配置）
     * @param userId
     * @return
     */
    EmailConfigEntity getConfigInfo(String userId);

    /**
     * 信息（收件/发件）
     *
     * @param id 主键值
     * @return
     */
    Object getInfo(String id);

    /**
     * 删除邮件（发、收）
     *
     * @param id 主键值
     * @return
     */
    boolean delete(String id);

    /**
     * 存草稿
     *
     * @param entity 实体对象
     */
    void saveDraft(EmailSendEntity entity);

    /**
     * 收邮件设置 已读/未读
     *
     * @param id
     * @param isRead
     * @return
     */
    boolean receiveRead(String id, int isRead);

    /**
     * 收邮件 星标邮件/取消星标
     *
     * @param id
     * @param isStarred
     * @return
     */
    boolean receiveStarred(String id, int isStarred);

    /**
     * 邮箱验证
     *
     * @param configEntity
     * @return
     */
    boolean checkLogin(EmailConfigEntity configEntity);

    /**
     * 保存邮箱配置
     *
     * @param configEntity
     * @throws DataException
     * @return
     */
    void saveConfig(EmailConfigEntity configEntity) throws DataException;

    /**
     * 发邮件
     *
     * @param entity     实体对象
     * @param mailConfig 邮件配置
     * @return
     */
    int saveSent(EmailSendEntity entity, EmailConfigEntity mailConfig) ;

    /**
     * 收邮件
     *
     * @param mailConfig 邮件配置
     * @return
     */
    int receive(EmailConfigEntity mailConfig);
}
