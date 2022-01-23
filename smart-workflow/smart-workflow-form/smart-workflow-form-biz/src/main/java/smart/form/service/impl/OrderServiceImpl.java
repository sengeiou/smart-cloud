package smart.form.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.DateUtil;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.base.BillRuleApi;
import smart.base.UserInfo;
import smart.engine.entity.FlowEngineEntity;
import smart.engine.entity.FlowTaskEntity;
import smart.engine.entity.FlowTaskNodeEntity;
import smart.engine.enums.FlowHandleEventEnum;
import smart.engine.enums.FlowModuleEnum;
import smart.engine.enums.FlowStatusEnum;
import smart.engine.enums.FlowTaskStatusEnum;
import smart.engine.model.FlowHandleModel;
import smart.engine.service.FlowEngineService;
import smart.engine.service.FlowTaskNodeService;
import smart.engine.service.FlowTaskService;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.OrderEntity;
import smart.form.entity.OrderEntryEntity;
import smart.form.entity.OrderReceivableEntity;
import smart.form.mapper.OrderMapper;
import smart.form.model.order.*;
import smart.form.service.OrderEntryService;
import smart.form.service.OrderReceivableService;
import smart.form.service.OrderService;
import smart.form.util.FileManageUtil;
import smart.form.util.FileModel;
import smart.permission.AuthorizeApi;
import smart.permission.UsersApi;
import smart.permission.model.user.UserAllModel;
import smart.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrderReceivableService orderReceivableService;
    @Autowired
    private OrderEntryService orderEntryService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private AuthorizeApi authorizeApi;
    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private FileManageUtil fileManageUtil;
    @Autowired
    private FlowEngineService flowEngineService;
    @Autowired
    private UsersApi usersApi;

    /**前单**/
    private static String PREV = "prev";
    /**后单**/
    private static String NEXT = "next";

    @Override
    public List<OrderEntity> getList(PaginationOrder paginationOrder) {
        UserInfo userInfo = userProvider.get();
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        //关键字（订单编码、客户名称、业务人员）
        String keyWord = paginationOrder.getKeyword() != null ? paginationOrder.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(OrderEntity::getOrderCode, word)
                            .or().like(OrderEntity::getCustomerName, word)
                            .or().like(OrderEntity::getSalesmanName, word)
            );
        }
        //起始日期-结束日期
        String startTime = paginationOrder.getStartTime() != null ? paginationOrder.getStartTime() : null;
        String endTime = paginationOrder.getEndTime() != null ? paginationOrder.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(OrderEntity::getOrderDate, startTimes).le(OrderEntity::getOrderDate, endTimes);
        }
        //订单状态
        String mark = paginationOrder.getEnabledMark() != null ? paginationOrder.getEnabledMark() : null;
        if (!StringUtils.isEmpty(mark)) {
            queryWrapper.lambda().eq(OrderEntity::getEnabledMark, mark);
        }
        //排序
        if (StringUtils.isEmpty(paginationOrder.getSidx())) {
            queryWrapper.lambda().orderByDesc(OrderEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationOrder.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationOrder.getSidx()) : queryWrapper.orderByDesc(paginationOrder.getSidx());
        }
        Page<OrderEntity> page = new Page<>(paginationOrder.getCurrentPage(), paginationOrder.getPageSize());
        IPage<OrderEntity> orderEntityPage = this.page(page, queryWrapper);
        List<OrderEntity> data = orderEntityPage.getRecords();
        List<String> id = data.stream().map(t -> t.getId()).collect(Collectors.toList());
        if (data.size() > 0) {
            List<FlowTaskEntity> orderStaList = flowTaskService.getOrderStaList(id);
            for (OrderEntity entity : data) {
                FlowTaskEntity taskEntity = orderStaList.stream().filter(t -> t.getId().equals(entity.getId())).findFirst().orElse(null);
                if (taskEntity != null) {
                    entity.setCurrentState(taskEntity.getStatus());
                }
            }
        }
        return paginationOrder.setData(data, page.getTotal());
    }

    @Override
    public List<OrderEntryEntity> getOrderEntryList(String id) {
        QueryWrapper<OrderEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderEntryEntity::getOrderId, id).orderByDesc(OrderEntryEntity::getSortCode);
        return orderEntryService.list(queryWrapper);
    }

    @Override
    public List<OrderReceivableEntity> getOrderReceivableList(String id) {
        QueryWrapper<OrderReceivableEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderReceivableEntity::getOrderId, id).orderByDesc(OrderReceivableEntity::getSortCode);
        return orderReceivableService.list(queryWrapper);
    }

    @Override
    public OrderEntity getPrevOrNextInfo(String id, String method) {
        QueryWrapper<OrderEntity> result = new QueryWrapper<>();
        OrderEntity orderEntity = getInfo(id);
        String orderBy = "desc";
        if (PREV.equals(method)) {
            result.lambda().gt(OrderEntity::getCreatorTime, orderEntity.getCreatorTime());
            orderBy = "";
        } else if (NEXT.equals(method)) {
            result.lambda().lt(OrderEntity::getCreatorTime, orderEntity.getCreatorTime());
        }
        result.lambda().notIn(OrderEntity::getId, orderEntity.getId());
        if (StringUtil.isNotEmpty(orderBy)) {
            result.lambda().orderByDesc(OrderEntity::getCreatorTime);
        }
        List<OrderEntity> data = this.list(result);
        if (data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    @Override
    public OrderInfoVO getInfoVo(String id, String method) throws DataException {
        OrderInfoVO infoModel = null;
        OrderEntity orderEntity = this.getPrevOrNextInfo(id, method);
        if (orderEntity != null) {
            List<UserAllModel> userList = usersApi.getAll().getData();
            List<OrderEntryEntity> orderEntryList = this.getOrderEntryList(orderEntity.getId());
            List<OrderReceivableEntity> orderReceivableList = this.getOrderReceivableList(orderEntity.getId());
            infoModel = JsonUtilEx.getJsonToBeanEx(orderEntity, OrderInfoVO.class);
            if (infoModel.getCreatorUserId() != null) {
                String createId = infoModel.getCreatorUserId();
                UserAllModel user = userList.stream().filter(t -> t.getId().equals(createId)).findFirst().orElse(new UserAllModel());
                infoModel.setCreatorUserId(user.getRealName() + "/" + user.getAccount());
            }
            if (infoModel.getLastModifyUserId() != null) {
                String fyUserId = infoModel.getLastModifyUserId();
                UserAllModel user = userList.stream().filter(t -> t.getId().equals(fyUserId)).findFirst().orElse(new UserAllModel());
                infoModel.setLastModifyUserId(user.getRealName() + "/" + user.getAccount());
            }
            List<OrderInfoOrderEntryModel> orderEntryModels = JsonUtil.getJsonToList(orderEntryList, OrderInfoOrderEntryModel.class);
            infoModel.setGoodsList(orderEntryModels);
            List<OrderInfoOrderReceivableModel> orderReceivableModels = JsonUtil.getJsonToList(orderReceivableList, OrderInfoOrderReceivableModel.class);
            infoModel.setCollectionPlanList(orderReceivableModels);
        }
        return infoModel;
    }

    @Override
    public OrderEntity getInfo(String id) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(OrderEntity entity) {
        QueryWrapper<OrderEntity> orderWrapper = new QueryWrapper<>();
        orderWrapper.lambda().eq(OrderEntity::getId, entity.getId());
        this.remove(orderWrapper);
        QueryWrapper<OrderEntryEntity> entryWrapper = new QueryWrapper<>();
        entryWrapper.lambda().eq(OrderEntryEntity::getOrderId, entity.getId());
        orderEntryService.remove(entryWrapper);
        QueryWrapper<OrderReceivableEntity> receivableWrapper = new QueryWrapper<>();
        receivableWrapper.lambda().eq(OrderReceivableEntity::getOrderId, entity.getId());
        orderReceivableService.remove(receivableWrapper);
        fileManageUtil.deleteFile(JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class));
    }

    @Override
    public void create(OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList, OrderForm orderForm) throws WorkFlowException {
        try {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            entity.setEnabledMark(1);
            for (int i = 0; i < orderEntryList.size(); i++) {
                orderEntryList.get(i).setId(RandomUtil.uuId());
                orderEntryList.get(i).setOrderId(entity.getId());
                orderEntryList.get(i).setSortCode(Long.parseLong(i + ""));
                orderEntryService.save(orderEntryList.get(i));
            }
            for (int i = 0; i < orderReceivableList.size(); i++) {
                orderReceivableList.get(i).setId(RandomUtil.uuId());
                orderReceivableList.get(i).setOrderId(entity.getId());
                orderReceivableList.get(i).setSortCode(Long.parseLong(i + ""));
                orderReceivableService.save(orderReceivableList.get(i));
            }
            billRuleApi.useBillNumber("OrderNumber");
            if (FlowStatusEnum.submit.getMessage().equals(orderForm.getStatus())) {
                String flowModuleMark = FlowModuleEnum.CRM_Order.getMessage();
                FlowEngineEntity flowEngineEntity = flowEngineService.getInfoByEnCode(flowModuleMark);
                entity.setCurrentState(FlowTaskStatusEnum.Handle.getCode());
                entity.setLastModifyTime(new Date());
                entity.setLastModifyUserId(userProvider.get().getUserId());
                flowSubmit(entity.getId(), flowEngineEntity, orderForm.getFreeApproverUserId(), entity);
            }
            fileManageUtil.createFile(JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class));
            this.save(entity);
        } catch (WorkFlowException e) {
            //手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new WorkFlowException(e.getMessage());
        }
    }

    @Override
    public boolean update(String id, OrderEntity entity, List<OrderEntryEntity> orderEntryList, List<OrderReceivableEntity> orderReceivableList, OrderForm orderForm) throws WorkFlowException {
        //删除原本的文件
        OrderEntity deEntity = getInfo(id);
        List<FileModel> list1 = JsonUtil.getJsonToList(deEntity.getFileJson(), FileModel.class);
        for (FileModel model : list1) {
            model.setFileType("delete");
        }
        fileManageUtil.updateFile(list1);
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        QueryWrapper<OrderEntryEntity> entryWrapper = new QueryWrapper<>();
        entryWrapper.lambda().eq(OrderEntryEntity::getOrderId, entity.getId());
        orderEntryService.remove(entryWrapper);
        QueryWrapper<OrderReceivableEntity> receivableWrapper = new QueryWrapper<>();
        receivableWrapper.lambda().eq(OrderReceivableEntity::getOrderId, entity.getId());
        orderReceivableService.remove(receivableWrapper);
        for (int i = 0; i < orderEntryList.size(); i++) {
            orderEntryList.get(i).setId(RandomUtil.uuId());
            orderEntryList.get(i).setOrderId(entity.getId());
            orderEntryList.get(i).setSortCode(Long.parseLong(i + ""));
            orderEntryService.save(orderEntryList.get(i));
        }
        for (int i = 0; i < orderReceivableList.size(); i++) {
            orderReceivableList.get(i).setId(RandomUtil.uuId());
            orderReceivableList.get(i).setOrderId(entity.getId());
            orderReceivableList.get(i).setSortCode(Long.parseLong(i + ""));
            orderReceivableService.save(orderReceivableList.get(i));
        }
        //添加新的文件
        List<FileModel> list2 = JsonUtil.getJsonToList(entity.getFileJson(), FileModel.class);
        for (FileModel model : list2) {
            model.setFileType("add");
        }
        fileManageUtil.updateFile(list2);
        if (FlowStatusEnum.submit.getMessage().equals(orderForm.getStatus())) {
            String flowModuleMark = FlowModuleEnum.CRM_Order.getMessage();
            FlowEngineEntity flowEngineEntity = flowEngineService.getInfoByEnCode(flowModuleMark);
            entity.setCurrentState(FlowTaskStatusEnum.Handle.getCode());
            entity.setLastModifyTime(new Date());
            entity.setLastModifyUserId(userProvider.get().getUserId());
            flowSubmit(id, flowEngineEntity, orderForm.getFreeApproverUserId(), entity);
        }
        boolean flag = this.updateById(entity);
        return flag;
    }

    @Override
    public void flowSubmit(String id, FlowEngineEntity flowEngineEntity, String freeApproverUserId, OrderEntity orderEntity) throws WorkFlowException {
        UserInfo userInfo = userProvider.get();
        flowTaskService.submit(id, flowEngineEntity.getId(), userInfo.getUserName() + "的订单示例" , 1, orderEntity.getOrderCode(), orderEntity, freeApproverUserId);
    }

    @Override
    public void flowRevoke(FlowTaskEntity flowTaskEntity, FlowHandleModel flowHandleModel) throws WorkFlowException {
        OrderEntity orderEntity = getInfo(flowTaskEntity.getProcessId());
        orderEntity.setCurrentState(FlowTaskStatusEnum.Revoke.getCode());
        orderEntity.setLastModifyTime(new Date());
        this.updateById(orderEntity);
        List<FlowTaskNodeEntity> flowTaskNodeEntityList = flowTaskNodeService.getList(flowTaskEntity.getId());
        FlowTaskNodeEntity flowTaskNodeEntity = flowTaskNodeEntityList.stream().filter(m -> "2".equals(String.valueOf(m.getSortCode()))).findFirst().get();
        if (flowTaskNodeEntity.getCompletion() > 0) {
            throw new WorkFlowException("当前流程被处理，无法撤回流程");
        } else {
            flowTaskService.revoke(flowTaskEntity, flowHandleModel);
        }
    }

    @Override
    public void flowHandleEvent(FlowHandleEventEnum flowHandleEvent, FlowTaskEntity flowTaskEntity) {
        OrderEntity orderEntity = this.getInfo(flowTaskEntity.getProcessId());
        if (orderEntity != null) {
            orderEntity.setCurrentState(flowTaskEntity.getStatus());
            this.updateById(orderEntity);
        }
    }

    @Override
    public void data(String id, String data) {
        OrderForm orderForm = JsonUtil.getJsonToBean(data, OrderForm.class);
        OrderEntity entity = JsonUtil.getJsonToBean(orderForm, OrderEntity.class);
        List<OrderEntryEntity> orderEntryList = JsonUtil.getJsonToList(orderForm.getGoodsList(), OrderEntryEntity.class);
        List<OrderReceivableEntity> orderReceivableList = JsonUtil.getJsonToList(orderForm.getCollectionPlanList(), OrderReceivableEntity.class);
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        QueryWrapper<OrderEntryEntity> entryWrapper = new QueryWrapper<>();
        entryWrapper.lambda().eq(OrderEntryEntity::getOrderId, entity.getId());
        orderEntryService.remove(entryWrapper);
        QueryWrapper<OrderReceivableEntity> receivableWrapper = new QueryWrapper<>();
        receivableWrapper.lambda().eq(OrderReceivableEntity::getOrderId, entity.getId());
        orderReceivableService.remove(receivableWrapper);
        for (int i = 0; i < orderEntryList.size(); i++) {
            orderEntryList.get(i).setId(RandomUtil.uuId());
            orderEntryList.get(i).setOrderId(entity.getId());
            orderEntryList.get(i).setSortCode(Long.parseLong(i + ""));
            orderEntryService.save(orderEntryList.get(i));
        }
        for (int i = 0; i < orderReceivableList.size(); i++) {
            orderReceivableList.get(i).setId(RandomUtil.uuId());
            orderReceivableList.get(i).setOrderId(entity.getId());
            orderReceivableList.get(i).setSortCode(Long.parseLong(i + ""));
            orderReceivableService.save(orderReceivableList.get(i));
        }
        this.updateById(entity);
    }


}
