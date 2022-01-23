package smart.engine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.Pagination;
import smart.base.UserInfo;
import smart.base.vo.PaginationVO;
import smart.engine.entity.FlowDelegateEntity;
import smart.engine.model.flowdelegate.FlowDelegatListVO;
import smart.engine.model.flowdelegate.FlowDelegateCrForm;
import smart.engine.model.flowdelegate.FlowDelegateInfoVO;
import smart.engine.model.flowdelegate.FlowDelegateUpForm;
import smart.engine.service.FlowDelegateService;
import smart.exception.DataException;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 流程委托
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "流程委托", value = "FlowDelegate")
@RestController
@RequestMapping("/Engine/FlowDelegate")
public class FlowDelegateController {

    @Autowired
    private FlowDelegateService flowDelegateService;
    @Autowired
    private UserProvider userProvider;

    /**
     * 获取流程委托列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("获取流程委托列表")
    @GetMapping
    public ActionResult list(Pagination pagination) {
        List<FlowDelegateEntity> list = flowDelegateService.getList(pagination);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        List<FlowDelegatListVO> listVO = JsonUtil.getJsonToList(list, FlowDelegatListVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 获取流程委托信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取流程委托信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        FlowDelegateEntity entity = flowDelegateService.getInfo(id);
        FlowDelegateInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, FlowDelegateInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建流程委托
     *
     * @param flowDelegateCrForm 实体对象
     * @return
     */
    @ApiOperation("新建流程委托")
    @PostMapping
    public ActionResult create(@RequestBody @Valid FlowDelegateCrForm flowDelegateCrForm) {
        FlowDelegateEntity entity = JsonUtil.getJsonToBean(flowDelegateCrForm, FlowDelegateEntity.class);
        UserInfo userInfo = userProvider.get();
        if(userInfo.getUserId().equals(entity.getFTouserid())){
            return ActionResult.fail("委托人为自己，委托失败");
        }
        flowDelegateService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新流程委托
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新流程委托")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid FlowDelegateUpForm flowDelegateUpForm) {
        FlowDelegateEntity entity = JsonUtil.getJsonToBean(flowDelegateUpForm, FlowDelegateEntity.class);
        UserInfo userInfo = userProvider.get();
        if(userInfo.getUserId().equals(entity.getFTouserid())){
            return ActionResult.fail("委托人为自己，委托失败");
        }
        boolean flag = flowDelegateService.update(id, entity);
        if (flag == false) {
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除流程委托
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除流程委托")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        FlowDelegateEntity entity = flowDelegateService.getInfo(id);
        if (entity != null) {
            flowDelegateService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/getList")
    public List<FlowDelegateEntity> getList() {
        return flowDelegateService.getList();
    }
}
