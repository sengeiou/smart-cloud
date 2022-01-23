package smart.roadtooth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.roadtooth.VisualDataEntity;
import smart.roadtooth.model.PaginationData;

import java.util.List;

/**
 * 大屏数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface VisualDataService extends IService<VisualDataEntity> {

    /**
     * 获取大屏列表(分页)
     *
     * @param pagination 分类
     */
    List<VisualDataEntity> getList(PaginationData pagination);

    /**
     * 获取大屏
     *
     */
    List<VisualDataEntity> getList();

    /**
     * 获取大屏基本信息
     *
     * @param id 主键
     */
    VisualDataEntity getInfo(String id);

    /**
     * 新增
     *
     * @param entity       大屏基本对象
     */
    void create(VisualDataEntity entity);

    /**
     * 修改
     *
     * @param id     主键
     * @param entity 实体
     */
    boolean update(String id, VisualDataEntity entity);


    /**
     * 删除
     *
     * @param entity 实体
     */
    void delete(VisualDataEntity entity);

    /**
     * 验证重复名称
     *
     * @param name 名称
     * @param id   id
     * @return
     */
    boolean isExistByName(String id, String name);
}
