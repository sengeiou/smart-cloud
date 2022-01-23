package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.ProjectGanttEntity;
import smart.base.Page;

import java.util.List;

/**
 * 项目计划
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface ProjectGanttService extends IService<ProjectGanttEntity> {

    /**
     * 项目列表
     * @param page
     * @return
     */
    List<ProjectGanttEntity> getList(Page page);

    /**
     * 任务列表
     *
     * @param projectId 项目Id
     * @return
     */
    List<ProjectGanttEntity> getTaskList(String projectId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    ProjectGanttEntity getInfo(String id);

    /**
     * 判断是否允许删除
     *
     * @param id 主键值
     * @return
     */
    boolean allowDelete(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(ProjectGanttEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     * @return
     */
    void create(ProjectGanttEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, ProjectGanttEntity entity);

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
