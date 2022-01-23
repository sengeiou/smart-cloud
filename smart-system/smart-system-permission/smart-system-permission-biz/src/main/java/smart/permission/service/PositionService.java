package smart.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.permission.entity.PositionEntity;
import smart.permission.model.position.PaginationPosition;

import java.util.List;

/**
 * 岗位信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface PositionService extends IService<PositionEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<PositionEntity> getList();

    /**
     * 获取redis存储的岗位信息
     *
     * @return
     */
    List<PositionEntity> getPosRedisList();

    /**
     * 列表
     *
     * @param  paginationPosition 条件
     * @return
     */
    List<PositionEntity> getList(PaginationPosition paginationPosition);

    /**
     * 列表
     * @param userId 用户主键
     * @return
     */
    List<PositionEntity> getListByUserId(String userId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    PositionEntity getInfo(String id);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(PositionEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, PositionEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(PositionEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     */
    boolean next(String id);

    /**
     * 获取名称
     * @param id
     * @return
     */
    List<PositionEntity> getPositionName(List<String> id);
}
