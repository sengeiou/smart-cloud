package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.EmployeeEntity;
import smart.model.EmployeeModel;
import smart.model.employee.EmployeeImportVO;
import smart.model.employee.PaginationEmployee;

import java.util.List;
import java.util.Map;

/**
 * 职员信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 */
public interface EmployeeService extends IService<EmployeeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<EmployeeEntity> getList();

    /**
     * 列表
     *
     * @param paginationEmployee
     * @return
     */
    List<EmployeeEntity> getList(PaginationEmployee paginationEmployee);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    EmployeeEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(EmployeeEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(EmployeeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    void update(String id, EmployeeEntity entity);

    /**
     * 导入预览
     *
     * @param personList 实体对象
     * @return
     */
    Map<String, Object> importPreview(List<EmployeeModel> personList);

    /**
     * 导入数据
     *
     * @param dt 数据源
     * @return
     */
    EmployeeImportVO importData(List<EmployeeModel> dt);

    /**
     * 导出pdf
     *
     * @param list      集合数据
     * @param outputUrl 保存路径
     * @return
     */
    void exportPdf(List<EmployeeEntity> list, String outputUrl);
}
