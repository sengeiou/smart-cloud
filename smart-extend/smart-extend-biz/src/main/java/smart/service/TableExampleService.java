package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.TableExampleEntity;
import smart.model.tableexample.PaginationTableExample;

import java.util.List;

/**
 * 表格示例数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface TableExampleService extends IService<TableExampleEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<TableExampleEntity> getList();

    /**
     * 列表(带关键字)
     *
     * @param keyword   关键字
     * @return
     */
    List<TableExampleEntity> getList(String keyword);

    /**
     * 列表
     *
     * @param typeId 类别主键
     * @param paginationTableExample
     * @return
     */
    List<TableExampleEntity> getList(String typeId,PaginationTableExample paginationTableExample);

    /**
     * 列表
     *
     * @param paginationTableExample
     * @return
     */
    List<TableExampleEntity> getList(PaginationTableExample paginationTableExample);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    TableExampleEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(TableExampleEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     * @return
     */
    void create(TableExampleEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, TableExampleEntity entity);

    /**
     * 行编辑
     *
     * @param entity 实体对象
     * @return
     */
    boolean rowEditing(TableExampleEntity entity);
}
