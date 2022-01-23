package smart.permission.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Page;
import smart.base.entity.DictionaryDataEntity;
import smart.base.service.DictionaryDataService;
import smart.exception.DataException;
import smart.permission.entity.UserRelationEntity;
import smart.permission.model.role.*;
import smart.permission.service.RoleService;
import smart.permission.service.UserRelationService;
import smart.permission.service.UserService;
import smart.permission.entity.RoleEntity;
import smart.permission.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "角色管理", value = "Role")
@RestController
@RequestMapping("/Permission/Role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private DictionaryDataService dataService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRelationService userRelationService;

    /**
     * 获取角色列表
     *
     * @param page
     * @return
     */
    @ApiOperation("获取角色列表")
    @GetMapping
    public ActionResult list(Page page) {
        List<RoleEntity> data = roleService.getList(page);
        List<String> type = data.stream().map(t->t.getType()).distinct().collect(Collectors.toList());
        List<DictionaryDataEntity> typeList = dataService.getDictionName(type);
        List<RoleListVO> list = new ArrayList<>();
        for (RoleEntity entity : data){
            RoleListVO roleVo = new RoleListVO();
            roleVo.setId(entity.getId());
            roleVo.setFullName(entity.getFullName());
            roleVo.setEnCode(entity.getEnCode());
            roleVo.setDescription(entity.getDescription());
            roleVo.setEnabledMark(entity.getEnabledMark());
            roleVo.setCreatorTime(JsonUtil.getJsonToBean(entity.getCreatorTime(),Long.class));
            String roleType = typeList.stream().filter(
                    t->t.getEnCode().equals(entity.getType())
            ).findFirst().orElse(new DictionaryDataEntity()).getEnCode();
            roleVo.setType(roleType);
            roleVo.setSortCode(entity.getSortCode());
            list.add(roleVo);
        }
        ListVO vo = new ListVO();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 角色下拉框列表
     *
     * @return
     */
    @ApiOperation("角色下拉框列表")
    @GetMapping("/Selector")
    public ActionResult listAll() {
        List<RoleEntity> list = roleService.getList().stream().filter(
                t -> "1".equals(String.valueOf(t.getEnabledMark()))
        ).collect(Collectors.toList());
        List<RoleSelectorVO> listvo = JsonUtil.getJsonToList(list,RoleSelectorVO.class);
        ListVO<RoleSelectorVO> vo = new ListVO();
        vo.setList(listvo);
        return ActionResult.success(vo);
    }

    /**
     * 获取角色信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取角色信息")
    @GetMapping("/{id}")
    public ActionResult getInfo(@PathVariable("id") String id)throws DataException {
        RoleEntity entity = roleService.getInfo(id);
        RoleInfoVO vo = JsonUtil.getJsonToBeanEx(entity,RoleInfoVO.class);
        return ActionResult.success(vo);
    }


    /**
     * 新建角色
     *
     * @param roleCrForm
     * @return
     */
    @ApiOperation("新建角色")
    @PostMapping
    public ActionResult create(@RequestBody @Valid RoleCrForm roleCrForm) {
        RoleEntity entity = JsonUtil.getJsonToBean(roleCrForm, RoleEntity.class);
        if (roleService.isExistByFullName(roleCrForm.getFullName(),entity.getId())){
            return ActionResult.fail("角色名称不能重复");
        }
        if (roleService.isExistByEnCode(roleCrForm.getEnCode(),entity.getId())){
            return ActionResult.fail("角色编码不能重复");
        }
        roleService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新角色
     *
     * @param id  主键值
     * @param roleUpForm
     * @return
     */
    @ApiOperation("更新角色")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id,@RequestBody @Valid RoleUpForm roleUpForm) {
        RoleEntity entity = JsonUtil.getJsonToBean(roleUpForm, RoleEntity.class);
        if (roleService.isExistByFullName(roleUpForm.getFullName(),id)){
            return ActionResult.fail("角色名称不能重复");
        }
        if (roleService.isExistByEnCode(roleUpForm.getEnCode(),id)){
            return ActionResult.fail("角色编码不能重复");
        }
        boolean flag=  roleService.update(id, entity);
        if(flag==false){
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除角色
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        RoleEntity entity = roleService.getInfo(id);
        if (entity != null) {
           List<UserRelationEntity> userRelList= userRelationService.getListByObjectId(id);
            for(UserRelationEntity entity1:userRelList){
               UserEntity entity2= userService.getById(entity1.getUserId());
               if(entity2!=null){
                   String newRoleId= entity2.getRoleId().replace(id,"");
                   if(entity2.getRoleId().contains(id)){
                       if(newRoleId.length()!=0&&newRoleId.substring(0,1)==","){
                           entity2.setRoleId(newRoleId.substring(1));
                       }else if(newRoleId.length()!=0){
                           entity2.setRoleId(newRoleId.replace(",,",","));
                       }
                   }
               }
            }
            userRelationService.deleteListByObjectId(id);
            roleService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 更新用户状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新用户状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult disable(@PathVariable("id") String id) {
        RoleEntity entity = roleService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == 1) {
                entity.setEnabledMark(0);
            }else {
                entity.setEnabledMark(1);
            }
            roleService.update(id, entity);
            return ActionResult.success("操作成功");
        }
        return ActionResult.fail("操作失败，数据不存在");
    }

    /**
     * 通过account返回角色实体
     * @param roleId
     * @return
     */
    @GetMapping("/getInfo/{roleId}")
    public ActionResult getInfoByRole(@PathVariable("roleId") String roleId){
        RoleEntity info = roleService.getInfo(roleId);
        return ActionResult.success(info);
    }

}
