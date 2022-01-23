package smart.base.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.entity.DictionaryTypeEntity;

import java.util.List;

/**
 * 字典分类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface DictionaryTypeService extends IService<DictionaryTypeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<DictionaryTypeEntity> getList();

    /**
     * 信息
     *
     * @param enCode 代码
     * @return
     */
    DictionaryTypeEntity getInfoByEnCode(String enCode);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    DictionaryTypeEntity getInfo(String id);

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
    void create(DictionaryTypeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, DictionaryTypeEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(DictionaryTypeEntity entity);

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
