package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.exception.DataException;
import smart.exception.WxErrorException;
import smart.model.mptag.*;
import smart.model.mpuser.MPUserModel;
import smart.service.MPTagService;
import smart.service.MPUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 公众号标签
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "公众号标签",description = "MPTag")
@RestController
@RequestMapping("/WeChat/MPTag")
public class MPTagController {

    @Autowired
    private MPTagService mpTagService;
    @Autowired
    private MPUserService mpUserService;

    /**
     * 标签列表
     *
     * @return
     */
    @ApiOperation("标签列表")
    @GetMapping
    public ActionResult getList() throws WxErrorException {
        List<MPTagsModel> data = mpTagService.GetTageList();
        List<MPTagListVO> vos = new ArrayList<>();
        MPTagListVO tag = new MPTagListVO();
        tag.setFullName("全部用户");
        tag.setId("user");
        tag.setIcon("fa fa-user");
        vos.add(tag);
        for (MPTagsModel tagsModel : data) {
            MPTagListVO model = new MPTagListVO();
            model.setId(String.valueOf(tagsModel.getId()));
            model.setFullName(tagsModel.getName());
            model.setIcon("fa fa-user");
            vos.add(model);
        }
        ListVO vo = new ListVO();
        vo.setList(vos);
        return ActionResult.success(vo);
    }

    /**
     * 标签
     *
     * @return
     */
    @ApiOperation("标签下拉框")
    @GetMapping("/Tree")
    public ActionResult GetTree() throws WxErrorException {
        List<MPTagsModel> data = mpTagService.GetTageList();
        List<MPTagListVO> vos = new ArrayList<>();
        for (MPTagsModel tagsModel : data) {
            MPTagListVO model = new MPTagListVO();
            model.setId(String.valueOf(tagsModel.getId()));
            model.setFullName(tagsModel.getName());
            model.setIcon("fa fa-user");
            vos.add(model);
        }
        ListVO vo = new ListVO();
        vo.setList(vos);
        return ActionResult.success(vo);
    }

    /**
     * 获取标签信息
     *
     * @param id 标签模型
     * @return
     */
    @ApiOperation("获取标签信息")
    @PostMapping("/{id}")
    public ActionResult Info(@PathVariable("id") int id) throws WxErrorException, DataException {
        List<MPTagsModel> data = mpTagService.GetTageList();
        MPTagsModel model = data.stream().filter(t->String.valueOf(t.getId()).equals(String.valueOf(id))).findFirst().orElse(new MPTagsModel());
        MPTagInfoVO vo = JsonUtil.getJsonToBeanEx(model,MPTagInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 列表
     *
     * @return
     */
//    @GetMapping("/Tags")
//    public ActionResult GetTagList() throws WxErrorException {
//        List<MPTagsModel> data = mpTagService.GetTageList();
//        List<TreeViewModel> treeList = new ArrayList<>();
//        for (MPTagsModel tagsModel : data) {
//            TreeViewModel treeModel = new TreeViewModel();
//            treeModel.setId(String.valueOf(tagsModel.getId()));
//            treeModel.setText(tagsModel.getName());
//            treeModel.setParentId("0");
//            treeModel.setImg("fa fa-user");
//            treeModel.setShowcheck(true);
//            treeList.add(treeModel);
//        }
//        return ActionResult.success(ListToTreeUtil.ToTreeView(treeList));
//    }

    /**
     * 创建标签
     *
     * @param mpTagForm 标签模型
     * @return
     */
    @ApiOperation("创建标签")
    @PostMapping
    public ActionResult CreateTag(@RequestBody MPTagForm mpTagForm) throws WxErrorException {
        mpTagService.CreateTag(mpTagForm.getFullName());
        return ActionResult.success("新建成功");
    }

    /**
     * 编辑标签
     *
     * @return
     */
    @ApiOperation("编辑标签")
    @PutMapping("/{id}")
    public ActionResult UpdateTag(@PathVariable("id") String id,@RequestBody MPTagForm mpTagForm) throws WxErrorException {
        if(!"user".equals(id)){
            MPTagsModel tagModel = new MPTagsModel();
            tagModel.setName(mpTagForm.getFullName());
            tagModel.setId(Integer.parseInt(id));
            mpTagService.UpdateTag(tagModel);
            return ActionResult.success("编辑成功");
        }
        return ActionResult.fail("编辑失败，数据不存在");
    }

    /**
     * 删除标签
     *
     * @param id 标签id
     * @return
     */
    @ApiOperation("删除标签")
    @DeleteMapping("/{id}")
    public ActionResult DeleteTag(@PathVariable("id") int id) throws WxErrorException {
        boolean flag=mpTagService.DeleteTag(id);
        if (flag ==false){
            return ActionResult.fail("删除失败，数据不存在");
        }
        return ActionResult.success("删除成功");
    }

    /**
     * 设置标签
     *
     * @return
     */
    @ApiOperation("设置标签")
    @PostMapping("/Actions/SetTag")
    public ActionResult SetTagged(@RequestBody MPTagSetForm mpTagSetForm) throws WxErrorException {
        int id = -1;
        String[] tags =mpTagSetForm.getTagId().split(",");
        String tagId = tags[0];
        MPUserModel userInfo = mpUserService.UserInfo(mpTagSetForm.getOpenId());
        for (int item : userInfo.getTagidList()) {
            if (Integer.parseInt(tagId) == item) {
                id = item;
                break;
            }
        }
        String[] openid = mpTagSetForm.getOpenId().split(",");
        if (Integer.parseInt(tagId) == id) {
            //批量为用户取消标签
            mpTagService.BatchUnTagged(openid, tagId);
        } else {
            //批量为用户打标签
            mpTagService.BatchTagged(openid, tagId);
        }
        return ActionResult.success("操作成功");
    }
}
