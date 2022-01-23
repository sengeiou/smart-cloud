package smart.base.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.entity.ComFieldsEntity;

import java.util.List;

/**
 *
 * 常用字段表
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
public interface ComFieldsService extends IService<ComFieldsEntity> {

    List<ComFieldsEntity> getList();

    ComFieldsEntity getInfo(String id);

    void create(ComFieldsEntity entity);

    boolean update(String id, ComFieldsEntity entity);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    void delete(ComFieldsEntity entity);
}
