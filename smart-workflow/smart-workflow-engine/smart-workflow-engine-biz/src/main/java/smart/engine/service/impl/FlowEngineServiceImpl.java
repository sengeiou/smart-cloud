package smart.engine.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.base.UserInfo;
import smart.engine.entity.FlowEngineEntity;
import smart.engine.entity.FlowEngineVisibleEntity;
import smart.engine.entity.FlowTaskNodeEntity;
import smart.engine.enums.FlowTaskOperatorEnum;
import smart.engine.mapper.FlowEngineMapper;
import smart.engine.model.flowengine.PaginationFlowEngine;
import smart.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import smart.engine.service.FlowEngineService;
import smart.engine.service.FlowEngineVisibleService;
import smart.exception.WorkFlowException;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程引擎
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class FlowEngineServiceImpl extends ServiceImpl<FlowEngineMapper, FlowEngineEntity> implements FlowEngineService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowEngineService flowEngineService;
    @Autowired
    private FlowEngineVisibleService flowEngineVisibleService;

    @Override
    public List<FlowEngineEntity> getList(PaginationFlowEngine pagination) {
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().like(FlowEngineEntity::getFullName, pagination.getKeyword());
        }
        if (StringUtil.isNotEmpty(pagination.getFormType())) {
            queryWrapper.lambda().like(FlowEngineEntity::getFormType, pagination.getFormType());
        }
        if (StringUtil.isNotEmpty(pagination.getEnabledMark())) {
            queryWrapper.lambda().like(FlowEngineEntity::getEnabledMark, pagination.getEnabledMark());
        }
        //排序
        queryWrapper.lambda().orderByAsc(FlowEngineEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowEngineEntity> getList() {
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(FlowEngineEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowEngineEntity> getFlowFormList() {
        List<FlowEngineEntity> data = new ArrayList<>();
        List<FlowEngineEntity> flowEngineData = flowEngineService.getList().stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark())) && t.getType() == 0).collect(Collectors.toList());
        UserInfo userInfo = userProvider.get();
        if (!userInfo.getIsAdministrator()) {
            //部分看见(岗位和用户)
            List<FlowEngineVisibleEntity> flowVisibleData = flowEngineVisibleService.getVisibleFlowList(userInfo.getUserId());
            for (FlowEngineVisibleEntity item : flowVisibleData) {
                List<FlowEngineEntity> flowEngineEntity = flowEngineData.stream().filter(m -> String.valueOf(m.getId()).equals(String.valueOf(item.getFlowId()))).collect(Collectors.toList());
                if (flowEngineEntity.size() > 0) {
                    data.addAll(flowEngineEntity);
                }
            }
            //全部看见
            List<FlowEngineEntity> datas = flowEngineData.stream().filter(m -> "0".equals(String.valueOf(m.getVisibleType()))).collect(Collectors.toList());
            for (FlowEngineEntity flowEngineEntity : datas) {
                data.add(flowEngineEntity);
            }
        } else {
            data = flowEngineData;
        }
        //去掉重复数据
        data = data.stream().distinct().collect(Collectors.toList());
        return data;
    }


    @Override
    public FlowEngineEntity getInfo(String id) throws WorkFlowException {
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowEngineEntity::getId, id);
        FlowEngineEntity flowEngineEntity = this.getOne(queryWrapper);
        if (flowEngineEntity == null) {
            throw new WorkFlowException("未找到流程引擎");
        }
        return flowEngineEntity;
    }

    @Override
    public FlowEngineEntity getInfoByEnCode(String enCode) throws WorkFlowException {
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowEngineEntity::getEnCode, enCode).eq(FlowEngineEntity::getEnabledMark, 1);
        FlowEngineEntity flowEngineEntity = this.getOne(queryWrapper);
        if (flowEngineEntity == null) {
            throw new WorkFlowException("未找到流程引擎");
        }
        return flowEngineEntity;
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowEngineEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(FlowEngineEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowEngineEntity::getEnCode, enCode);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(FlowEngineEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void delete(FlowEngineEntity entity) {
        this.removeById(entity.getId());
        QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowEngineVisibleEntity::getFlowId, entity.getId());
        flowEngineVisibleService.remove(queryWrapper);
    }

    @Override
    public void create(FlowEngineEntity entity, List<FlowEngineVisibleEntity> visibleList) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUser(userProvider.get().getUserId());
        entity.setVisibleType(visibleList.size() == 0 ? 0 : 1);
        this.save(entity);
        for (int i = 0; i < visibleList.size(); i++) {
            visibleList.get(i).setId(RandomUtil.uuId());
            visibleList.get(i).setFlowId(entity.getId());
            visibleList.get(i).setSortCode(RandomUtil.parses());
            flowEngineVisibleService.save(visibleList.get(i));
        }
    }

    @Override
    public boolean update(String id, FlowEngineEntity entity, List<FlowEngineVisibleEntity> visibleList) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUser(userProvider.get().getUserId());
        entity.setVisibleType(visibleList.size() == 0 ? 0 : 1);
        boolean flag = this.updateById(entity);
        if (flag == true) {
            QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(FlowEngineVisibleEntity::getFlowId, entity.getId());
            flowEngineVisibleService.remove(queryWrapper);
            for (int i = 0; i < visibleList.size(); i++) {
                visibleList.get(i).setId(RandomUtil.uuId());
                visibleList.get(i).setFlowId(entity.getId());
                visibleList.get(i).setSortCode(Long.parseLong(i + ""));
                flowEngineVisibleService.save(visibleList.get(i));
            }
        }
        return flag;
    }

    @Override
    public void update(String id, FlowEngineEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUser(userProvider.get().getUserId());
        this.updateById(entity);
    }

    @Override
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        FlowEngineEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(FlowEngineEntity::getSortCode, upSortCode)
                .eq(FlowEngineEntity::getCategory, upEntity.getCategory())
                .orderByDesc(FlowEngineEntity::getSortCode);
        List<FlowEngineEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            updateById(downEntity.get(0));
            updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        FlowEngineEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<FlowEngineEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(FlowEngineEntity::getSortCode, upSortCode)
                .eq(FlowEngineEntity::getCategory, downEntity.getCategory())
                .orderByAsc(FlowEngineEntity::getSortCode);
        List<FlowEngineEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            upEntity.get(0).setSortCode(temp);
            updateById(upEntity.get(0));
            updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public long getFlowNodeList(String stepId, List<FlowTaskNodeEntity> flowTaskNodeList) {
        long freeApprover = 0;
        if (StringUtil.isNotEmpty(stepId)) {
            String[] idAll = stepId.split(",");
            for (String id : idAll) {
                //当前节点是加签
                FlowTaskNodeEntity thisStep = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(id)).findFirst().orElse(null);
                if (thisStep != null) {
                    ChildNodeList thisNode = JsonUtil.getJsonToBean(thisStep.getNodePropertyJson(), ChildNodeList.class);
                    String type = String.valueOf(thisNode.getProperties().getAssigneeType());
                    if (type.equals(String.valueOf(FlowTaskOperatorEnum.FreeApprover.getCode()))) {
                        freeApprover = 1;
                        break;
                    } else {
                        //下个节点是加签
                        if (thisStep.getNodeNext() != null) {
                            String[] nextAll = thisStep.getNodeNext().split(",");
                            for (String nextId : nextAll) {
                                FlowTaskNodeEntity nextStep = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(nextId)).findFirst().orElse(null);
                                if (nextStep != null) {
                                    ChildNodeList nextNode = JsonUtil.getJsonToBean(nextStep.getNodePropertyJson(), ChildNodeList.class);
                                    String nexttype = String.valueOf(nextNode.getProperties().getAssigneeType());
                                    if (nexttype.equals(String.valueOf(FlowTaskOperatorEnum.FreeApprover.getCode()))) {
                                        freeApprover = 1;
                                        break;
                                    }
                                }
                            }
                            //跳出循环
                            if (freeApprover == 1) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return freeApprover;
    }
}
