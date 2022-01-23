package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.MPEventContentEntity;

/**
 * 事件内容
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface MPEventContentService extends IService<MPEventContentEntity> {


    MPEventContentEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    boolean delete(MPEventContentEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(MPEventContentEntity entity);

    /**
     * 更新
     *
     * @param entity 实体对象
     */
    boolean update(MPEventContentEntity entity);
}
