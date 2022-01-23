package smart.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.ActionResult;
import smart.base.entity.DataInterfaceEntity;
import smart.base.model.datainterface.PaginationDataInterface;
import smart.exception.DataException;

import java.util.List;
import java.util.Map;

/**
 * 数据接口业务层
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
public interface DataInterfaceService extends IService<DataInterfaceEntity> {
    /**
     * 获取接口列表(分页)
     * @param pagination
     * @return
     */
    List<DataInterfaceEntity> getList(PaginationDataInterface pagination);

    /**
     * 获取接口列表下拉框
     * @return
     */
    List<DataInterfaceEntity> getList();

    /**
     * 获取接口数据
     * @param id
     * @return
     */
    DataInterfaceEntity getInfo(String id);

    /**
     * 添加数据接口
     * @param entity
     * @throws DataException
     */
    void create(DataInterfaceEntity entity) throws DataException;

    /**
     * 修改接口
     * @param entity
     * @param id
     * @return
     * @throws DataException
     */
    boolean update(DataInterfaceEntity entity,String id) throws DataException;

    /**
     * 删除接口
     * @param entity
     */
    void delete(DataInterfaceEntity entity);

    /**
     * 判断接口名称是否重复
     * @param fullName
     * @param id
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 访问接口
     * @param id
     * @param sql
     * @return
     * @throws DataException
     */
    List<Map<String,Object>> get(String id, String sql) throws DataException;

    /**
     * 访问接口路径
     * @param id
     * @return
     */
    ActionResult infoToId(String id);

}
