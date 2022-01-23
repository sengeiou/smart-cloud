package smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.model.BaseSystemInfo;
import smart.util.DateUtil;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.base.SysConfigApi;
import smart.QYDepartmentEntity;
import smart.exception.WxErrorException;
import smart.mapper.QYDepartmentMapper;
import smart.model.qydepart.QYActionsModel;
import smart.permission.entity.OrganizeEntity;
import smart.service.QYDepartmentService;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.util.QyApiClient;
import smart.util.UserProvider;

import smart.model.qydepart.QYDepartTreeModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业号部门
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class QYDepartmentServiceImpl extends ServiceImpl<QYDepartmentMapper, QYDepartmentEntity> implements QYDepartmentService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private SysConfigApi sysConfigApi;

    @Override
    public List<QYDepartmentEntity> getList() {
        List<QYDepartmentEntity> data = this.baseMapper.getList();
        for (QYDepartmentEntity entity : data) {
            entity.setCategory("company".equals(entity.getCategory()) ? "公司" : "部门");
        }
        return data;
    }

    @Override
    public List<QYDepartmentEntity> getSyncList() {
        QueryWrapper<QYDepartmentEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public List<QYDepartmentEntity> getListByUserId(String userId) {
        List<QYDepartmentEntity> data = this.baseMapper.getListByUserId(userId);
        return data;
    }

    @Override
    public void synchronization(List<OrganizeEntity> organizelist) throws WxErrorException {
        String userId = userProvider.get().getUserId();
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        //删除
        List<QYDepartmentEntity> syncList = this.getSyncList();
        if (syncList.size() > 0) {
            for (QYDepartmentEntity departmentEntity : syncList) {
                if (organizelist.stream().filter(t -> t.getId().equals(departmentEntity.getOrganizeId())).count() == 0) {
                    JSONObject tokenObject = QyApiClient.getAccessToken(config.getQyhCorpId(), config.getQyhCorpSecret());
                    boolean resultBool = QyApiClient.deleteDepartment(String.valueOf(departmentEntity.getWeChatDeptId()), tokenObject.getString("access_token"));
                    if (resultBool == true) {
                        departmentEntity.setSubmitState("删除");
                        this.removeById(departmentEntity.getId());
                    }
                }
            }
        }
        List<QYDepartTreeModel> treeModels = JsonUtil.getJsonToList(organizelist, QYDepartTreeModel.class);
        List<SumTree<QYDepartTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeModels);
        List<QYActionsModel> models = JsonUtil.getJsonToList(trees, QYActionsModel.class);
        int order = 1;
        List<QYDepartmentEntity> list = this.baseMapper.getList();
        for (QYDepartmentEntity entity : list) {
            entity.setCategory("company".equals(entity.getCategory()) ? "公司" : "部门");
        }
        JSONObject tokenObject = QyApiClient.getAccessToken(config.getQyhCorpId(), config.getQyhCorpSecret());
        this.qyhhSynchronous(models, order, list, tokenObject.getString("access_token"), userId, syncList);
    }

    private void qyhhSynchronous(List<QYActionsModel> list, int order, List<QYDepartmentEntity> qyDepartmentList, String token, String userId, List<QYDepartmentEntity> syncList) throws WxErrorException {
        for (QYActionsModel model : list) {
            List<QYDepartmentEntity> entityList = qyDepartmentList.stream().filter(t -> String.valueOf(t.getOrganizeId()).equals(model.getId())).collect(Collectors.toList());
            if (entityList.size() == 0) {
                Integer parentId = 1;
                if (!"-1".equals(model.getParentId())) {
                    parentId = qyDepartmentList.stream().filter(t -> String.valueOf(t.getOrganizeId()).equals(model.getParentId())).findFirst().get().getWeChatDeptId();
                }
                JSONObject object = new JSONObject();
                object.put("name", model.getFullName());
                object.put("parentid", parentId);
                object.put("order", order);
                Integer id = QyApiClient.createDepartment(object.toJSONString(), token).getInteger("id");
                if (!StringUtils.isEmpty(String.valueOf(id))) {
                    QYDepartmentEntity departmentEntity = new QYDepartmentEntity();
                    departmentEntity.setId(RandomUtil.uuId());
                    departmentEntity.setOrganizeId(model.getId());
                    departmentEntity.setEnabledMark(model.getEnabledMark());
                    departmentEntity.setLastModifyTime(model.getLastModifyTime());
                    departmentEntity.setCategory(model.getCategory());
                    departmentEntity.setDescription(model.getDescription());
                    departmentEntity.setEnCode(model.getEnCode());
                    departmentEntity.setFullName(model.getFullName());
                    departmentEntity.setManagerId(model.getManagerId());
                    departmentEntity.setPropertyJson(model.getPropertyJson());
                    departmentEntity.setParentId(model.getParentId());
                    departmentEntity.setWeChatParentId(parentId);
                    departmentEntity.setWeChatDeptId(id);
                    departmentEntity.setCreatorUserId(userId);
                    departmentEntity.setCreatorTime(DateUtil.getNowDate());
                    departmentEntity.setSyncState(1);
                    departmentEntity.setForder(order);
                    departmentEntity.setSubmitState("创建");
                    this.save(departmentEntity);
                    qyDepartmentList.add(departmentEntity);
                }
            } else {
                QYDepartmentEntity department = entityList.get(0);
                qyDepartmentList.remove(department);
                JSONObject object = new JSONObject();
                object.put("id", department.getWeChatDeptId());
                object.put("name", model.getFullName());
                object.put("parentid", department.getWeChatParentId());
                object.put("order", department.getForder());
                boolean resultBool = QyApiClient.updateDepartment(object.toJSONString(), token);
                if (resultBool == true) {
                    department.setLastModifyTime(model.getLastModifyTime());
                    department.setLastModifyUserId(userId);
                    department.setSubmitState("编辑");
                    String id = syncList.stream().filter(t -> t.getOrganizeId().equals(model.getId())).findFirst().get().getId();
                    department.setId(id);
                    this.updateById(department);
                    qyDepartmentList.add(department);
                }
            }
            order++;
            if (model.getChildren() != null) {
                qyhhSynchronous(model.getChildren(), order, qyDepartmentList, token, userId, syncList);
            }
        }
    }
}
