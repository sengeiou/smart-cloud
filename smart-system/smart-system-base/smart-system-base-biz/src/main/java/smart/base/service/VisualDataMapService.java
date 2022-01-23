package smart.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Pagination;
import smart.base.entity.VisualDataMapEntity;

import java.util.List;

/**
 * 大屏地图
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface VisualDataMapService extends IService<VisualDataMapEntity> {

    /**
     * 获取大屏列表(分页)
     *
     * @param pagination 分类
     * @return
     */
    List<VisualDataMapEntity> getList(Pagination pagination);

    /**
     * 获取大屏列表
     *
     * @param
     * @return
     */
    List<VisualDataMapEntity> getList();

    /**
     * 获取大屏基本信息
     *
     * @param id 主键
     * @return
     */
    VisualDataMapEntity getInfo(String id);

    /**
     * 新增
     *
     * @param entity 实体
     */
    void create(VisualDataMapEntity entity);

    /**
     * 修改
     *
     * @param id     主键
     * @param entity 实体
     * @return
     */
    boolean update(String id, VisualDataMapEntity entity);

    /**
     * 删除
     *
     * @param entity 实体
     */
    void delete(VisualDataMapEntity entity);

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
}
