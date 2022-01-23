package smart.engine.util;


import smart.engine.entity.FlowTaskNodeEntity;
import smart.engine.model.flowengine.shuntjson.childnode.ChildNode;
import smart.engine.model.flowengine.shuntjson.childnode.ProperCond;
import smart.engine.model.flowengine.shuntjson.childnode.Properties;
import smart.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import smart.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import smart.engine.model.flowengine.shuntjson.nodejson.Custom;
import smart.engine.model.flowengine.shuntjson.nodejson.DateProperties;
import smart.util.JsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线工作流开发
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
public class FlowJsonUtil {

    /**
     * 外层节点
     **/
    private static String cusNum = "0";

    /**
     * 获取下一节点
     **/
    public static String getNextNode(String nodeId, String data, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll) {
        String next = nextNodeId(data, nodeId, childNodeListAll, conditionListAll);
        return next;
    }

    /**
     * 下一节点id
     **/
    private static String nextNodeId(String data, String nodeId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll) {
        String nextId = "";
        boolean flag = false;
        ChildNodeList childNode = childNodeListAll.stream().filter(t -> t.getCustom().getNodeId().equals(nodeId)).findFirst().orElse(null);
        String contextType = childNode.getConditionType();
        //条件、分流的判断
        if (StringUtils.isNotEmpty(contextType)) {
            if (FlowCondition.CONDITION.equals(contextType)) {
                List<String> nextNodeId = new ArrayList<>();
                getContionNextNode(data, conditionListAll, nodeId, nextNodeId);
                nextId = String.join(",", nextNodeId);
                if (StringUtils.isNotEmpty(nextId)) {
                    flag = true;
                }
            } else if (FlowCondition.INTERFLOW.equals(contextType)) {
                List<String> flowList = childNode.getFlowList().stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                nextId = String.join(",", flowList);
                flag = true;
            }
        }
        //子节点
        if (!flag) {
            if (childNode.getCustom().getFlow()) {
                nextId = childNode.getCustom().getFlowId();
            } else {
                //不是外层的下一节点
                if (!cusNum.equals(childNode.getCustom().getNum())) {
                    nextId = childNode.getCustom().getFirstId();
                    if (childNode.getCustom().getChild()) {
                        nextId = childNode.getCustom().getChildNode();
                    }
                } else {
                    //外层的子节点
                    if (childNode.getCustom().getChild()) {
                        nextId = childNode.getCustom().getChildNode();
                    }
                }
            }
        }
        return nextId;
    }

    //---------------------------------------------------递归获取当前的上节点和下节点----------------------------------------------

