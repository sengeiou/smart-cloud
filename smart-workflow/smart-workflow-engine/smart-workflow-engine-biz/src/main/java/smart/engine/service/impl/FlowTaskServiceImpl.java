package smart.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.DictionaryDataApi;
import smart.base.UserInfo;
import smart.base.entity.DictionaryDataEntity;
import smart.base.model.FormDataModel;
import smart.emnus.DbType;
import smart.engine.entity.*;
import smart.engine.enums.FlowMessageEnum;
import smart.engine.enums.FlowNodeEnum;
import smart.engine.enums.FlowTaskOperatorEnum;
import smart.engine.enums.FlowTaskStatusEnum;
import smart.engine.mapper.FlowTaskMapper;
import smart.engine.model.FlowHandleModel;
import smart.engine.model.flowengine.shuntjson.childnode.ChildNode;
import smart.engine.model.flowengine.shuntjson.childnode.Properties;
import smart.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import smart.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import smart.engine.model.flowengine.shuntjson.nodejson.Custom;
import smart.engine.model.flowengine.shuntjson.nodejson.DateProperties;
import smart.engine.model.flowtask.FlowTableModel;
import smart.engine.model.flowtask.FlowTaskWaitListModel;
import smart.engine.model.flowtask.PaginationFlowTask;
import smart.engine.service.*;
import smart.engine.util.FlowDataUtil;
import smart.engine.util.FlowJsonUtil;
import smart.engine.util.FlowNature;
import smart.exception.WorkFlowException;
import smart.message.NoticeApi;
import smart.message.model.SentMessageModel;
import smart.permission.OrganizeApi;
import smart.permission.PositionApi;
import smart.permission.UserRelationApi;
import smart.permission.UsersApi;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.PositionEntity;
import smart.permission.entity.UserEntity;
import smart.permission.entity.UserRelationEntity;
import smart.permission.model.user.UserAllModel;
import smart.util.*;
import smart.util.DataSourceUtil;
import smart.util.UserProvider;
import smart.util.type.SortType;
import smart.util.wxutil.HttpUtil;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.model.fields.config.ConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ????????????
 *
 * @author ???????????????
 * @version V3.1.0
 * @copyright ??????????????????
 * @date 2019???9???27??? ??????9:18
 */
