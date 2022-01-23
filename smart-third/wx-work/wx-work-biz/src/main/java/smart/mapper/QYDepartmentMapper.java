package smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.QYDepartmentEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业号部门
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface QYDepartmentMapper extends BaseMapper<QYDepartmentEntity> {

    /**
     * 获取列表
     * @return
     */
    List<QYDepartmentEntity> getList();

    /**
     * 通过id获取详情
     * @param userId
     * @return
     */
    List<QYDepartmentEntity> getListByUserId(@Param("userId") String userId);

}
