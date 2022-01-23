package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.BigDataEntity;
import smart.base.Pagination;
import smart.exception.WorkFlowException;

import java.util.List;

/**
 * 大数据测试
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface BigDataService extends IService<BigDataEntity> {

    /**
     * 列表
     *
     * @param pagination 分页参数
     * @return
     */
    List<BigDataEntity> getList(Pagination pagination);

    /**
     * 创建
     * @param insertCount           添加数量
     * @throws WorkFlowException
     */
    void create(int insertCount) throws WorkFlowException;
}
