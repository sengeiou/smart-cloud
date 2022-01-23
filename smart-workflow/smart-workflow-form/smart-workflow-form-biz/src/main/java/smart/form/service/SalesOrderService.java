package smart.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.exception.WorkFlowException;
import smart.form.entity.SalesOrderEntity;
import smart.form.entity.SalesOrderEntryEntity;

import java.util.List;

/**
 * 销售订单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
public interface SalesOrderService extends IService<SalesOrderEntity> {

    /**
     * 列表
     *
     * @param id 主键值
     * @return
     */
    List<SalesOrderEntryEntity> getSalesEntryList(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    SalesOrderEntity getInfo(String id);

    /**
     * 保存
     *
     * @param id                        主键值
     * @param entity                    实体对象
     * @param salesOrderEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void save(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList) throws WorkFlowException;

    /**
     * 提交
     *
     * @param id                        主键值
     * @param entity                    实体对象
     * @param salesOrderEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void submit(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList) throws WorkFlowException;

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);
}
