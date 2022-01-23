package smart.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.entity.DbLinkEntity;

import java.util.List;

/**
 * 数据连接
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface DblinkService extends IService<DbLinkEntity> {
    /**
     * 列表
     *
     * @return
     */
    List<DbLinkEntity> getList();

    /**
     * 列表关键字查询
     * @param keyWord
     * @return
     */
    List<DbLinkEntity> getList(String keyWord);

    /**
     * 信息
     *
     * @param id
     * @return
     */
    DbLinkEntity getInfo(String id);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(DbLinkEntity entity);

    /**
     * 更新
     * @param id        主键值
     * @param entity    实体对象
     * @return
     */
    boolean update(String id, DbLinkEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(DbLinkEntity entity);

    /**
     * 上移
     * @param id    主键值
     * @return
     */
    boolean first(String id);

    /**
     * 下移
     * @param id    主键值
     * @return
     */
    boolean next(String id);

    /**
     * 测试连接
     *
     * @param entity 实体对象
     * @return
     */
    boolean testDbConnection(DbLinkEntity entity);
}
