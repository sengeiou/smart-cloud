package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.QYDepartmentEntity;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Page;
import smart.exception.WxErrorException;
import smart.permission.OrganizeApi;
import smart.permission.entity.OrganizeEntity;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.model.qydepart.QYDepartListVO;
import smart.model.qydepart.QYDepartTreeModel;
import smart.service.QYDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@Api(tags = "企业号部门", value = "QYDepartment")
@RestController
@RequestMapping("/WeChat/QYDepartment")
public class QYDepartmentController {

    @Autowired
    private OrganizeApi organizeApi;
    @Autowired
    private QYDepartmentService qyDepartmentService;

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("企业号组织列表")
    @GetMapping
    public ActionResult getList(Page page) {
        List<QYDepartmentEntity> data = qyDepartmentService.getList();
        List<QYDepartmentEntity> dataAll = data;
        if(StringUtil.isNotEmpty(page.getKeyword())){
            data = data.stream().filter(t->t.getFullName().contains(page.getKeyword()) || t.getEnCode().contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<QYDepartmentEntity> list = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, dataAll), QYDepartmentEntity.class);
        List<QYDepartTreeModel> treeModels = JsonUtil.getJsonToList(list,QYDepartTreeModel.class);
        List<SumTree<QYDepartTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeModels);
        List<QYDepartListVO> listVOS = JsonUtil.getJsonToList(trees,QYDepartListVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVOS);
        return ActionResult.success(vo);
    }

    /**
     * 同步部门
     *
     * @return
     */
    @ApiOperation("同步部门")
    @PostMapping("/Actions/Synchronization")
    public ActionResult synchronization() throws WxErrorException {
        List<OrganizeEntity> data = organizeApi.getList();
        if (data.size() > 0) {
            qyDepartmentService.synchronization(data);
            return ActionResult.success("同步成功");
        }
        return ActionResult.success("同步失败，数据不存在");
    }
}
