package smart.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.engine.entity.FlowEngineEntity;
import smart.engine.entity.FlowTaskEntity;
import smart.engine.enums.FlowHandleEventEnum;
import smart.engine.model.FlowHandleModel;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.OrderEntity;
import smart.form.entity.OrderEntryEntity;
import smart.form.entity.OrderReceivableEntity;
import smart.form.model.order.OrderForm;
import smart.form.model.order.OrderInfoVO;
import smart.form.model.order.PaginationOrder;

import java.util.List;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public interface OrderService extends IService<OrderEntity> {

    /**
     * 列表
     *
     * @param paginationOrder 分页
     * @return
     */
    List<OrderEntity> getList(PaginationOrder paginationOrder);

    /**
     * 子列表（订单明细）
     *
     * @param id 主表Id
     * @return
     */
    List<OrderEntryEntity> getOrderEntryList(String id);

    /**
     * 子列表（订单收款）
     *
     * @param id 主表Id
     * @return
     */
    List<OrderReceivableEntity> getOrderReceivableList(String id);

    /**
     * 信息（前单、后单）
     *
     * @param id     主键值
     * @param method 方法:prev、next
     * @return
     */
    OrderEntity getPrevOrNextInfo(String id, String method);

    /**
     * 信息（前单、后单）
     *
     * @param id     主键值
     * @param method 方法:prev、next
     * @return
     * @throws DataException 异常
     */
    OrderInfoVO getInfoVo(String id, String method) throws DataException;

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    OrderEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 订单信息
     */
    void delete(OrderEntity entity);

    /**
     * 新增
     *
     * @param entity              订单信息
     * @param orderEntryList      订单明细
     * @param orderReceivableList 订单收款
     * @param orderForm           提交状态
     * @throws WorkFlowException 异常
     */
    void create(OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList, OrderForm orderForm) throws WorkFlowException;

    /**
     * 更新
     *
     * @param id                  主键值
     * @param entity              订单信息
     * @param orderEntryList      订单明细
     * @param orderReceivableList 订单收款
     * @param orderForm           提交状态
     * @return
     * @throws WorkFlowException 异常
     */
    boolean update(String id, OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList, OrderForm orderForm) throws WorkFlowException;

    /**
     * 提交审核
     *
     * @param id                 主键值
     * @param flowEngineEntity   流程信息
     * @param freeApproverUserId 授权审批人
     * @param orderEntity        订单实体
     * @throws WorkFlowException 异常
     */
    void flowSubmit(String id, FlowEngineEntity flowEngineEntity, String freeApproverUserId, OrderEntity orderEntity) throws WorkFlowException;

    /**
     * 撤回审核
     *
     * @param flowTaskEntity  流程任务
     * @param flowHandleModel 流程经办
     * @throws WorkFlowException 异常
     */
    void flowRevoke(FlowTaskEntity flowTaskEntity, FlowHandleModel flowHandleModel) throws WorkFlowException;

    /**
     * 流程事件
     *
     * @param flowHandleEvent 经办事件
     * @param flowTaskEntity  流程任务
     */
    void flowHandleEvent(FlowHandleEventEnum flowHandleEvent, FlowTaskEntity flowTaskEntity);

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);

}