    /**
     * 获取当前已完成节点
     **/
    public static void upList(List<FlowTaskNodeEntity> flowTaskNodeList, String node, Set<String> upList, String[] tepId) {
        FlowTaskNodeEntity entity = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(node)).findFirst().orElse(null);
        if (entity != null) {
            List<String> list = flowTaskNodeList.stream().filter(t -> t.getSortCode() != null && t.getSortCode() < entity.getSortCode()).map(t -> t.getNodeCode()).collect(Collectors.toList());
            list.removeAll(Arrays.asList(tepId));
            upList.addAll(list);
        }
    }

    /**
     * 获取当前未完成节点
     **/
    public static void nextList(List<FlowTaskNodeEntity> flowTaskNodeList, String node, Set<String> nextList, String[] tepId) {
        FlowTaskNodeEntity entity = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(node)).findFirst().orElse(null);
        if (entity != null) {
            List<String> list = flowTaskNodeList.stream().filter(t -> t.getSortCode() != null && t.getSortCode() > entity.getSortCode()).map(t -> t.getNodeCode()).collect(Collectors.toList());
            list.removeAll(Arrays.asList(tepId));
            nextList.addAll(list);
        }
    }

    //---------------------------------------------------条件----------------------------------------------

    /**
     * 递归条件
     **/
    private static void getContionNextNode(String data, List<ConditionList> conditionListAll, String nodeId, List<String> nextNodeId) {
        List<ConditionList> conditionAll = conditionListAll.stream().filter(t -> t.getPrevId().equals(nodeId)).collect(Collectors.toList());
        for (int i = 0; i < conditionAll.size(); i++) {
            ConditionList condition = conditionAll.get(i);
            List<ProperCond> conditions = condition.getConditions();
            boolean flag = nodeConditionDecide(data, conditions, new HashMap<>(100), new HashMap<>(100));
            //判断条件是否成立或者其他情况条件
            if (flag || condition.getIsDefault()) {
                String conditionId = condition.getNodeId();
                List<ConditionList> collect = conditionListAll.stream().filter(t -> t.getPrevId().equals(conditionId)).collect(Collectors.toList());
                if (collect.size() > 0) {
                    getContionNextNode(data, conditionListAll, conditionId, nextNodeId);
                } else {
                    if (nextNodeId.size() == 0) {
                        //先获取条件下的分流节点
                        if (condition.getFlow()) {
                            nextNodeId.add(condition.getFlowId());
                        } else {
                            //条件的子节点
                            if (condition.getChild()) {
                                nextNodeId.add(condition.getChildNodeId());
                            } else {
                                nextNodeId.add(condition.getFirstId());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 节点条件判断
     **/
    private static boolean nodeConditionDecide(String formDataJson, List<ProperCond> conditionList, Map<String, String> jnpfKey, Map<String, Object> keyList) {
        boolean flag = false;
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
        Map<String, Object> map = JsonUtil.stringToMap(formDataJson);
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < conditionList.size(); i++) {
            String logic = conditionList.get(i).getLogic();
            String field = conditionList.get(i).getField();
            Object form = map.get(field);
            if (form == null) {
                form = "";
            }
            String formValue = stringToAscii(String.valueOf(form));
            String symbol = conditionList.get(i).getSymbol();
            if ("<>".equals(symbol)) {
                symbol = "!=";
            }
            String value = conditionList.get(i).getFiledValue();
            String jnpfkey = jnpfKey.get(field);
            String filedValue = stringToAscii(String.valueOf(value));
            expression.append(formValue + symbol + filedValue);
            if (!StringUtils.isEmpty(logic)) {
                if (i != conditionList.size() - 1) {
                    expression.append(" " + logic + " ");
                }
            }
        }
        try {
            String result = String.valueOf(scriptEngine.eval(expression.toString()));
            flag = Boolean.valueOf(result);
        } catch (ScriptException e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 转成ascii码
     **/
    private static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append((int) chars[i]);
        }
        return sbu.toString();
    }

    //---------------------------------------------------------------解析--------------------------------------------------------------------------

    /**
     * 递归外层的节点
     **/
    public static void childListAll(ChildNode childNode, List<ChildNode> chilNodeList) {
        if (childNode != null) {
            chilNodeList.add(childNode);
            boolean haschildNode = childNode.getChildNode() != null;
            if (haschildNode) {
                ChildNode nextNode = childNode.getChildNode();
                childListAll(nextNode, chilNodeList);
            }
        }
    }

    /**
     * 最外层的json
     **/
    public static void getTemplateAll(ChildNode childNode, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll) {
        List<ChildNode> chilNodeList = new ArrayList<>();
        childListAll(childNode, chilNodeList);
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            Properties properties = childNode.getProperties();
            ChildNodeList childNodeList = new ChildNodeList();
            childNodeList.setProperties(properties);
            //定时器
            DateProperties model = JsonUtil.getJsonToBean(properties, DateProperties.class);
            childNodeList.setTimer(model);
            //自定义属性
            Custom customModel = new Custom();
            customModel.setType(childNode.getType());
            customModel.setNum("0");
            customModel.setFirstId("");
            customModel.setChild(haschildNode);
            customModel.setNodeId(nodeId);
            customModel.setPrevId(prevId);
            customModel.setChildNode(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                childNodeList.setConditionType(FlowCondition.CONDITION);
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    customModel.setFlow(isFlow);
                    childNodeList.setConditionType(FlowCondition.INTERFLOW);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    customModel.setFlowId(String.join(",", flowIdAll));
                }
            }
            childNodeList.setCustom(customModel);
            childNodeList.setFlowList(childNode.getConditionNodes());
            childNodeListAll.add(childNodeList);
            String firstId = "";
            if (haschildNode) {
                firstId = childNode.getChildNode().getNodeId();
            }
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

    /**
     * 递归子节点的子节点
     **/
    private static void getchildNode(ChildNode parentChildNode, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        ChildNode childNode = parentChildNode.getChildNode();
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            Properties properModel = childNode.getProperties();
            ChildNodeList childNodeList = new ChildNodeList();
            childNodeList.setProperties(properModel);
            //定时器
            DateProperties model = JsonUtil.getJsonToBean(properModel, DateProperties.class);
            childNodeList.setTimer(model);
            //自定义属性
            Custom customModel = new Custom();
            customModel.setType(childNode.getType());
            String num = chilNodeList.stream().filter(t -> t.getNodeId().equals(nodeId)).count() > 0 ? "0" : "1";
            customModel.setNum(num);
            customModel.setFirstId(firstId);
            if ("0".equals(num)) {
                customModel.setFirstId(haschildNode ? childNode.getChildNode().getNodeId() : "");
            }
            customModel.setChild(haschildNode);
            customModel.setNodeId(nodeId);
            customModel.setPrevId(prevId);
            customModel.setChildNode(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                childNodeList.setConditionType(FlowCondition.CONDITION);
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    customModel.setFlow(isFlow);
                    childNodeList.setConditionType(FlowCondition.INTERFLOW);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    customModel.setFlowId(String.join(",", flowIdAll));
                }
            }
            childNodeList.setCustom(customModel);
            childNodeList.setFlowList(childNode.getConditionNodes());
            childNodeListAll.add(childNodeList);
            //条件或者分流递归
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            //子节点递归
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

    /**
     * 条件、分流递归
     **/
    private static void conditionList(ChildNode childNode, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        List<ChildNode> conditionNodes = childNode.getConditionNodes();
        if (conditionNodes.size() > 0) {
            //判断是条件还是分流
            //判断父节点是否还有子节点,有的话替换子节点数据
            ChildNode childNodeModel = childNode.getChildNode();
            if (childNodeModel != null) {
                firstId = childNodeModel.getNodeId();
            } else {
                ChildNode nodes = chilNodeList.stream().filter(t -> t.getNodeId().equals(childNode.getNodeId())).findFirst().orElse(null);
                if (nodes != null) {
                    if (nodes.getChildNode() != null) {
                        firstId = childNode.getChildNode().getNodeId();
                    } else {
                        firstId = "";
                    }
                }
            }
            for (ChildNode node : conditionNodes) {
                boolean conditionType = node.getIsInterflow() == null ? true : false;
                if (conditionType) {
                    getCondition(node, firstId, childNodeListAll, conditionListAll, chilNodeList);
                } else {
                    getConditonFlow(node, firstId, childNodeListAll, conditionListAll, chilNodeList);
                }
            }
        }
    }

    /**
     * 条件递归
     **/
    private static void getCondition(ChildNode childNode, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            boolean isDefault = childNode.getProperties().getIsDefault() != null ? childNode.getProperties().getIsDefault() : false;
            ConditionList conditionList = new ConditionList();
            conditionList.setNodeId(nodeId);
            conditionList.setPrevId(prevId);
            conditionList.setChild(haschildNode);
            conditionList.setTitle(childNode.getProperties().getTitle());
            List<ProperCond> proList = JsonUtil.getJsonToList(childNode.getProperties().getConditions(), ProperCond.class);
            conditionList.setConditions(proList);
            conditionList.setChildNodeId(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            conditionList.setIsDefault(isDefault);
            conditionList.setFirstId(firstId);
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    conditionList.setFlow(isFlow);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    conditionList.setFlowId(String.join(",", flowIdAll));
                }
            }
            conditionListAll.add(conditionList);
            //递归条件、分流
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            //递归子节点
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

    /**
     * 条件递归
     **/
    private static void getConditonFlow(ChildNode childNode, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            Properties properties = childNode.getProperties();
            ChildNodeList childNodeList = new ChildNodeList();
            childNodeList.setProperties(properties);
            //定时器
            DateProperties model = JsonUtil.getJsonToBean(properties, DateProperties.class);
            childNodeList.setTimer(model);
            //自定义属性
            Custom customModel = new Custom();
            customModel.setType(childNode.getType());
            customModel.setNum("1");
            customModel.setFirstId(firstId);
            customModel.setChild(haschildNode);
            customModel.setChildNode(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            customModel.setNodeId(nodeId);
            customModel.setPrevId(prevId);
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                childNodeList.setConditionType(FlowCondition.CONDITION);
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    customModel.setFlow(isFlow);
                    childNodeList.setConditionType(FlowCondition.INTERFLOW);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    customModel.setFlowId(String.join(",", flowIdAll));
                }
            }
            childNodeList.setCustom(customModel);
            childNodeList.setFlowList(childNode.getConditionNodes());
            childNodeListAll.add(childNodeList);
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

}