@Slf4j
@Service
public class FlowTaskServiceImpl extends ServiceImpl<FlowTaskMapper, FlowTaskEntity> implements FlowTaskService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private OrganizeApi organizeApi;
    @Autowired
    private PositionApi positionApi;
    @Autowired
    private DictionaryDataApi dictionaryDataApi;
    @Autowired
    private NoticeApi noticeApi;
    @Autowired
    private FlowDelegateService flowDelegateService;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    private FlowTaskOperatorRecordService flowTaskOperatorRecordService;
    @Autowired
    private FlowTaskCirculateService flowTaskCirculateService;
    @Autowired
    private FlowEngineService flowEngineService;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private UserRelationApi userRelationApi;
    @Autowired
    private FlowDataUtil flowDataUtil;

    /**
     * ??????id
     **/
    private String taskNodeId = "taskNodeId";
    /**
     * ??????
     **/
    private String handleStatus = "handleStatus";
    /**
     * ??????id
     **/
    private String taskId = "taskId";

    @Override
    public List<FlowTaskEntity> getMonitorList(PaginationFlowTask paginationFlowTask) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().gt(FlowTaskEntity::getStatus, 0);
        //??????????????????????????????????????????
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            queryWrapper.lambda().and(
                    t -> t.like(FlowTaskEntity::getEnCode, keyWord)
                            .or().like(FlowTaskEntity::getFullName, keyWord)
            );
        }
        //??????????????????7?????????1?????????3??????????????????
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = new Date(Long.parseLong(startTime));
            Date endTimes = DateUtil.dateAddDays(new Date(Long.parseLong(endTime)), 1);
            queryWrapper.lambda().ge(FlowTaskEntity::getCreatorTime, startTimes).le(FlowTaskEntity::getCreatorTime, endTimes);
        }
        //????????????
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (!StringUtils.isEmpty(flowId)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowId, flowId);
        }
        //????????????
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        if (!StringUtils.isEmpty(flowCategory)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowCategory, flowCategory);
        }
        //????????????
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        if (!StringUtils.isEmpty(creatorUserId)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getCreatorUserId, creatorUserId);
        }
        //??????
        if (SortType.DESC.equals(paginationFlowTask.getSort().toLowerCase())) {
            queryWrapper.lambda().orderByDesc(FlowTaskEntity::getCreatorTime);
        } else {
            queryWrapper.lambda().orderByAsc(FlowTaskEntity::getCreatorTime);
        }
        Page<FlowTaskEntity> page = new Page<>(paginationFlowTask.getCurrentPage(), paginationFlowTask.getPageSize());
        IPage<FlowTaskEntity> flowTaskEntityPage = this.page(page, queryWrapper);
        return paginationFlowTask.setData(flowTaskEntityPage.getRecords(), page.getTotal());
    }

    @Override
    public List<FlowTaskEntity> getLaunchList(PaginationFlowTask paginationFlowTask) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        String userId = userProvider.get().getUserId();
        queryWrapper.lambda().eq(FlowTaskEntity::getCreatorUserId, userId);
        //??????????????????????????????????????????
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            queryWrapper.lambda().and(
                    t -> t.like(FlowTaskEntity::getEnCode, keyWord)
                            .or().like(FlowTaskEntity::getFullName, keyWord)
            );
        }
        //??????????????????7?????????1?????????3??????????????????
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(FlowTaskEntity::getCreatorTime, startTimes).le(FlowTaskEntity::getCreatorTime, endTimes);
        }
        //????????????
        String flowName = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (!StringUtils.isEmpty(flowName)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowId, flowName);
        }
        //????????????
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        if (!StringUtils.isEmpty(flowCategory)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowCategory, flowCategory);
        }
        //??????
        if (SortType.ASC.equals(paginationFlowTask.getSort().toLowerCase())) {
            queryWrapper.lambda().orderByAsc(FlowTaskEntity::getCreatorTime);
        } else {
            queryWrapper.lambda().orderByDesc(FlowTaskEntity::getCreatorTime);
        }
        Page<FlowTaskEntity> page = new Page<>(paginationFlowTask.getCurrentPage(), paginationFlowTask.getPageSize());
        IPage<FlowTaskEntity> flowTaskEntityPage = this.page(page, queryWrapper);
        return paginationFlowTask.setData(flowTaskEntityPage.getRecords(), page.getTotal());
    }

    @Override
    public List<FlowTaskEntity> getWaitList(PaginationFlowTask paginationFlowTask) {
        String userId = userProvider.get().getUserId();
        StringBuilder dbSql = new StringBuilder();
        //?????????????????????
        dbSql.append(" AND (");
        dbSql.append("o.F_HandleId = '" + userId + "' ");
        //????????????
        List<FlowDelegateEntity> flowDelegateList = flowDelegateService.getUser(userId);
        if (flowDelegateList.size() > 0) {
            dbSql.append(" OR ");
            for (int i = 0; i < flowDelegateList.size(); i++) {
                FlowDelegateEntity delegateEntity = flowDelegateList.get(i);
                //????????????
                dbSql.append(" o.F_HandleId = '" + delegateEntity.getCreatorUserId() + "' ");
                if (flowDelegateList.size() - 1 > i) {
                    dbSql.append(" OR ");
                }
            }
            dbSql.append(")");
        } else {
            dbSql.append(")");
        }
        //??????????????????????????????????????????
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            dbSql.append(" AND (t.F_EnCode like '%" + keyWord + "%' or t.F_FullName like '%" + keyWord + "%') ");
        }
        //??????????????????7?????????1?????????3??????????????????
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            if (DbType.ORACLE.getMessage().equals(dataSourceUtils.getDataType().toLowerCase())) {
                String startTimes = DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00";
                String endTimes = DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59";
                dbSql.append(" AND o.F_CreatorTime Between TO_DATE('" + startTimes + "','yyyy-mm-dd HH24:mi:ss') AND TO_DATE('" + endTimes + "','yyyy-mm-dd HH24:mi:ss') ");
            } else {
                String startTimes = DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00";
                String endTimes = DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59";
                dbSql.append(" AND o.F_CreatorTime Between '" + startTimes + "' AND '" + endTimes + "' ");
            }
        }
        //????????????
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (!StringUtils.isEmpty(flowId)) {
            dbSql.append(" AND t.F_FlowId = '" + flowId + "'");
        }
        //????????????
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        if (!StringUtils.isEmpty(flowCategory)) {
            dbSql.append(" AND t.F_FlowCategory = '" + flowCategory + "'");
        }
        //????????????
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        if (!StringUtils.isEmpty(creatorUserId)) {
            dbSql.append(" AND t.F_CreatorUserId = '" + creatorUserId + "'");
        }
        //??????
        StringBuilder orderBy = new StringBuilder();
        if (SortType.DESC.equals(paginationFlowTask.getSort().toLowerCase())) {
            orderBy.append(" Order by F_CreatorTime DESC");
        } else {
            orderBy.append(" Order by F_CreatorTime ASC");
        }
        String sql = dbSql.toString() + " " + orderBy.toString();
        List<FlowTaskWaitListModel> data = this.baseMapper.getWaitList(sql);
        List<FlowTaskEntity> result = new LinkedList<>();
        for (FlowTaskWaitListModel model : data) {
            ChildNodeList childNodeModelList = JsonUtil.getJsonToBean(model.getNodePropertyJson(), ChildNodeList.class);
            FlowTaskEntity entity = JsonUtil.getJsonToBean(model, FlowTaskEntity.class);
            boolean delegate = true;
            boolean isuser = model.getHandleId().equals(userId);
            entity.setFullName(!isuser ? entity.getFullName() + "(??????)" : entity.getFullName());
            List<FlowDelegateEntity> flowList = flowDelegateList.stream().filter(t -> t.getFlowId().equals(model.getFlowId())).collect(Collectors.toList());
            //???????????????????????????
            if (!isuser) {
                //?????????????????????????????? true??? flas???
                delegate = flowList.stream().filter(t -> t.getCreatorUserId().equals(model.getHandleId())).count() > 0;
            }
            if (delegate) {
                result.add(entity);
                List<DateProperties> timerAll = childNodeModelList.getTimerAll();
                Date date = new Date();
                boolean del = timerAll.stream().filter(t -> t.getDate().getTime() > date.getTime()).count() > 0;
                if (del) {
                    result.remove(entity);
                }
            }
        }
        //????????????
        return paginationFlowTask.setData(PageUtil.getListPage((int) paginationFlowTask.getCurrentPage(), (int) paginationFlowTask.getPageSize(), result), result.size());
    }

    @Override
    public List<FlowTaskEntity> getTrialList(PaginationFlowTask paginationFlowTask) {
        String userId = userProvider.get().getUserId();
        Map<String, Object> queryParam = new HashMap<>(16);
        StringBuilder dbSql = new StringBuilder();
        //??????????????????????????????????????????
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            dbSql.append(" AND (t.F_EnCode like '%" + keyWord + "%' or t.F_FullName like '%" + keyWord + "%') ");
        }
        //??????????????????7?????????1?????????3??????????????????
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            if (DbType.ORACLE.getMessage().equals(dataSourceUtils.getDataType().toLowerCase())) {
                String startTimes = DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00";
                String endTimes = DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59";
                dbSql.append(" AND r.F_HandleTime Between TO_DATE('" + startTimes + "','yyyy-mm-dd HH24:mi:ss') AND TO_DATE('" + endTimes + "','yyyy-mm-dd HH24:mi:ss') ");
            } else {
                String startTimes = DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00";
                String endTimes = DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59";
                dbSql.append(" AND r.F_HandleTime Between '" + startTimes + "' AND '" + endTimes + "' ");
            }
        }
        //????????????
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (!StringUtils.isEmpty(flowId)) {
            dbSql.append(" AND t.F_FlowId = '" + flowId + "' ");
        }
        //????????????
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        if (!StringUtils.isEmpty(flowCategory)) {
            dbSql.append(" AND t.F_FlowCategory = '" + flowCategory + "' ");
        }
        //????????????
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        if (!StringUtils.isEmpty(creatorUserId)) {
            dbSql.append(" AND t.F_CreatorUserId = '" + creatorUserId + "' ");
        }
        //??????
        if (SortType.DESC.equals(paginationFlowTask.getSort().toLowerCase())) {
            dbSql.append(" Order by F_LastModifyTime DESC");
        } else {
            dbSql.append(" Order by F_LastModifyTime ASC");
        }
        queryParam.put("handleId" , userId);
        queryParam.put("sql" , dbSql.toString());
        List<FlowTaskEntity> data = this.baseMapper.getTrialList(queryParam);
        return paginationFlowTask.setData(PageUtil.getListPage((int) paginationFlowTask.getCurrentPage(), (int) paginationFlowTask.getPageSize(), data), data.size());
    }

    @Override
    public List<FlowTaskEntity> getTrialList() {
        String userId = userProvider.get().getUserId();
        Map<String, Object> queryParam = new HashMap<>(16);
        StringBuilder dbSql = new StringBuilder();
        queryParam.put("handleId" , userId);
        queryParam.put("sql" , dbSql.toString());
        List<FlowTaskEntity> data = this.baseMapper.getTrialList(queryParam);
        return data;
    }


    @Override
    public List<FlowTaskEntity> getWaitList() {
        String userId = userProvider.get().getUserId();
        StringBuilder dbSql = new StringBuilder();
        //?????????????????????
        dbSql.append(" AND (");
        dbSql.append("o.F_HandleId = '" + userId + "' ");
        //????????????
        List<FlowDelegateEntity> flowDelegateList = flowDelegateService.getUser(userId);
        if (flowDelegateList.size() > 0) {
            dbSql.append(" OR ");
            for (int i = 0; i < flowDelegateList.size(); i++) {
                FlowDelegateEntity delegateEntity = flowDelegateList.get(i);
                //????????????
                dbSql.append(" o.F_HandleId = '" + delegateEntity.getCreatorUserId() + "' ");
                if (flowDelegateList.size() - 1 > i) {
                    dbSql.append(" OR ");
                }
            }
            dbSql.append(")");
        } else {
            dbSql.append(")");
        }
        List<FlowTaskWaitListModel> data = this.baseMapper.getWaitList(dbSql.toString());
        //????????????
        List<FlowTaskEntity> result = JsonUtil.getJsonToList(data, FlowTaskEntity.class);
        return result;
    }

    @Override
    public List<FlowTaskEntity> getAllWaitList() {
        StringBuilder dbSql = new StringBuilder();
        List<FlowTaskWaitListModel> data = this.baseMapper.getWaitList(dbSql.toString());
        List<FlowTaskEntity> result = JsonUtil.getJsonToList(data, FlowTaskEntity.class);
        return result;
    }

    @Override
    public List<FlowTaskEntity> getCirculateList(PaginationFlowTask paginationFlowTask) {
        String userId = userProvider.get().getUserId();
        List<UserRelationEntity> list = userRelationApi.getList(userId);
        List<String> userRelationList = list.stream().map(u -> u.getObjectId()).collect(Collectors.toList());
        String[] objectId = (String.join("," , userRelationList) + "," + userId).split(",");
        //????????????
        StringBuilder dbSql = new StringBuilder();
        dbSql.append(" AND (");
        for (int i = 0; i < objectId.length; i++) {
            dbSql.append("c.F_ObjectId = '" + objectId[i] + "'");
            if (objectId.length - 1 > i) {
                dbSql.append(" OR ");
            }
        }
        dbSql.append(")");
        //??????????????????????????????????????????
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (!StringUtils.isEmpty(keyWord)) {
            dbSql.append(" AND (t.F_EnCode like " + " '%" + keyWord + "%' " + " or t.F_FullName like" + " '%" + keyWord + "%') ");
        }
        //??????????????????7?????????1?????????3??????????????????
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            if (DbType.ORACLE.getMessage().equals(dataSourceUtils.getDataType().toLowerCase())) {
                String startTimes = DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00";
                String endTimes = DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59";
                dbSql.append(" AND c.F_CreatorTime Between TO_DATE('" + startTimes + "','yyyy-mm-dd HH24:mi:ss') AND TO_DATE('" + endTimes + "','yyyy-mm-dd HH24:mi:ss') ");
            } else {
                String startTimes = DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00";
                String endTimes = DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59";
                dbSql.append(" AND c.F_CreatorTime Between  '" + startTimes + "' AND '" + endTimes + "' ");
            }
        }
        //????????????
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (!StringUtils.isEmpty(flowId)) {
            dbSql.append(" AND t.F_FlowId = '" + flowId + "'");
        }
        //????????????
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        if (!StringUtils.isEmpty(flowCategory)) {
            dbSql.append(" AND t.F_FlowCategory = '" + flowCategory + "'");
        }
        //????????????
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        if (!StringUtils.isEmpty(creatorUserId)) {
            dbSql.append(" AND t.F_CreatorUserId = '" + creatorUserId + "'");
        }
        //??????
        if (SortType.DESC.equals(paginationFlowTask.getSort().toLowerCase())) {
            dbSql.append(" Order by F_LastModifyTime DESC");
        } else {
            dbSql.append(" Order by F_LastModifyTime ASC");
        }
        List<FlowTaskEntity> data = this.baseMapper.getCirculateList(dbSql.toString());
        return paginationFlowTask.setData(PageUtil.getListPage((int) paginationFlowTask.getCurrentPage(), (int) paginationFlowTask.getPageSize(), data), data.size());
    }

    @Override
    public FlowTaskEntity getInfo(String id) throws WorkFlowException {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.eq(FlowTaskEntity::getId, id)
                        .or().eq(FlowTaskEntity::getProcessId, id)
        );
        FlowTaskEntity entity = this.getOne(queryWrapper);
        if (entity == null) {
            throw new WorkFlowException("?????????????????????");
        }
        return entity;
    }

    @Override
    public FlowTaskEntity getInfoSubmit(String id) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.eq(FlowTaskEntity::getId, id)
                        .or().eq(FlowTaskEntity::getProcessId, id)
        );
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(FlowTaskEntity entity) throws WorkFlowException {
        if (!checkStatus(entity.getStatus())) {
            throw new WorkFlowException("????????????????????????????????????");
        } else {
            this.removeById(entity.getId());
            QueryWrapper<FlowTaskNodeEntity> node = new QueryWrapper<>();
            node.lambda().eq(FlowTaskNodeEntity::getTaskId, entity.getId());
            flowTaskNodeService.remove(node);
            QueryWrapper<FlowTaskOperatorEntity> operator = new QueryWrapper<>();
            operator.lambda().eq(FlowTaskOperatorEntity::getTaskId, entity.getId());
            flowTaskOperatorService.remove(operator);
            QueryWrapper<FlowTaskOperatorRecordEntity> record = new QueryWrapper<>();
            record.lambda().eq(FlowTaskOperatorRecordEntity::getTaskId, entity.getId());
            flowTaskOperatorRecordService.remove(record);
            QueryWrapper<FlowTaskCirculateEntity> circulate = new QueryWrapper<>();
            circulate.lambda().eq(FlowTaskCirculateEntity::getTaskId, entity.getId());
            flowTaskCirculateService.remove(circulate);
        }
    }

    @Override
    public void delete(String[] ids) {
        if (ids.length > 0) {
            QueryWrapper<FlowTaskEntity> task = new QueryWrapper<>();
            task.lambda().in(FlowTaskEntity::getId, ids);
            this.remove(task);
            QueryWrapper<FlowTaskNodeEntity> node = new QueryWrapper<>();
            node.lambda().in(FlowTaskNodeEntity::getTaskId, ids);
            flowTaskNodeService.remove(node);
            QueryWrapper<FlowTaskOperatorEntity> operator = new QueryWrapper<>();
            operator.lambda().in(FlowTaskOperatorEntity::getTaskId, ids);
            flowTaskOperatorService.remove(operator);
            QueryWrapper<FlowTaskOperatorRecordEntity> record = new QueryWrapper<>();
            record.lambda().in(FlowTaskOperatorRecordEntity::getTaskId, ids);
            flowTaskOperatorRecordService.remove(record);
            QueryWrapper<FlowTaskCirculateEntity> circulate = new QueryWrapper<>();
            circulate.lambda().in(FlowTaskCirculateEntity::getTaskId, ids);
            flowTaskCirculateService.remove(circulate);
        }
    }

    @Override
    public void save(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo, String formData) throws WorkFlowException {
        if (id == null) {
            //????????????
            FlowEngineEntity flowEngineEntity = flowEngineService.getInfo(flowId);
            FlowTaskEntity taskEntity = new FlowTaskEntity();
            taskEntity.setId(processId);
            taskEntity.setProcessId(processId);
            taskEntity.setEnCode(billNo);
            taskEntity.setFullName(flowTitle);
            taskEntity.setFlowUrgent(flowUrgent);
            taskEntity.setFlowId(flowEngineEntity.getId());
            taskEntity.setFlowCode(flowEngineEntity.getEnCode());
            taskEntity.setFlowName(flowEngineEntity.getFullName());
            taskEntity.setFlowType(flowEngineEntity.getType());
            taskEntity.setFlowCategory(flowEngineEntity.getCategory());
            taskEntity.setFlowForm(flowEngineEntity.getFormData());
            if (formData != null) {
                taskEntity.setFlowFormContentJson(formData);
            }
            taskEntity.setFlowTemplateJson(flowEngineEntity.getFlowTemplateJson());
            taskEntity.setFlowVersion(flowEngineEntity.getVersion());
            taskEntity.setStatus(FlowTaskStatusEnum.Draft.getCode());
            taskEntity.setCompletion(0);
            taskEntity.setThisStep("??????");
            taskEntity.setCreatorTime(new Date());
            taskEntity.setCreatorUserId(userProvider.get().getUserId());
            this.save(taskEntity);
        } else {
            FlowTaskEntity flowTaskEntity = this.getInfo(processId);
            if (!checkStatus(flowTaskEntity.getStatus())) {
                throw new WorkFlowException("??????????????????????????????????????????");
            } else {
                flowTaskEntity.setFullName(flowTitle);
                flowTaskEntity.setFlowUrgent(flowUrgent);
                if (formData != null) {
                    flowTaskEntity.setFlowFormContentJson(formData);
                }
                this.updateById(flowTaskEntity);
            }
        }
    }

    @Override
    public void save(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo) throws WorkFlowException {
        this.save(id, flowId, processId, flowTitle, flowUrgent, billNo, null);
    }

    @Override
    public void submit(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo, Object formEntity, String freeApproverUserId) throws WorkFlowException {
        try {
            List<UserAllModel> userAllModel = usersApi.getAll().getData();
            UserInfo userInfo = userProvider.get();
            //????????????
            FlowEngineEntity flowEngineEntity = flowEngineService.getInfo(flowId);
            //????????????
            FlowTaskEntity flowTaskEntity = new FlowTaskEntity();
            //????????????
            List<FlowTaskNodeEntity> flowTaskNodeEntityList = new LinkedList<>();
            //????????????
            List<FlowTaskOperatorEntity> flowTaskOperatorEntityList = new ArrayList<>();
            //????????????
            List<FlowTaskCirculateEntity> flowTaskCirculateEntityList = new ArrayList<>();
            //??????????????????
            Map<String, String> jnpfKey = new HashMap<>(16);
            //?????????????????????????????????list
            Map<String, Object> keyList = new HashMap<>(16);
            //???????????????????????????jnpfkey
            if (FlowNature.CUSTOM.equals(flowEngineEntity.getFormType())) {
                FormDataModel formData = JsonUtil.getJsonToBean(flowEngineEntity.getFormData(), FormDataModel.class);
                List<FieLdsModel> fieLdsModelList = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
                tempJson(fieLdsModelList, jnpfKey, keyList);
            }
            boolean flag = id == null;
            if (id == null) {
                //????????????
                flowTaskEntity.setId(processId);
                flowTaskEntity.setProcessId(processId);
                flowTaskEntity.setEnCode(billNo);
                flowTaskEntity.setFullName(flowTitle);
                flowTaskEntity.setFlowUrgent(flowUrgent);
                flowTaskEntity.setFlowId(flowEngineEntity.getId());
                flowTaskEntity.setFlowCode(flowEngineEntity.getEnCode());
                flowTaskEntity.setFlowName(flowEngineEntity.getFullName());
                flowTaskEntity.setFlowType(flowEngineEntity.getType());
                flowTaskEntity.setFlowCategory(flowEngineEntity.getCategory());
                flowTaskEntity.setFlowForm(flowEngineEntity.getFormData());
                flowTaskEntity.setFlowTemplateJson(flowEngineEntity.getFlowTemplateJson());
                flowTaskEntity.setFlowVersion(flowEngineEntity.getVersion());
                flowTaskEntity.setStatus(FlowTaskStatusEnum.Handle.getCode());
                flowTaskEntity.setCompletion(0);
                flowTaskEntity.setStartTime(new Date());
                flowTaskEntity.setCreatorTime(new Date());
                flowTaskEntity.setCreatorUserId(userInfo.getUserId());
                if (formEntity != null) {
                    flowTaskEntity.setFlowFormContentJson(JsonUtilEx.getObjectToString(formEntity));
                }
            } else {
                //????????????
                flowTaskEntity = this.getInfo(id);
                if (!checkStatus(flowTaskEntity.getStatus())) {
                    throw new WorkFlowException("??????????????????????????????????????????");
                }
                flowTaskEntity.setFullName(flowTitle);
                flowTaskEntity.setFlowUrgent(flowUrgent);
                flowTaskEntity.setStatus(FlowTaskStatusEnum.Handle.getCode());
                flowTaskEntity.setStartTime(new Date());
                flowTaskEntity.setFlowForm(flowEngineEntity.getFormData());
                flowTaskEntity.setFlowTemplateJson(flowEngineEntity.getFlowTemplateJson());
                flowTaskEntity.setLastModifyTime(new Date());
                flowTaskEntity.setLastModifyUserId(userInfo.getUserId());
                if (formEntity != null) {
                    flowTaskEntity.setFlowFormContentJson(JsonUtilEx.getObjectToString(formEntity));
                }
            }
            //??????????????????
            if (flag) {
                this.save(flowTaskEntity);
            } else {
                this.updateById(flowTaskEntity);
            }
            //????????????Json
            String formDataJson = flowTaskEntity.getFlowTemplateJson();
            ChildNode childNodeAll = JsonUtil.getJsonToBean(formDataJson, ChildNode.class);
            //??????????????????
            List<ChildNodeList> childNodeListAll = new ArrayList<>();
            List<ConditionList> conditionListAll = new ArrayList<>();
            //???????????????????????????????????????
            FlowJsonUtil.getTemplateAll(childNodeAll, childNodeListAll, conditionListAll);
            ChildNodeList start = new ChildNodeList();
            //????????????
            List<FlowTaskNodeEntity> emptyList = new ArrayList<>();
            List<FlowTaskNodeEntity> timerList = new ArrayList<>();
            for (ChildNodeList childNode : childNodeListAll) {
                FlowTaskNodeEntity flowTaskNodeEntity = new FlowTaskNodeEntity();
                String nodeId = childNode.getCustom().getNodeId();
                String dataJson = flowTaskEntity.getFlowFormContentJson();
                String type = childNode.getCustom().getType();
                flowTaskNodeEntity.setId(RandomUtil.uuId());
                childNode.setTaskNodeId(flowTaskNodeEntity.getId());
                childNode.setTaskId(flowTaskEntity.getId());
                flowTaskNodeEntity.setCreatorTime(new Date());
                flowTaskNodeEntity.setTaskId(flowTaskEntity.getId());
                flowTaskNodeEntity.setNodeCode(nodeId);
                flowTaskNodeEntity.setNodeName(childNode.getProperties().getTitle());
                flowTaskNodeEntity.setNodeType(type);
                flowTaskNodeEntity.setState("-2");
                flowTaskNodeEntity.setNodeUp(childNode.getProperties().getRejectStep());
                flowTaskNodeEntity.setNodeNext(FlowJsonUtil.getNextNode(nodeId, dataJson, childNodeListAll, conditionListAll));
                flowTaskNodeEntity.setNodePropertyJson(JsonUtilEx.getObjectToString(childNode));
                boolean isSstart = "start".equals(childNode.getCustom().getType());
                flowTaskNodeEntity.setCompletion(isSstart ? 1 : 0);
                if (isSstart) {
                    childNode.getProperties().setApproverPos(new String[]{});
                    childNode.getProperties().setApprovers(new String[]{});
                    childNode.getProperties().setCirculatePosition(new String[]{});
                    childNode.getProperties().setCirculateUser(new String[]{});
                    childNode.getProperties().setHasEndfunc(childNode.getProperties().getHasEndfunc() != null ? childNode.getProperties().getHasEndfunc() : false);
                    childNode.getProperties().setHasInitfunc(childNode.getProperties().getHasInitfunc() != null ? childNode.getProperties().getHasInitfunc() : false);
                    start = childNode;
                    flowTaskNodeEntity.setNodeName("??????");
                    flowTaskNodeEntity.setNodePropertyJson(JsonUtilEx.getObjectToString(childNode));
                }
                flowTaskNodeEntityList.add(flowTaskNodeEntity);
                if ("empty".equals(type)) {
                    emptyList.add(flowTaskNodeEntity);
                }
                if ("timer".equals(type)) {
                    timerList.add(flowTaskNodeEntity);
                }
            }
            //??????empty??????????????????????????????
            for (FlowTaskNodeEntity empty : emptyList) {
                List<FlowTaskNodeEntity> noxtEmptyList = flowTaskNodeEntityList.stream().filter(t -> t.getNodeNext().contains(empty.getNodeCode())).collect(Collectors.toList());
                for (FlowTaskNodeEntity entity : noxtEmptyList) {
                    entity.setNodeNext(empty.getNodeNext());
                }
            }
            //??????timer??????????????????????????????
            for (FlowTaskNodeEntity timer : timerList) {
                //?????????timer???????????????
                ChildNodeList timerlList = JsonUtil.getJsonToBean(timer.getNodePropertyJson(), ChildNodeList.class);
                DateProperties timers = timerlList.getTimer();
                timers.setNodeId(timer.getNodeCode());
                timers.setTime(true);
                List<FlowTaskNodeEntity> upEmptyList = flowTaskNodeEntityList.stream().filter(t -> t.getNodeNext().contains(timer.getNodeCode())).collect(Collectors.toList());
                for (FlowTaskNodeEntity entity : upEmptyList) {
                    //??????????????????timer?????????
                    ChildNodeList modelList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                    modelList.setTimer(timers);
                    entity.setNodeNext(timer.getNodeNext());
                    entity.setNodePropertyJson(JsonUtilEx.getObjectToString(modelList));
                }
            }
            flowTaskNodeService.create(flowTaskNodeEntityList);
            //?????????????????????
            FlowTaskNodeEntity startNode = flowTaskNodeEntityList.stream().filter(t -> "start".equals(t.getNodeType())).findFirst().get();
            ChildNodeList timerNode = JsonUtil.getJsonToBean(startNode.getNodePropertyJson(), ChildNodeList.class);
            String[] fNodeNext = String.valueOf(startNode.getNodeNext()).split(",");
            List<String> stepId = new ArrayList<>();
            List<String> stepTitle = new ArrayList<>();
            List<String> progress = new ArrayList<>();
            for (String nextNode : fNodeNext) {
                List<ChildNodeList> nextNodeList = childNodeListAll.stream().filter(t -> t.getCustom().getNodeId().contains(nextNode)).collect(Collectors.toList());
                List<FlowTaskOperatorEntity> nextList = new ArrayList<>();
                for (ChildNodeList nextNodeModel : nextNodeList) {
                    Properties properties = nextNodeModel.getProperties();
                    Custom custom = nextNodeModel.getCustom();
                    String type = String.valueOf(properties.getAssigneeType());
                    //???????????????????????????????????????,???????????????????????????
                    DateProperties timer = timerNode.getTimer();
                    Date date = new Date();
                    if (timer.getTime()) {
                        //???????????????
                        FlowTaskNodeEntity entity = flowTaskNodeEntityList.stream().filter(t -> t.getNodeCode().equals(nextNode)).findFirst().get();
                        ChildNodeList entityTimer = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                        date = DateUtil.dateAddDays(date, timer.getDay());
                        date = DateUtil.dateAddHours(date, timer.getHour());
                        date = DateUtil.dateAddMinutes(date, timer.getMinute());
                        date = DateUtil.dateAddSeconds(date, timer.getSecond());
                        timer.setDate(date);
                        List<DateProperties> timerAll = new ArrayList<>();
                        timerAll.add(timer);
                        entityTimer.setTimerAll(timerAll);
                        entity.setNodePropertyJson(JsonUtilEx.getObjectToString(entityTimer));
                        flowTaskNodeService.update(entity);
                    }
                    if (type.equals(String.valueOf(FlowTaskOperatorEnum.LaunchCharge.getCode()))) {
                        //?????????????????????
                        UserEntity info = usersApi.getInfoById(userInfo.getUserId());
                        //???????????????????????????
                        if (StringUtil.isEmpty(info.getManagerId())) {
                            throw new WorkFlowException("???????????????????????????");
                        }
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(FlowTaskOperatorEnum.LaunchCharge.getCode());
                        flowTask.setHandleId(info.getManagerId());
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(nextNodeModel.getTaskNodeId());
                        flowTask.setTaskId(nextNodeModel.getTaskId());
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        nextList.add(flowTask);
                    }
                    //???????????????????????????
                    if (type.equals(String.valueOf((FlowTaskOperatorEnum.DepartmentCharge.getCode())))) {
                        OrganizeEntity organizeEntity = organizeApi.getById(userInfo.getDepartmentId());
                        if (StringUtil.isEmpty(organizeEntity.getManager())) {
                            throw new WorkFlowException("????????????????????????");
                        }
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(FlowTaskOperatorEnum.DepartmentCharge.getCode());
                        flowTask.setHandleId(organizeEntity.getManager());
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(nextNodeModel.getTaskNodeId());
                        flowTask.setTaskId(nextNodeModel.getTaskId());
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        nextList.add(flowTask);
                    }
                    //???????????????????????????
                    if (type.equals(String.valueOf(FlowTaskOperatorEnum.InitiatorMe.getCode()))) {
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(FlowTaskOperatorEnum.InitiatorMe.getCode());
                        flowTask.setHandleId(flowTaskEntity.getCreatorUserId());
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(nextNodeModel.getTaskNodeId());
                        flowTask.setTaskId(nextNodeModel.getTaskId());
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        nextList.add(flowTask);
                    }
                    //??????????????????????????????
                    if (type.equals(String.valueOf(FlowTaskOperatorEnum.FreeApprover.getCode()))) {
                        if (!StringUtils.isEmpty(freeApproverUserId)) {
                            FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setHandleType(FlowTaskOperatorEnum.FreeApprover.getCode());
                            flowTask.setHandleId(freeApproverUserId);
                            flowTask.setNodeCode(custom.getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(nextNodeModel.getTaskNodeId());
                            flowTask.setTaskId(nextNodeModel.getTaskId());
                            flowTask.setCreatorTime(date);
                            flowTask.setCompletion(0);
                            flowTask.setState(FlowNodeEnum.Process.getCode());
                            flowTask.setType(type);
                            nextList.add(flowTask);
                        }
                    }
                    //???????????????????????????
                    for (String userId : properties.getApprovers()) {
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(FlowTaskOperatorEnum.AppointUser.getCode());
                        flowTask.setHandleId(userId);
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(nextNodeModel.getTaskNodeId());
                        flowTask.setTaskId(nextNodeModel.getTaskId());
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        nextList.add(flowTask);
                    }
                    //???????????????????????????
                    if (properties.getApproverPos().length > 0) {
                        List<UserRelationEntity> listByObjectIdAll = userRelationApi.getObjectList(String.join("," , properties.getApproverPos()));
                        List<String> userPosition = listByObjectIdAll.stream().map(t -> t.getUserId()).collect(Collectors.toList());
                        getApproverUser(userPosition, flowTaskOperatorEntityList, nextNodeModel);
                    }
                    //????????????
                    for (String positionId : properties.getCirculatePosition()) {
                        FlowTaskCirculateEntity flowTask = new FlowTaskCirculateEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setObjectType(FlowTaskOperatorEnum.AppointPosition.getCode());
                        flowTask.setObjectId(positionId);
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(nextNodeModel.getTaskNodeId());
                        flowTask.setTaskId(nextNodeModel.getTaskId());
                        flowTask.setCreatorTime(date);
                        flowTaskCirculateEntityList.add(flowTask);
                    }
                    for (String userId : properties.getCirculateUser()) {
                        //????????????
                        List<String> positionIds = userAllModel.stream().filter(t -> t.getId().contains(userId)).map(t -> t.getPositionId()).collect(Collectors.toList());
                        if (getCirculateUser(properties.getCirculatePosition(), positionIds)) {
                            FlowTaskCirculateEntity flowTask = new FlowTaskCirculateEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setObjectType(FlowTaskOperatorEnum.AppointUser.getCode());
                            flowTask.setObjectId(userId);
                            flowTask.setNodeCode(custom.getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(nextNodeModel.getTaskNodeId());
                            flowTask.setTaskId(nextNodeModel.getTaskId());
                            flowTask.setCreatorTime(date);
                            flowTaskCirculateEntityList.add(flowTask);
                        }
                    }
                    stepId.add(custom.getNodeId());
                    stepTitle.add(properties.getTitle());
                    if (properties.getProgress() != null) {
                        progress.add(properties.getProgress());
                    }
                }
                flowTaskOperatorEntityList.addAll(nextList);
            }
            //?????????
            flowTaskOperatorService.create(flowTaskOperatorEntityList);
            //?????????
            flowTaskCirculateService.create(flowTaskCirculateEntityList);
            //????????????
            flowTaskEntity.setThisStepId(String.join("," , stepId));
            flowTaskEntity.setThisStep(String.join("," , stepTitle));
            //????????????
            Collections.sort(progress);
            flowTaskEntity.setCompletion(progress.size() > 0 ? Integer.valueOf(progress.get(0)) : null);
            this.updateById(flowTaskEntity);
            //????????????
            FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity = new FlowTaskOperatorRecordEntity();
            flowTaskOperatorRecordEntity.setHandleId(userInfo.getUserId());
            flowTaskOperatorRecordEntity.setHandleTime(new Date());
            flowTaskOperatorRecordEntity.setHandleStatus(2);
            flowTaskOperatorRecordEntity.setNodeName("??????");
            flowTaskOperatorRecordEntity.setTaskId(flowTaskEntity.getId());
            flowTaskOperatorRecordService.create(flowTaskOperatorRecordEntity);
            //????????????
            Properties properties = start.getProperties();
            boolean func = properties.getHasInitfunc() != null ? properties.getHasInitfunc() : false;
            if (func) {
                String faceUrl = properties.getInitInterfaceUrl() + "?" + taskNodeId + "=" + startNode.getId() + "&" + taskId + "=" + startNode.getTaskId();
                System.out.println("??????????????????:" + faceUrl);
                HttpUtil.httpRequestAll(faceUrl, "GET" , null);
            }
            //????????????
            Map<String, Object> message = new HashMap<>(16);
            message.put("type" , FlowMessageEnum.wait.getCode());
            message.put("id" , processId);
            //????????????
            messagePush(flowTaskOperatorEntityList, flowTitle + "????????????" , JsonUtilEx.getObjectToString(message));
            //????????????
            message.put("type" , FlowMessageEnum.circulate.getCode());
            messagePushCirculate(flowTaskCirculateEntityList, flowTaskEntity.getFullName() + "????????????" , JsonUtilEx.getObjectToString(message));
        } catch (WorkFlowException e) {
            //??????????????????
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new WorkFlowException(e.getMessage());
        }
    }

    @Override
    public void submit(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo, Object formEntity) throws WorkFlowException {
        this.submit(processId, flowId, flowTitle, flowUrgent, billNo, formEntity, null);
    }

    @Override
    public void submit(String processId, String flowId, String flowTitle, int flowUrgent, String billNo, Object formEntity) throws WorkFlowException {
        this.submit(processId, flowId, flowTitle, flowUrgent, billNo, formEntity, null);
    }

    @Override
    public void submit(String processId, String flowId, String flowTitle, int flowUrgent, String billNo, Object formEntity, String freeApproverUserId) throws WorkFlowException {
        FlowTaskEntity flowTaskEntity = this.getInfoSubmit(processId);
        if (flowTaskEntity != null) {
            this.submit(flowTaskEntity.getId(), flowId, processId, flowTitle, flowUrgent, billNo, formEntity, freeApproverUserId);
        } else {
            this.submit(null, flowId, processId, flowTitle, flowUrgent, billNo, formEntity, freeApproverUserId);
        }
    }

    @Override
    public void revoke(FlowTaskEntity flowTaskEntity, FlowHandleModel flowHandleModel) {
        List<FlowTaskNodeEntity> list = flowTaskNodeService.getList(flowTaskEntity.getId());
        FlowTaskNodeEntity start = list.stream().filter(t -> "start".equals(String.valueOf(t.getNodeType()))).findFirst().orElse(null);
        //????????????
        flowTaskNodeService.deleteByTaskId(flowTaskEntity.getId());
        //????????????
        flowTaskOperatorService.deleteByTaskId(flowTaskEntity.getId());
        //????????????
        flowTaskCirculateService.deleteByTaskId(flowTaskEntity.getId());
        //????????????
        flowTaskEntity.setThisStepId("");
        flowTaskEntity.setThisStep("??????");
        flowTaskEntity.setCompletion(0);
        flowTaskEntity.setStatus(FlowTaskStatusEnum.Revoke.getCode());
        flowTaskEntity.setStartTime(null);
        flowTaskEntity.setEndTime(null);
        this.updateById(flowTaskEntity);
        //????????????
        UserInfo userInfo = userProvider.get();
        FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity = new FlowTaskOperatorRecordEntity();
        flowTaskOperatorRecordEntity.setHandleOpinion(flowHandleModel.getHandleOpinion());
        flowTaskOperatorRecordEntity.setHandleId(userInfo.getUserId());
        flowTaskOperatorRecordEntity.setHandleTime(new Date());
        flowTaskOperatorRecordEntity.setHandleStatus(3);
        flowTaskOperatorRecordEntity.setNodeName("??????");
        flowTaskOperatorRecordEntity.setTaskId(flowTaskEntity.getId());
        flowTaskOperatorRecordService.create(flowTaskOperatorRecordEntity);
        //??????????????????
        if (start != null) {
            ChildNodeList childNode = JsonUtil.getJsonToBean(start.getNodePropertyJson(), ChildNodeList.class);
            Properties properties = childNode.getProperties();
            boolean func = properties.getHasFlowRecallFunc() != null ? properties.getHasFlowRecallFunc() : false;
            if (func) {
                String faceUrl = properties.getFlowRecallInterfaceUrl() + "?" + handleStatus + "=" + flowTaskOperatorRecordEntity.getHandleStatus()
                        + "&" + taskId + "=" + flowTaskEntity.getId();
                System.out.println("??????????????????:" + faceUrl);
                HttpUtil.httpRequestAll(faceUrl, "GET" , null);
            }
        }
    }

    @Override
    public void audit(FlowTaskEntity flowTaskEntity, FlowTaskOperatorEntity flowTaskOperatorEntity, FlowHandleModel flowHandleModel) throws WorkFlowException {
        try {
            String freeApproverUserId = flowHandleModel.getFreeApproverUserId();
            UserInfo userInfo = userProvider.get();
            List<UserAllModel> userAllModel = usersApi.getAll().getData();
            //????????????
            List<FlowTaskNodeEntity> flowTaskNodeAll = flowTaskNodeService.getList(flowTaskEntity.getId());
            List<FlowTaskNodeEntity> flowTaskNodeEntityList = flowTaskNodeAll.stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            //????????????
            FlowTaskOperatorEntity operator = flowTaskOperatorEntity;
            FlowTaskNodeEntity flowTaskNodeEntity = flowTaskNodeEntityList.stream().filter(m -> m.getId().equals(operator.getTaskNodeId())).findFirst().get();
            //??????????????????
            ChildNodeList nodeModel = JsonUtil.getJsonToBean(flowTaskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
            String assigneType = String.valueOf(nodeModel.getProperties().getAssigneeType());
            //??????????????????
            List<FlowTaskOperatorEntity> entityList = flowTaskOperatorService.getList(flowTaskNodeEntity.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            List<FlowTaskOperatorEntity> thisFlowTaskOperatorEntityList = entityList.stream().filter(m -> m.getTaskNodeId().equals(flowTaskNodeEntity.getId())).collect(Collectors.toList());
            //????????????
            FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity = new FlowTaskOperatorRecordEntity();
            flowTaskOperatorRecordEntity.setHandleOpinion(flowHandleModel.getHandleOpinion());
            flowTaskOperatorRecordEntity.setHandleId(userInfo.getUserId());
            flowTaskOperatorRecordEntity.setHandleTime(DateUtil.getNowDate());
            flowTaskOperatorRecordEntity.setHandleStatus(1);
            flowTaskOperatorRecordEntity.setNodeCode(flowTaskNodeEntity.getNodeCode());
            flowTaskOperatorRecordEntity.setNodeName(flowTaskNodeEntity.getNodeName());
            flowTaskOperatorRecordEntity.setTaskOperatorId(flowTaskOperatorEntity.getId());
            flowTaskOperatorRecordEntity.setTaskNodeId(flowTaskNodeEntity.getId());
            flowTaskOperatorRecordEntity.setTaskId(flowTaskEntity.getId());
            flowTaskOperatorRecordService.create(flowTaskOperatorRecordEntity);
            //???????????????????????????
            if (FlowTaskOperatorEnum.Fixedapprover.getCode().equals(assigneType)) {
                flowTaskOperatorService.update(flowTaskNodeEntity.getId(), FlowTaskOperatorEnum.Fixedapprover.getCode());
            }
            flowTaskOperatorEntity.setCompletion(1);
            flowTaskOperatorEntity.setHandleStatus(1);
            flowTaskOperatorEntity.setHandleTime(DateUtil.getNowDate());
            flowTaskOperatorService.update(flowTaskOperatorEntity);
            //??????????????????id
            List<String> userIdListAll = new ArrayList<>();
            //?????????????????????????????????????????????????????????????????????????????????
            if (operator.getHandleId().equals(userInfo.getUserId())) {
                userIdListAll = flowDelegateService.getUser(userInfo.getUserId()).stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList());
            }
            //????????????????????????
            if (FlowTaskOperatorEnum.FixedJointlyApprover.getCode().equals(assigneType)) {
                flowTaskOperatorService.update(flowTaskNodeEntity.getId(), userIdListAll);
            }
            //?????????????????????
            String[] fNodeNext = String.valueOf(flowTaskNodeEntity.getNodeNext()).split(",");
            //????????????
            boolean isUpdateflowTaskNode = true;
            //??????????????????
            boolean isFixedJointly = true;
            //??????????????????
            boolean freeApprover = FlowTaskOperatorEnum.FreeApprover.getCode().equals(assigneType) && !StringUtils.isEmpty(freeApproverUserId);
            List<String> operatorList = thisFlowTaskOperatorEntityList.stream().filter(t -> !t.getHandleId().equals(userInfo.getUserId()) && t.getCompletion() == 0).map(t -> t.getHandleId()).collect(Collectors.toList());
            List<String> userIdAll = userIdListAll;
            boolean fixedJointlyApprover = operatorList.stream().filter(t -> !userIdAll.contains(t)).count() > 0;
            if (freeApprover) {
                isUpdateflowTaskNode = false;
                //???????????????????????????????????????
                fNodeNext = flowTaskNodeEntity.getNodeCode().split(",");
            } else if (FlowTaskOperatorEnum.FixedJointlyApprover.getCode().equals(assigneType) && fixedJointlyApprover) {
                isUpdateflowTaskNode = false;
                isFixedJointly = false;
            }
            //????????????id
            Set<String> stepId = new HashSet<>(16);
            List<String> progress = new ArrayList<>();
            List<String> interflowId = new ArrayList<>();
            //????????????
            List<FlowTaskOperatorEntity> flowTaskOperatorEntityList = new ArrayList<>();
            //????????????
            List<FlowTaskCirculateEntity> flowTaskCirculateEntityList = new ArrayList<>();
            //??????????????????????????????
            boolean isNext = true;
            //????????????????????????
            boolean endround = Arrays.asList(fNodeNext).contains("end");
            //????????????
            ChildNodeList endChildNode = new ChildNodeList();
            //????????????????????????????????????
            //?????????????????????
            DateProperties timer = nodeModel.getTimer();
            //????????????????????????
            List<DateProperties> interTimerAll = new ArrayList<>();
            boolean isTimer = timer.getTime();
            String isNextId = flowTaskNodeEntity.getNodeNext();
            Date date = new Date();
            if (isTimer) {
                //??????????????????????????????
                date = DateUtil.dateAddDays(date, timer.getDay());
                date = DateUtil.dateAddHours(date, timer.getHour());
                date = DateUtil.dateAddMinutes(date, timer.getMinute());
                date = DateUtil.dateAddSeconds(date, timer.getSecond());
                timer.setDate(date);
                List<DateProperties> timerAll = new ArrayList<>();
                timerAll.add(timer);
                interTimerAll.add(timer);
                ChildNodeList entityTimer = JsonUtil.getJsonToBean(flowTaskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
                entityTimer.setTimerAll(timerAll);
                flowTaskNodeEntity.setNodePropertyJson(JsonUtilEx.getObjectToString(entityTimer));
            }
            //????????????????????????
            List<FlowTaskNodeEntity> freeListAll = new ArrayList<>();
            //??????????????????????????????
            for (String nextNode : fNodeNext) {
                //?????????????????????
                List<String> nextNodeList = new ArrayList<>();
                //?????????????????????????????????
                List<FlowTaskNodeEntity> interflowAll = flowTaskNodeEntityList.stream().filter(t -> String.valueOf(t.getNodeNext()).contains(nextNode) && !t.getNodeCode().equals(flowTaskNodeEntity.getNodeCode()) && t.getCompletion() == 0).collect(Collectors.toList());
                if (interflowAll.size() > 0) {
                    isNext = false;
                    break;
                }
                //??????????????????????????????
                List<FlowTaskNodeEntity> interflow = flowTaskNodeEntityList.stream().filter(t -> String.valueOf(t.getNodeNext()).contains(nextNode) && !t.getNodeCode().equals(flowTaskNodeEntity.getNodeCode()) && t.getCompletion() == 1).collect(Collectors.toList());
                interflowId = interflow.stream().map(t -> t.getNodeCode()).distinct().collect(Collectors.toList());
                //???????????????????????????
                List<FlowTaskNodeEntity> nextList = flowTaskNodeEntityList.stream().filter(t -> String.valueOf(t.getNodeCode()).contains(nextNode)).collect(Collectors.toList());
                //????????????
                List<FlowTaskNodeEntity> nextAll = new ArrayList<>();
                for (FlowTaskNodeEntity entity : nextList) {
                    ChildNodeList modelList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                    Properties properties = modelList.getProperties();
                    String type = String.valueOf(properties.getAssigneeType());
                    nextAll.add(entity);
                    //??????????????????
                    nextNodeList.add(entity.getNodeCode());
                    if (FlowTaskOperatorEnum.FreeApprover.getCode().equals(type) && StringUtils.isEmpty(freeApproverUserId)) {
                        nextAll.remove(entity);
                        freeListAll.add(entity);
                        String[] freeNext = String.valueOf(entity.getNodeNext()).split(",");
                        for (String free : freeNext) {
                            FlowTaskNodeEntity taskNodeEntity = flowTaskNodeEntityList.stream().filter(t -> t.getNodeCode().equals(free)).findFirst().orElse(null);
                            //?????????????????????????????????
                            List<FlowTaskNodeEntity> freeList = flowTaskNodeEntityList.stream().filter(t -> String.valueOf(t.getNodeNext()).contains(free) && !t.getNodeCode().equals(free) && t.getCompletion() == 0).collect(Collectors.toList());
                            if (freeList.size() == 0 && taskNodeEntity != null) {
                                nextAll.add(taskNodeEntity);
                                //??????????????????
                                nextNodeList.remove(entity.getNodeCode());
                                nextNodeList.addAll(freeList.stream().map(t -> t.getNodeCode()).collect(Collectors.toList()));
                            }
                        }
                    }
                }
                //??????????????????
                for (FlowTaskNodeEntity entity : nextAll) {
                    ChildNodeList modelList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                    if ("endround".equals(entity.getNodeType())) {
                        endChildNode = modelList;
                    }
                    Properties properties = modelList.getProperties();
                    Custom custom = modelList.getCustom();
                    String type = String.valueOf(properties.getAssigneeType());
                    String taskId = modelList.getTaskId();
                    String taskNodeId = modelList.getTaskNodeId();
                    //????????????
                    //???????????????????????????
                    UserAllModel createUser = userAllModel.stream().filter(t -> t.getId().equals(flowTaskEntity.getCreatorUserId())).findFirst().get();
                    if (FlowTaskOperatorEnum.LaunchCharge.getCode().equals(type)) {
                        if (StringUtil.isEmpty(createUser.getManagerId())) {
                            throw new WorkFlowException("????????????????????????");
                        }
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.LaunchCharge.getCode()));
                        flowTask.setHandleId(createUser.getManagerId());
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    //???????????????????????????
                    if (FlowTaskOperatorEnum.DepartmentCharge.getCode().equals(type)) {
                        OrganizeEntity organizeEntity = organizeApi.getById(createUser.getDepartmentId());
                        if (StringUtil.isEmpty(organizeEntity.getManager())) {
                            throw new WorkFlowException("????????????????????????");
                        }
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.DepartmentCharge.getCode()));
                        flowTask.setHandleId(organizeEntity.getManager());
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    //???????????????????????????
                    if (FlowTaskOperatorEnum.InitiatorMe.getCode().equals(type)) {
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.InitiatorMe.getCode()));
                        flowTask.setHandleId(flowTaskEntity.getCreatorUserId());
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    //??????????????????????????????
                    if (FlowTaskOperatorEnum.FreeApprover.getCode().equals(type)) {
                        if (!StringUtils.isEmpty(freeApproverUserId)) {
                            FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.FreeApprover.getCode()));
                            flowTask.setHandleId(freeApproverUserId);
                            flowTask.setNodeCode(custom.getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(taskNodeId);
                            flowTask.setTaskId(taskId);
                            flowTask.setCompletion(0);
                            flowTask.setState(FlowNodeEnum.Process.getCode());
                            flowTask.setType(type);
                            flowTaskOperatorEntityList.add(flowTask);
                        }
                    }
                    //???????????????????????????
                    for (String userId : properties.getApprovers()) {
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.AppointUser.getCode()));
                        flowTask.setHandleId(userId);
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(date);
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(type);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    //???????????????????????????
                    if (properties.getApproverPos().length > 0) {
                        List<UserRelationEntity> listByObjectIdAll = userRelationApi.getObjectList(String.join("," , properties.getApproverPos()));
                        List<String> userPosition = listByObjectIdAll.stream().map(t -> t.getUserId()).collect(Collectors.toList());
                        getApproverUser(userPosition, flowTaskOperatorEntityList, modelList);
                    }
                    //????????????
                    //???????????????????????????
                    for (String positionId : properties.getCirculatePosition()) {
                        FlowTaskCirculateEntity flowTask = new FlowTaskCirculateEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setObjectType(String.valueOf(FlowTaskOperatorEnum.AppointPosition.getCode()));
                        flowTask.setObjectId(positionId);
                        flowTask.setNodeCode(custom.getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(date);
                        flowTaskCirculateEntityList.add(flowTask);
                    }
                    //???????????????????????????
                    for (String userId : properties.getCirculateUser()) {
                        //????????????
                        List<String> positionIds = userAllModel.stream().filter(t -> t.getId().contains(userId)).map(t -> t.getPositionId()).collect(Collectors.toList());
                        if (getCirculateUser(properties.getCirculatePosition(), positionIds)) {
                            FlowTaskCirculateEntity flowTask = new FlowTaskCirculateEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setObjectType(String.valueOf(FlowTaskOperatorEnum.AppointUser.getCode()));
                            flowTask.setObjectId(userId);
                            flowTask.setNodeCode(custom.getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(taskNodeId);
                            flowTask.setTaskId(taskId);
                            flowTask.setCreatorTime(date);
                            flowTaskCirculateEntityList.add(flowTask);
                        }
                    }
                    stepId.add(custom.getNodeId());
                    if (properties.getProgress() != null) {
                        progress.add(properties.getProgress());
                    }
                }
                //??????????????????????????????
                if (stepId.size() == 0) {
                    stepId.addAll(nextNodeList);
                }
            }
            //????????????
            if (isUpdateflowTaskNode) {
                flowTaskNodeEntity.setCompletion(1);
                flowTaskNodeService.update(flowTaskNodeEntity);
                //????????????????????????
                for (FlowTaskNodeEntity entity : freeListAll) {
                    entity.setCompletion(1);
                    flowTaskNodeService.update(entity);
                }
                //????????????
                Properties properties = nodeModel.getProperties();
                boolean func = properties.getHasApproverfunc() != null ? properties.getHasApproverfunc() : false;
                if (func) {
                    String faceUrl = properties.getApproverInterfaceUrl() + "?" + taskNodeId + "=" + flowTaskNodeEntity.getId() + "&" +
                            handleStatus + "=" + flowTaskOperatorRecordEntity.getHandleStatus() + "&" + taskId + "=" + flowTaskNodeEntity.getTaskId();
                    System.out.println("??????????????????:" + faceUrl);
                    HttpUtil.httpRequestAll(faceUrl, "GET" , null);
                }
                //?????????????????????????????????
                if (isNext) {
                    String[] stepIdsAll = flowTaskEntity.getThisStepId().split(",");
                    Set<String> id = new HashSet<>(16);
                    List<String> title = new ArrayList<>();
                    //??????????????????
                    for (String ids : stepIdsAll) {
                        if (ids.equals(flowTaskNodeEntity.getNodeCode())) {
                            String stepIdAll = ids.replace(flowTaskNodeEntity.getNodeCode(), String.join("," , stepId));
                            id.add(stepIdAll);
                        } else {
                            boolean flowId = interflowId.stream().filter(t -> t.contains(ids)).count() == 0;
                            if (flowId) {
                                id.add(ids);
                            } else {
                                //????????????????????????????????????????????????
                                FlowTaskNodeEntity interTimeEntity = flowTaskNodeEntityList.stream().filter(m -> m.getNodeCode().equals(ids)).findFirst().get();
                                ChildNodeList interTimeChildNode = JsonUtil.getJsonToBean(interTimeEntity.getNodePropertyJson(), ChildNodeList.class);
                                boolean nextIsTimer = interTimeChildNode.getTimer().getTime();
                                if (nextIsTimer) {
                                    //??????????????????????????????
                                    interTimerAll.addAll(interTimeChildNode.getTimerAll());
                                    isTimer = true;
                                }
                            }
                        }
                    }
                    //??????????????????
                    for (String ids : id) {
                        List<FlowTaskNodeEntity> nameList = flowTaskNodeEntityList.stream().filter(t -> ids.contains(t.getNodeCode())).collect(Collectors.toList());
                        title = nameList.stream().map(t -> t.getNodeName()).collect(Collectors.toList());
                    }
                    flowTaskEntity.setThisStepId(String.join("," , id));
                    flowTaskEntity.setThisStep(String.join("," , title));
                    //??????????????????
                    Collections.sort(progress);
                    flowTaskEntity.setCompletion(progress.size() > 0 ? Integer.valueOf(progress.get(0)) : null);
                    //??????????????????
                    if (endround) {
                        flowTaskEntity.setStatus(FlowTaskStatusEnum.Adopt.getCode());
                        flowTaskEntity.setCompletion(100);
                        flowTaskEntity.setEndTime(DateUtil.getNowDate());
                        flowTaskEntity.setThisStepId("end");
                        flowTaskEntity.setThisStep("??????");
                        //????????????????????????????????????
                        List<String> userId = new ArrayList<>();
                        userId.add(flowTaskEntity.getCreatorUserId());
                        SentMessageModel model = new SentMessageModel();
                        model.setBodyText("");
                        model.setTitle(flowTaskEntity.getFullName() + "??????????????????");
                        model.setToUserIds(userId);
                        noticeApi.sentMessage(model);
                        //????????????
                        Properties endProperties = endChildNode.getProperties();
                        boolean endfunc = endProperties.getHasEndfunc() != null ? endProperties.getHasEndfunc() : false;
                        if (endfunc) {
                            String faceUrl = endProperties.getEndInterfaceUrl() + "?" + taskNodeId + "=" + flowTaskNodeEntity.getId() + "&" +
                                    handleStatus + "=" + flowTaskOperatorRecordEntity.getHandleStatus() + "&" + taskId + "=" + flowTaskNodeEntity.getTaskId();
                            System.out.println("??????????????????:" + faceUrl);
                            HttpUtil.httpRequestAll(faceUrl, "GET" , null);
                        }
                    }
                    //?????????????????????
                    FlowEngineEntity flowentity = flowEngineService.getInfo(flowTaskEntity.getFlowId());
                    if (FlowNature.CUSTOM.equals(flowentity.getFormType())) {
                        List<FlowTableModel> tableModelList = JsonUtil.getJsonToList(flowentity.getTables(), FlowTableModel.class);
                        Object objectData = flowHandleModel.getFormData();
                        if (objectData instanceof Map) {
                            Map<String, Object> formDataAll = (Map<String, Object>) objectData;
                            Map<String, Object> data = JsonUtil.stringToMap(String.valueOf(formDataAll.get("data")));
                            //formTempJson
                            FormDataModel formData = JsonUtil.getJsonToBean(flowTaskEntity.getFlowForm(), FormDataModel.class);
                            List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
                            Map<String, Object> dataAll = flowDataUtil.update(data, list, tableModelList, flowTaskEntity.getProcessId());
                            //??????task?????????
                            flowTaskEntity.setFlowFormContentJson(JsonUtil.getObjectToString(dataAll));
                        }
                    } else if (FlowNature.SYSTEM.equals(flowentity.getFormType())) {
                        String coed = flowHandleModel.getEnCode();
                        Object objectData = flowHandleModel.getFormData();
                        if (objectData instanceof Map) {
                            String data = JsonUtil.getObjectToString(objectData);
                            formData(coed, flowTaskEntity.getId(), data);
                            //??????task?????????
                            flowTaskEntity.setFlowFormContentJson(data);
                        }
                    }
                    this.updateById(flowTaskEntity);
                    //???????????????????????????
                    if (isTimer) {
                        String[] ids = isNextId.split(",");
                        for (String interId : ids) {
                            FlowTaskNodeEntity interTimeEntity = flowTaskNodeEntityList.stream().filter(m -> m.getNodeCode().equals(interId)).findFirst().orElse(null);
                            if (interTimeEntity != null) {
                                ChildNodeList interTimeChildNode = JsonUtil.getJsonToBean(interTimeEntity.getNodePropertyJson(), ChildNodeList.class);
                                interTimeChildNode.setTimerAll(interTimerAll);
                                interTimeEntity.setNodePropertyJson(JsonUtilEx.getObjectToString(interTimeChildNode));
                                flowTaskNodeService.update(interTimeEntity);
                            }
                        }
                    }
                }
            }
            //??????????????????????????????
            if (isFixedJointly && isNext) {
                flowTaskOperatorService.create(flowTaskOperatorEntityList);
            }
            flowTaskCirculateService.create(flowTaskCirculateEntityList);
            //????????????
            if (isUpdateflowTaskNode) {
                //????????????
                Map<String, Object> message = new HashMap<>(16);
                message.put("type" , FlowMessageEnum.wait.getCode());
                message.put("id" , flowTaskEntity.getId());
                //????????????
                messagePush(flowTaskOperatorEntityList, flowTaskEntity.getFullName() + "????????????" , JsonUtilEx.getObjectToString(message));
                //????????????
                message.put("type" , FlowMessageEnum.circulate.getCode());
                messagePushCirculate(flowTaskCirculateEntityList, flowTaskEntity.getFullName() + "????????????" , JsonUtilEx.getObjectToString(message));
                //?????????????????????
                List<String> userId = new ArrayList<>();
                userId.add(flowTaskEntity.getCreatorUserId());
                message.put("type" , FlowMessageEnum.me.getCode());
                SentMessageModel model = new SentMessageModel();
                model.setBodyText(JsonUtil.getObjectToString(message));
                model.setTitle(flowTaskEntity.getFullName() + "??????????????????");
                model.setToUserIds(userId);
                noticeApi.sentMessage(model);
            }
        } catch (SQLException e) {
            //??????????????????
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new WorkFlowException("??????????????????");
        } catch (WorkFlowException work) {
            //??????????????????
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new WorkFlowException(work.getMessage());
        }
    }

    @Override
    public void reject(FlowTaskEntity flowTaskEntity, FlowTaskOperatorEntity flowTaskOperatorEntity, FlowHandleModel flowHandleModel) throws WorkFlowException {
        try {
            UserInfo userInfo = userProvider.get();
            List<UserAllModel> userAllModels = usersApi.getAll().getData();
            UserAllModel userAllModel = userAllModels.stream().filter(t -> t.getId().equals(flowTaskEntity.getCreatorUserId())).findFirst().get();
            //????????????
            List<FlowTaskNodeEntity> flowTaskNodeEntityList = flowTaskNodeService.getList(flowTaskEntity.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            //????????????
            FlowTaskOperatorEntity task = flowTaskOperatorEntity;
            FlowTaskNodeEntity flowTaskNodeEntity = flowTaskNodeEntityList.stream().filter(m -> m.getId().equals(task.getTaskNodeId())).findFirst().get();
            //??????????????????
            ChildNodeList nodeModel = JsonUtil.getJsonToBean(flowTaskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
            String assigneType = nodeModel.getProperties().getAssigneeType();
            //??????????????????
            List<FlowTaskOperatorEntity> thisFlowTaskOperatorEntityList = flowTaskOperatorService.getList(flowTaskNodeEntity.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            //????????????
            List<FlowTaskNodeEntity> upNodeEntity = flowTaskNodeEntityList.stream().filter(m -> String.valueOf(m.getNodeNext()).contains(String.valueOf(flowTaskNodeEntity.getNodeCode()))).collect(Collectors.toList());
            //????????????
            List<FlowTaskOperatorEntity> flowTaskOperatorEntityList = new ArrayList<>();
            //????????????
            FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity = new FlowTaskOperatorRecordEntity();
            flowTaskOperatorRecordEntity.setHandleOpinion(flowHandleModel.getHandleOpinion());
            flowTaskOperatorRecordEntity.setHandleId(userInfo.getUserId());
            flowTaskOperatorRecordEntity.setHandleTime(DateUtil.getNowDate());
            flowTaskOperatorRecordEntity.setHandleStatus(0);
            flowTaskOperatorRecordEntity.setNodeCode(flowTaskNodeEntity.getNodeCode());
            flowTaskOperatorRecordEntity.setNodeName(flowTaskNodeEntity.getNodeName());
            flowTaskOperatorRecordEntity.setTaskOperatorId(flowTaskOperatorEntity.getId());
            flowTaskOperatorRecordEntity.setTaskNodeId(flowTaskNodeEntity.getId());
            flowTaskOperatorRecordEntity.setTaskId(flowTaskEntity.getId());
            flowTaskOperatorRecordService.create(flowTaskOperatorRecordEntity);
            //???????????????????????????
            if (assigneType.equals(String.valueOf(FlowTaskOperatorEnum.Fixedapprover.getCode()))) {
                flowTaskOperatorService.update(flowTaskOperatorEntity.getTaskNodeId(), FlowTaskOperatorEnum.Fixedapprover.getCode());
            }
            //???????????????????????????
            if (assigneType.equals(String.valueOf(FlowTaskOperatorEnum.FixedJointlyApprover.getCode()))) {
                flowTaskOperatorService.update(flowTaskOperatorEntity.getTaskNodeId(), FlowTaskOperatorEnum.FixedJointlyApprover.getCode());
            }
            // ????????????
            if (assigneType.equals(String.valueOf(FlowTaskOperatorEnum.FixedJointlyApprover.getCode()))) {
                flowTaskOperatorEntity = thisFlowTaskOperatorEntityList.stream().filter(t -> t.getHandleId().equals(userInfo.getUserId())).findFirst().get();
            }
            flowTaskOperatorEntity.setCompletion(1);
            flowTaskOperatorEntity.setHandleStatus(0);
            flowTaskOperatorEntity.setHandleTime(DateUtil.getNowDate());
            flowTaskOperatorService.update(flowTaskOperatorEntity);
            //????????????
            flowTaskNodeEntity.setCompletion(-1);
            flowTaskNodeService.update(flowTaskNodeEntity);
            //???????????? true???????????????flase????????????
            boolean isWill = true;
            //??????????????????????????????
            boolean isNode = false;
            //????????????id
            Set<String> stepId = new HashSet<>(16);
            Set<String> stepTitle = new HashSet<>(16);
            List<String> progress = new ArrayList<>();
            //???????????????
            Set<String> rejectId = new HashSet<>(16);
            //????????????
            if (FlowNature.START.equals(flowTaskNodeEntity.getNodeUp())) {
                isWill = false;
            } else if (FlowNature.UP.equals(flowTaskNodeEntity.getNodeUp())) {
                List<FlowTaskNodeEntity> upTaskList = new ArrayList<>();
                long start = upNodeEntity.stream().filter(t -> "start".equals(t.getNodeType())).count();
                long all = start;
                //????????????
                for (FlowTaskNodeEntity taskNode : upNodeEntity) {
                    ChildNodeList modelList = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                    Properties properties = modelList.getProperties();
                    String upType = properties.getAssigneeType();
                    upTaskList.add(taskNode);
                    //??????
                    if (upType.equals(String.valueOf(FlowTaskOperatorEnum.FreeApprover.getCode()))) {
                        upTaskList.remove(taskNode);
                        List<FlowTaskNodeEntity> upFree = flowTaskNodeEntityList.stream().filter(m -> String.valueOf(m.getNodeNext()).contains(String.valueOf(taskNode.getNodeCode()))).collect(Collectors.toList());
                        long count = upFree.stream().filter(t -> "start".equals(t.getNodeType())).count();
                        all += count;
                        upTaskList.addAll(upFree);
                    }
                }
                //??????????????????????????????
                isWill = all == 0;
                if (isWill) {
                    for (FlowTaskNodeEntity entity : upTaskList) {
                        rejectId.add(entity.getNodeCode());
                        ChildNodeList modelList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                        Custom custom = modelList.getCustom();
                        Properties properties = modelList.getProperties();
                        String upType = properties.getAssigneeType();
                        String taskId = modelList.getTaskId();
                        String taskNodeId = modelList.getTaskNodeId();
                        //????????????
                        //???????????????????????????
                        if (upType.equals(String.valueOf(FlowTaskOperatorEnum.LaunchCharge.getCode()))) {
                            FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.LaunchCharge.getCode()));
                            flowTask.setHandleId(userAllModel.getManagerId());
                            flowTask.setNodeCode(modelList.getCustom().getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(taskNodeId);
                            flowTask.setTaskId(taskId);
                            flowTask.setCreatorTime(new Date());
                            flowTask.setCompletion(0);
                            flowTask.setState(FlowNodeEnum.Process.getCode());
                            flowTask.setType(upType);
                            flowTaskOperatorEntityList.add(flowTask);
                        }
                        //???????????????????????????
                        if (upType.equals(String.valueOf((FlowTaskOperatorEnum.DepartmentCharge.getCode())))) {
                            OrganizeEntity organizeEntity = organizeApi.getById(userAllModel.getOrganizeId());
                            if (StringUtil.isEmpty(organizeEntity.getManager())) {
                                throw new WorkFlowException("????????????????????????");
                            }
                            FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.DepartmentCharge.getCode()));
                            flowTask.setHandleId(organizeEntity.getManager());
                            flowTask.setNodeCode(modelList.getCustom().getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(taskNodeId);
                            flowTask.setTaskId(taskId);
                            flowTask.setCreatorTime(new Date());
                            flowTask.setCompletion(0);
                            flowTask.setState(FlowNodeEnum.Process.getCode());
                            flowTask.setType(upType);
                            flowTaskOperatorEntityList.add(flowTask);
                        }
                        //???????????????????????????
                        for (String userId : properties.getApprovers()) {
                            FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.AppointUser.getCode()));
                            flowTask.setHandleId(userId);
                            flowTask.setNodeCode(modelList.getCustom().getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(taskNodeId);
                            flowTask.setTaskId(taskId);
                            flowTask.setCreatorTime(new Date());
                            flowTask.setCompletion(0);
                            flowTask.setState(FlowNodeEnum.Process.getCode());
                            flowTask.setType(upType);
                            flowTaskOperatorEntityList.add(flowTask);
                        }
                        //???????????????????????????
                        if (properties.getApproverPos().length > 0) {
                            List<UserRelationEntity> listByObjectIdAll = userRelationApi.getObjectList(String.join("," , properties.getApproverPos()));
                            List<String> userPosition = listByObjectIdAll.stream().map(t -> t.getUserId()).collect(Collectors.toList());
                            getApproverUser(userPosition, flowTaskOperatorEntityList, modelList);
                        }
                        //???????????????????????????
                        if (upType.equals(String.valueOf(FlowTaskOperatorEnum.InitiatorMe.getCode()))) {
                            FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                            flowTask.setId(RandomUtil.uuId());
                            flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.InitiatorMe.getCode()));
                            flowTask.setHandleId(flowTaskEntity.getCreatorUserId());
                            flowTask.setNodeCode(modelList.getCustom().getNodeId());
                            flowTask.setNodeName(properties.getTitle());
                            flowTask.setTaskNodeId(taskNodeId);
                            flowTask.setTaskId(taskId);
                            flowTask.setCreatorTime(new Date());
                            flowTask.setCompletion(0);
                            flowTask.setState(FlowNodeEnum.Process.getCode());
                            flowTask.setType(upType);
                            flowTaskOperatorEntityList.add(flowTask);
                        }
                        stepId.add(custom.getNodeId());
                        stepTitle.add(properties.getTitle());
                        if (properties.getProgress() != null) {
                            progress.add(properties.getProgress());
                        }
                    }
                }
            } else {
                //????????????
                upNodeEntity = flowTaskNodeEntityList.stream().filter(m -> String.valueOf(m.getNodeCode()).equals(String.valueOf(flowTaskNodeEntity.getNodeUp()))).collect(Collectors.toList());
                //?????????????????????????????????
                isNode = upNodeEntity.size() == 0;
                for (FlowTaskNodeEntity taskNode : upNodeEntity) {
                    rejectId.add(taskNode.getNodeCode());
                    ChildNodeList modelList = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                    Custom custom = modelList.getCustom();
                    Properties properties = modelList.getProperties();
                    String upType = String.valueOf(properties.getAssigneeType());
                    String taskId = modelList.getTaskId();
                    String taskNodeId = modelList.getTaskNodeId();
                    //????????????
                    //???????????????????????????
                    if (upType.equals(String.valueOf(FlowTaskOperatorEnum.LaunchCharge.getCode()))) {
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.LaunchCharge.getCode()));
                        flowTask.setHandleId(userAllModel.getManagerId());
                        flowTask.setNodeCode(modelList.getCustom().getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(new Date());
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(upType);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    //???????????????????????????
                    if (upType.equals(String.valueOf((FlowTaskOperatorEnum.DepartmentCharge.getCode())))) {
                        OrganizeEntity organizeEntity = organizeApi.getById(userAllModel.getOrganizeId());
                        if (StringUtil.isEmpty(organizeEntity.getManager())) {
                            throw new WorkFlowException("????????????????????????");
                        }
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.DepartmentCharge.getCode()));
                        flowTask.setHandleId(organizeEntity.getManager());
                        flowTask.setNodeCode(modelList.getCustom().getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(new Date());
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(upType);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    //???????????????????????????
                    for (String userId : properties.getApprovers()) {
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.AppointUser.getCode()));
                        flowTask.setHandleId(userId);
                        flowTask.setNodeCode(modelList.getCustom().getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(new Date());
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(upType);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    //???????????????????????????
                    if (properties.getApproverPos().length > 0) {
                        List<UserRelationEntity> listByObjectIdAll = userRelationApi.getObjectList(String.join("," , properties.getApproverPos()));
                        List<String> userPosition = listByObjectIdAll.stream().map(t -> t.getUserId()).collect(Collectors.toList());
                        getApproverUser(userPosition, flowTaskOperatorEntityList, modelList);
                    }
                    //???????????????????????????
                    if (upType.equals(String.valueOf(FlowTaskOperatorEnum.InitiatorMe.getCode()))) {
                        FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                        flowTask.setId(RandomUtil.uuId());
                        flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.InitiatorMe.getCode()));
                        flowTask.setHandleId(flowTaskEntity.getCreatorUserId());
                        flowTask.setNodeCode(modelList.getCustom().getNodeId());
                        flowTask.setNodeName(properties.getTitle());
                        flowTask.setTaskNodeId(taskNodeId);
                        flowTask.setTaskId(taskId);
                        flowTask.setCreatorTime(new Date());
                        flowTask.setCompletion(0);
                        flowTask.setState(FlowNodeEnum.Process.getCode());
                        flowTask.setType(upType);
                        flowTaskOperatorEntityList.add(flowTask);
                    }
                    stepId.add(custom.getNodeId());
                    stepTitle.add(properties.getTitle());
                    if (properties.getProgress() != null) {
                        progress.add(properties.getProgress());
                    }
                }
            }
            //???????????????????????????
            if (isNode) {
                throw new WorkFlowException("????????????????????????");
            }
            //????????????
            if (isWill) {
                //???????????????????????????????????????
                Set<String> nodeIdAll = new HashSet<>(16);
                List<String> reTitle = new ArrayList<>();
                for (String nodeId : rejectId) {
                    //???????????????????????????????????????
                    FlowJsonUtil.nextList(flowTaskNodeEntityList, nodeId, nodeIdAll, new String[]{});
                    FlowTaskNodeEntity entity = flowTaskNodeEntityList.stream().filter(t -> t.getNodeCode().equals(nodeId)).findFirst().orElse(null);
                    if (entity != null) {
                        reTitle.add(entity.getNodeName());
                    }
                }
                //?????????????????????????????????
                flowTaskOperatorService.updateReject(flowTaskEntity.getId(), nodeIdAll);
                //??????????????????
                String[] stepIdsAll = flowTaskEntity.getThisStepId().split(",");
                Set<String> id = new HashSet<>(16);
                List<String> title = new ArrayList<>();
                for (String ids : stepIdsAll) {
                    //?????????????????????????????????????????????????????????
                    boolean contains = nodeIdAll.contains(ids);
                    if (!contains) {
                        FlowTaskNodeEntity entity = flowTaskNodeEntityList.stream().filter(t -> t.getNodeCode().equals(ids)).findFirst().orElse(null);
                        if (ids.equals(flowTaskNodeEntity.getNodeCode())) {
                            String step = String.join("," , stepId);
                            String stepIdAll = ids.replace(flowTaskNodeEntity.getNodeCode(), step);
                            id.add(stepIdAll);
                            List<String> name = flowTaskNodeEntityList.stream().filter(t -> step.contains(t.getNodeCode())).map(t -> t.getNodeName()).collect(Collectors.toList());
                            title.addAll(name);
                        } else {
                            id.add(ids);
                            if (entity != null) {
                                title.add(entity.getNodeName());
                            }
                        }
                    }
                }
                if (id.size() == 0) {
                    id.addAll(rejectId);
                    title.addAll(reTitle);
                }
                flowTaskEntity.setThisStepId(String.join("," , id));
                flowTaskEntity.setThisStep(String.join("," , title));
                //????????????
                Collections.sort(progress);
                flowTaskEntity.setCompletion(progress.size() > 0 ? Integer.valueOf(progress.get(0)) : null);
                flowTaskOperatorService.create(flowTaskOperatorEntityList);
            } else {
                //????????????
                flowTaskEntity.setThisStepId(flowTaskNodeEntity.getNodeCode());
                flowTaskEntity.setThisStep("??????");
                flowTaskEntity.setCompletion(0);
                flowTaskEntity.setStatus(FlowTaskStatusEnum.Reject.getCode());
                flowTaskNodeService.update(flowTaskEntity.getId());
                flowTaskOperatorService.update(flowTaskEntity.getId());
            }
            //?????????????????????
            FlowEngineEntity flowentity = flowEngineService.getInfo(flowTaskEntity.getFlowId());
            if (FlowNature.CUSTOM.equals(flowentity.getFormType())) {
                List<FlowTableModel> tableModelList = JsonUtil.getJsonToList(flowentity.getTables(), FlowTableModel.class);
                Object objectData = flowHandleModel.getFormData();
                if (objectData instanceof Map) {
                    Map<String, Object> formDataAll = (Map<String, Object>) objectData;
                    Map<String, Object> data = JsonUtil.stringToMap(String.valueOf(formDataAll.get("data")));
                    //formTempJson
                    FormDataModel formData = JsonUtil.getJsonToBean(flowTaskEntity.getFlowForm(), FormDataModel.class);
                    List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
                    Map<String, Object> dataAll = flowDataUtil.update(data, list, tableModelList, flowTaskEntity.getProcessId());
                    //??????task?????????
                    flowTaskEntity.setFlowFormContentJson(JsonUtil.getObjectToString(dataAll));
                }
            } else if (flowentity.getFormType() == 1) {
                String coed = flowHandleModel.getEnCode();
                Object objectData = flowHandleModel.getFormData();
                if (objectData instanceof Map) {
                    String data = JsonUtil.getObjectToString(objectData);
                    formData(coed, flowTaskEntity.getId(), data);
                }
            }
            this.updateById(flowTaskEntity);
            //????????????
            Properties properties = nodeModel.getProperties();
            boolean func = properties.getHasApproverfunc() != null ? properties.getHasApproverfunc() : false;
            if (func) {
                String faceUrl = properties.getApproverInterfaceUrl() + "?" + taskNodeId + "=" + flowTaskNodeEntity.getId() + "&" +
                        handleStatus + "=" + flowTaskOperatorRecordEntity.getHandleStatus() + "&" + taskId + "=" + flowTaskNodeEntity.getTaskId();
                System.out.println("??????????????????:" + faceUrl);
                HttpUtil.httpRequestAll(faceUrl, "GET" , null);
            }
            //????????????
            Map<String, Object> message = new HashMap<>(16);
            message.put("type" , FlowMessageEnum.wait.getCode());
            message.put("id" , flowTaskEntity.getId());
            messagePush(flowTaskOperatorEntityList, flowTaskEntity.getFullName() + "????????????" , JsonUtilEx.getObjectToString(message));
            //?????????????????????
            List<String> userId = new ArrayList<>();
            userId.add(flowTaskEntity.getCreatorUserId());
            message.put("type" , FlowMessageEnum.me.getCode());
            SentMessageModel model = new SentMessageModel();
            model.setBodyText(JsonUtil.getObjectToString(message));
            model.setTitle(flowTaskEntity.getFullName() + "??????????????????");
            model.setToUserIds(userId);
            noticeApi.sentMessage(model);
        } catch (SQLException e) {
            //??????????????????
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new WorkFlowException("??????????????????");
        } catch (
                WorkFlowException work) {
            //??????????????????
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new WorkFlowException(work.getMessage());
        }
    }

    @Override
    public void recall(FlowTaskEntity flowTaskEntity, List<FlowTaskNodeEntity> flowTaskNodeList, FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity, FlowHandleModel flowHandleModel) throws WorkFlowException {
        if (flowTaskEntity.getCompletion() == 100) {
            throw new WorkFlowException("??????????????????????????????????????????");
        }
        //???????????????????????????
        FlowTaskNodeEntity nodeEntity = flowTaskNodeList.stream().filter(t -> t.getId().equals(flowTaskOperatorRecordEntity.getTaskNodeId())).findFirst().get();
        //??????????????????
        List<FlowTaskNodeEntity> upList = flowTaskNodeList.stream().filter(t -> nodeEntity.getNodeUp().equals(t.getNodeCode())).collect(Collectors.toList());
        if (upList.size() == 0) {
            //??????????????????
            upList = flowTaskNodeList.stream().filter(t -> nodeEntity.getNodeNext() != null && nodeEntity.getNodeNext().contains(t.getNodeCode())).collect(Collectors.toList());
        }
        //??????????????????
        List<String> upNode = upList.stream().map(t -> t.getNodeCode()).collect(Collectors.toList());
        //??????????????????
        List<FlowTaskOperatorEntity> operatorList = flowTaskOperatorService.getList(flowTaskEntity.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        //??????????????????????????????
        boolean isNext = operatorList.stream().filter(t -> upNode.contains(t.getNodeCode()) && t.getCompletion() == 0).count() > 0;
        //???????????????????????????????????????
        Set<String> nodeIdAll = new HashSet<>(16);
        FlowJsonUtil.upList(flowTaskNodeList, nodeEntity.getNodeCode(), nodeIdAll, new String[]{});
        FlowJsonUtil.nextList(flowTaskNodeList, nodeEntity.getNodeCode(), nodeIdAll, new String[]{});
        //???????????????????????????????????????????????????
        if (isNext) {
            //?????????????????????????????????
            flowTaskOperatorService.updateReject(flowTaskEntity.getId(), nodeIdAll);
            //????????????
            FlowTaskOperatorEntity flowTaskOperatorEntity = flowTaskOperatorService.getInfo(flowTaskOperatorRecordEntity.getTaskOperatorId());
            flowTaskOperatorEntity.setCompletion(0);
            flowTaskOperatorEntity.setHandleStatus(null);
            flowTaskOperatorEntity.setHandleTime(null);
            flowTaskOperatorService.update(flowTaskOperatorEntity);
            //????????????
            flowTaskOperatorRecordEntity.setTaskOperatorId(null);
            flowTaskOperatorRecordEntity.setTaskNodeId(null);
            flowTaskOperatorRecordService.update(flowTaskOperatorRecordEntity.getId(), flowTaskOperatorRecordEntity);
            //????????????
            UserInfo userInfo = userProvider.get();
            flowTaskOperatorRecordEntity.setHandleId(userInfo.getUserId());
            flowTaskOperatorRecordEntity.setHandleTime(new Date());
            flowTaskOperatorRecordEntity.setHandleStatus(3);
            flowTaskOperatorRecordEntity.setNodeName(nodeEntity.getNodeName());
            flowTaskOperatorRecordEntity.setTaskId(flowTaskEntity.getId());
            flowTaskOperatorRecordEntity.setHandleOpinion(flowHandleModel.getHandleOpinion());
            flowTaskOperatorRecordService.create(flowTaskOperatorRecordEntity);
            //???????????????
            flowTaskCirculateService.deleteByNodeId(nodeEntity.getId());
            //??????????????????
            List<String> thisStepId = Arrays.asList(flowTaskEntity.getThisStepId().split(","));
            List<String> thisStepIdAll = thisStepId.stream().filter(item -> !upNode.contains(item)).collect(Collectors.toList());
            //??????????????????
            thisStepIdAll.add(flowTaskOperatorRecordEntity.getNodeCode());
            //????????????
            List<String> stepTitleAll = new ArrayList<>();
            for (String id : thisStepIdAll) {
                FlowTaskNodeEntity entity = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(id)).findFirst().get();
                stepTitleAll.add(entity.getNodeName());
            }
            flowTaskEntity.setThisStepId(String.join("," , thisStepIdAll));
            flowTaskEntity.setThisStep(String.join("," , stepTitleAll));
            flowTaskEntity.setCompletion(0);
            flowTaskEntity.setStatus(FlowTaskStatusEnum.Handle.getCode());
            this.updateById(flowTaskEntity);
            //????????????
            ChildNodeList nodeModel = JsonUtil.getJsonToBean(nodeEntity.getNodePropertyJson(), ChildNodeList.class);
            Properties properties = nodeModel.getProperties();
            boolean func = properties.getHasRecallFunc() != null ? properties.getHasRecallFunc() : false;
            if (func) {
                String faceUrl = properties.getRecallInterfaceUrl() + "?" + taskNodeId + "=" + nodeEntity.getId() + "&" +
                        handleStatus + "=" + flowTaskOperatorRecordEntity.getHandleStatus() + "&" + taskId + "=" + nodeEntity.getTaskId();
                System.out.println("??????????????????:" + faceUrl);
                HttpUtil.httpRequestAll(faceUrl, "GET" , null);
            }
        } else {
            throw new WorkFlowException("??????????????????????????????????????????");
        }
    }

    @Override
    public void cancel(FlowTaskEntity flowTaskEntity, FlowHandleModel flowHandleModel) {
        //????????????
        flowTaskEntity.setStatus(FlowTaskStatusEnum.Cancel.getCode());
        flowTaskEntity.setEndTime(new Date());
        this.updateById(flowTaskEntity);
        //????????????
        UserInfo userInfo = userProvider.get();
        FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity = new FlowTaskOperatorRecordEntity();
        flowTaskOperatorRecordEntity.setHandleOpinion(flowHandleModel.getHandleOpinion());
        flowTaskOperatorRecordEntity.setHandleId(userInfo.getUserId());
        flowTaskOperatorRecordEntity.setHandleTime(new Date());
        flowTaskOperatorRecordEntity.setHandleStatus(4);
        flowTaskOperatorRecordEntity.setNodeName(flowTaskEntity.getThisStep());
        flowTaskOperatorRecordEntity.setTaskId(flowTaskEntity.getId());
        flowTaskOperatorRecordService.create(flowTaskOperatorRecordEntity);
    }

    @Override
    public List<FlowTaskEntity> getTaskList(String id) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskEntity::getFlowId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowTaskEntity> getOrderStaList(List<String> id) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(FlowTaskEntity::getId, id);
        return this.list(queryWrapper);
    }

    /**
     * ??????????????????
     *
     * @param status ????????????
     * @return
     */
    private boolean checkStatus(int status) {
        if (status == FlowTaskStatusEnum.Draft.getCode() || status == FlowTaskStatusEnum.Reject.getCode() || status == FlowTaskStatusEnum.Revoke.getCode()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param data     ????????????
     * @param title    ????????????
     * @param bodyText ????????????
     */
    private void messagePush(List<FlowTaskOperatorEntity> data, String title, String bodyText) {
        if (data.size() > 0) {
            List<String> toUserIds = new ArrayList<>();
            for (FlowTaskOperatorEntity item : data) {
                if (item.getHandleType().equals(String.valueOf(FlowTaskOperatorEnum.AppointPosition.getCode()))) {
                    //??????????????????????????????
                    List<UserEntity> users = usersApi.getListByPositionId(item.getHandleId());
                    toUserIds = users.stream().map(u -> u.getId()).collect(Collectors.toList());
                } else {
                    if (StringUtil.isNotEmpty(item.getHandleId())) {
                        toUserIds.add(item.getHandleId());
                    }
                }
            }
            SentMessageModel model = new SentMessageModel();
            model.setBodyText(bodyText);
            model.setTitle(title);
            model.setToUserIds(toUserIds);
            noticeApi.sentMessage(model);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param data     ????????????
     * @param title    ????????????
     * @param bodyText ????????????
     */
    private void messagePushCirculate(List<FlowTaskCirculateEntity> data, String title, String bodyText) {
        if (data.size() > 0) {
            List<String> toUserIds = new ArrayList<>();
            for (FlowTaskCirculateEntity item : data) {
                if (item.getObjectType().equals(String.valueOf(FlowTaskOperatorEnum.AppointPosition.getCode()))) {
                    //??????????????????????????????
                    List<UserEntity> users = usersApi.getListByPositionId(item.getObjectId());
                    toUserIds = users.stream().map(u -> u.getId()).collect(Collectors.toList());
                } else {
                    toUserIds.add(item.getObjectId());
                }
            }
            SentMessageModel model = new SentMessageModel();
            model.setBodyText(bodyText);
            model.setTitle(title);
            model.setToUserIds(toUserIds);
            noticeApi.sentMessage(model);
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param positionAll  ????????????
     * @param userPosition ??????????????????
     * @return
     */
    private boolean getCirculateUser(String[] positionAll, List<String> userPosition) {
        boolean flag = true;
        for (String positionId : positionAll) {
            if (userPosition.stream().filter(t -> t.contains(positionId)).count() > 0) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param userPosition ???????????????????????????
     * @param operatorList ???????????????
     * @param nodeModel    ????????????
     */
    private void getApproverUser(List<String> userPosition, List<FlowTaskOperatorEntity> operatorList, ChildNodeList nodeModel) {
        for (String user : userPosition) {
            //??????????????????????????????
            if (operatorList.stream().filter(t -> t.getHandleId().equals(user)).count() == 0) {
                FlowTaskOperatorEntity flowTask = new FlowTaskOperatorEntity();
                flowTask.setId(RandomUtil.uuId());
                flowTask.setHandleType(String.valueOf(FlowTaskOperatorEnum.AppointUser.getCode()));
                flowTask.setHandleId(user);
                flowTask.setNodeCode(nodeModel.getCustom().getNodeId());
                flowTask.setNodeName(nodeModel.getProperties().getTitle());
                flowTask.setTaskNodeId(nodeModel.getTaskNodeId());
                flowTask.setTaskId(nodeModel.getTaskId());
                flowTask.setCreatorTime(new Date());
                flowTask.setCompletion(0);
                flowTask.setState(FlowNodeEnum.Process.getCode());
                flowTask.setType(nodeModel.getProperties().getAssigneeType());
                operatorList.add(flowTask);
            }
        }
    }

    /**
     * ????????????key?????????list
     *
     * @param fieLdsModelList ?????????json
     * @param jnpfKey         ????????????
     * @param keyList         ??????list
     */
    private void tempJson(List<FieLdsModel> fieLdsModelList, Map<String, String> jnpfKey, Map<String, Object> keyList) {
        List<DictionaryDataEntity> dicDatayList = dictionaryDataApi.getListAll().getData();
        List<OrganizeEntity> organizeList = organizeApi.getList();
        List<UserEntity> userList = usersApi.getUserList();
        List<PositionEntity> positionList = positionApi.getListAll().getData();
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            String model = fieLdsModel.getVModel();
            ConfigModel config = fieLdsModel.getConfig();
            String key = config.getJnpfKey();
            jnpfKey.put(model, key);
            if ("select".equals(key) || "checkbox".equals(key) || "radio".equals(key)) {
                String type = config.getDataType();
                List<Map<String, String>> optionslList = new ArrayList<>();
                String fullName = config.getProps().getLabel();
                String value = config.getProps().getValue();
                if ("dictionary".equals(type)) {
                    String dictionaryType = config.getDictionaryType();
                    List<DictionaryDataEntity> dicList = dicDatayList.stream().filter(t -> t.getDictionaryTypeId().equals(dictionaryType)).collect(Collectors.toList());
                    for (DictionaryDataEntity dataEntity : dicList) {
                        Map<String, String> optionsModel = new HashMap<>(16);
                        optionsModel.put("id" , dataEntity.getId());
                        optionsModel.put("fullName" , dataEntity.getFullName());
                        optionslList.add(optionsModel);
                    }
                } else if ("static".equals(type)) {
                    List<Map<String, Object>> staticList = JsonUtil.getJsonToListMap(fieLdsModel.getSlot().getOptions());
                    for (Map<String, Object> options : staticList) {
                        Map<String, String> optionsModel = new HashMap<>(16);
                        optionsModel.put("id" , String.valueOf(options.get(value)));
                        optionsModel.put("fullName" , String.valueOf(options.get(fullName)));
                        optionslList.add(optionsModel);
                    }
                } else if ("dynamic".equals(type)) {
                    String dynId = config.getPropsUrl();
                    //??????????????????
                    Map<String, Object> dynamicMap = new HashMap<>(16);
                    if (dynamicMap.get("data") != null) {
                        List<Map<String, Object>> dataList = JsonUtil.getJsonToListMap(dynamicMap.get("data").toString());
                        for (Map<String, Object> options : dataList) {
                            Map<String, String> optionsModel = new HashMap<>(16);
                            optionsModel.put("id" , String.valueOf(options.get(value)));
                            optionsModel.put("fullName" , String.valueOf(options.get(fullName)));
                            optionslList.add(optionsModel);
                        }
                    }
                }
                keyList.put(model, optionslList);
            }
            //??????
            if ("comSelect".equals(key)) {
                keyList.put(model, organizeList);
            }
            //??????
            if ("depSelect".equals(key)) {
                keyList.put(model, organizeList);
            }
            //??????
            if ("userSelect".equals(key)) {
                keyList.put(model, userList);
            }
            //??????
            if ("posSelect".equals(key)) {
                keyList.put(model, positionList);
            }
        }
    }

    private void formData(String code, String id, String data) throws WorkFlowException {
        try {
            /*Class[] types = new Class[]{String.class, String.class};
            Object[] datas = new Object[]{id, data};
            Object service = SpringContext.getBean(code);
            ReflectionUtil.invokeMethod(service, "data", types, datas);*/
        } catch (Exception e) {
            throw new WorkFlowException("????????????????????????");
        }
    }


}
