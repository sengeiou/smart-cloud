package smart.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.entity.ProvinceEntity;

import java.util.List;

/**
 * 行政区划
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface ProvinceService extends IService<ProvinceEntity> {


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
     * 普通列表
     *
     * @param parentId 节点Id
     * @return
     */
    List<ProvinceEntity> getList(String parentId);

    /**
     * 普通列表
     *
     * @return
     */
    List<ProvinceEntity> getAllList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    ProvinceEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ProvinceEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ProvinceEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, ProvinceEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     * @return
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return
     */
    boolean next(String id);
}
