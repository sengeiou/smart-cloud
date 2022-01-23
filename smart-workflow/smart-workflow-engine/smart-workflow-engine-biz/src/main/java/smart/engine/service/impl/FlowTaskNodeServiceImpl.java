package smart.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.engine.entity.FlowTaskNodeEntity;
import smart.engine.mapper.FlowTaskNodeMapper;
import smart.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import smart.engine.service.FlowTaskNodeService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程节点
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class FlowTaskNodeServiceImpl extends ServiceImpl<FlowTaskNodeMapper, FlowTaskNodeEntity> implements FlowTaskNodeService {

    @Override
    public List<FlowTaskNodeEntity> getListAll() {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(FlowTaskNodeEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowTaskNodeEntity> getList(String taskId) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId).orderByAsc(FlowTaskNodeEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTaskNodeEntity getInfo(String id) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void create(List<FlowTaskNodeEntity> dataAll) {
        List<FlowTaskNodeEntity> startNodes = dataAll.stream().filter(t -> "start".equals(t.getNodeType())).collect(Collectors.toList());
        if (startNodes.size() > 0) {
            String startNode = startNodes.get(0).getNodeCode();
            long num = 0L;
            long maxNum = 0L;
            List<Long> max = new ArrayList<>();
            List<FlowTaskNodeEntity> treeList = new ArrayList<>();
            nodeList(dataAll, startNode, treeList, num, max);
            List<Long> sortIdList = max.stream().sorted(Long::compareTo).collect(Collectors.toList());
            if (sortIdList.size() > 0) {
                maxNum = sortIdList.get(sortIdList.size() - 1);
            }
            String nodeNext = "end";
            for (FlowTaskNodeEntity entity : dataAll) {
                String type = entity.getNodeType();
                FlowTaskNodeEntity node = treeList.stream().filter(t -> t.getNodeCode().equals(entity.getNodeCode())).findFirst().orElse(null);
                //判断结束节点是否多个
                List<FlowTaskNodeEntity> endCount = treeList.stream().filter(t -> StringUtil.isEmpty(t.getNodeNext())).collect(Collectors.toList());
                //判断下一节点是否多个
                String next = entity.getNodeNext();
                List<FlowTaskNodeEntity> nextNum = treeList.stream().filter(t -> t.getNodeNext().equals(next)).collect(Collectors.toList());
                if (StringUtil.isEmpty(next)) {
                    entity.setNodeNext(nodeNext);
                }
                if (node != null) {
                    entity.setSortCode(node.getSortCode());
                    entity.setState("0");
                    if (StringUtil.isEmpty(next)) {
                        entity.setNodeNext(nodeNext);
                    }
                }
                //判断下一节点是否相同
                if (!"empty".equals(type) && !"timer".equals(type)) {
                    //至少2条下一节点一样,才有可能是分流
                    if (endCount.size() > 1) {
                        if (nodeNext.equals(entity.getNodeNext())) {
                            ChildNodeList modelList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                            //添加指向下一节点的id
                            List<String> nextEndList = endCount.stream().map(t -> t.getNodeCode()).collect(Collectors.toList());
                            nextEndList.remove(entity.getNodeCode());
                            //赋值合流id和分流的id
                            modelList.getCustom().setInterflow(true);
                            modelList.getCustom().setInterflowId(String.join(",", nextEndList));
                            modelList.getCustom().setInterflowNextId(nodeNext);
                            entity.setNodePropertyJson(JsonUtilEx.getObjectToString(modelList));
                        }
                    }
                    //至少2条下一节点一样,才有可能是分流
                    if (nextNum.size() > 1) {
                        ChildNodeList modelList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                        //添加指向下一节点的id
                        List<String> nextEndList = nextNum.stream().map(t -> t.getNodeCode()).collect(Collectors.toList());
                        nextEndList.remove(entity.getNodeCode());
                        //赋值合流id和分流的id
                        modelList.getCustom().setInterflowId(String.join(",", nextEndList));
                        modelList.getCustom().setInterflowNextId(next);
                        modelList.getCustom().setInterflow(true);
                        entity.setNodePropertyJson(JsonUtilEx.getObjectToString(modelList));
                    }
                    this.save(entity);
                }
            }
            FlowTaskNodeEntity endround = new FlowTaskNodeEntity();
            endround.setId(RandomUtil.uuId());
            endround.setNodeCode(nodeNext);
            endround.setNodeName("结束");
            endround.setCompletion(0);
            endround.setCreatorTime(new Date());
            endround.setSortCode(maxNum + 1);
            endround.setTaskId(treeList.get(0).getTaskId());
            endround.setNodePropertyJson(startNodes.get(0).getNodePropertyJson());
            endround.setNodeType("endround");
            endround.setState("0");
            this.save(endround);
        }
    }

    @Override
    public void update(FlowTaskNodeEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void update(String taskId) {
        this.baseMapper.updateState(taskId);
    }


    private void nodeList(List<FlowTaskNodeEntity> dataAll, String nodeCode, List<FlowTaskNodeEntity> treeList, long num, List<Long> max) {
        num++;
        max.add(num);
        List<FlowTaskNodeEntity> thisEntity = dataAll.stream().filter(t -> t.getNodeCode().contains(nodeCode)).collect(Collectors.toList());
        for (int i = 0; i < thisEntity.size(); i++) {
            FlowTaskNodeEntity entity = thisEntity.get(i);
            entity.setSortCode(num);
            entity.setState("0");
            treeList.add(entity);
            String[] nodeNext = entity.getNodeNext().split(",");
            if (nodeNext.length > 0) {
                for (int k = 0; k < nodeNext.length; k++) {
                    String next = nodeNext[k];
                    long nums = treeList.stream().filter(t -> t.getNodeCode().equals(next)).count();
                    if (StringUtil.isNotEmpty(next) && nums == 0) {
                        nodeList(dataAll, next, treeList, num, max);
                    }
                }
            }
        }
    }

}
