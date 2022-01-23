package smart.portal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.vo.ListVO;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import smart.util.*;
import smart.base.*;
import smart.base.entity.DictionaryDataEntity;
import smart.permission.AuthorizeApi;
import smart.permission.UsersApi;
import smart.permission.entity.AuthorizeEntity;
import smart.permission.entity.UserEntity;
import smart.permission.model.user.UserAllModel;
import smart.portal.entity.PortalEntity;
import smart.portal.model.*;
import smart.portal.service.PortalService;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 可视化门户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@RestController
@Api(tags = "可视化门户", description = "Portal")
@RequestMapping("/Portal")
public class PortalController {

    @Autowired
    private UsersApi usersApi;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private PortalService portalService;
    @Autowired
    private DictionaryDataApi dictionaryDataApi;
    @Autowired
    private AuthorizeApi authorizeApi;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private RedisUtil redisUtil;


    @GetMapping
    public ActionResult list(PortalPagination portalPagination) {
        List<PortalEntity> list = portalService.getList(portalPagination);
        List<PortalListVO> listVO = JsonUtil.getJsonToList(list, PortalListVO.class);
        List<UserAllModel> userAllVOS = usersApi.getAll().getData();
        for (PortalListVO vo : listVO) {
            for (UserAllModel userVo : userAllVOS) {
                if (userVo.getId().equals(vo.getCreatorUser())) {
                    vo.setCreatorUser(userVo.getRealName() + "/" + userVo.getAccount());
                }
                if (userVo.getId().equals(vo.getLastmodifyuser())) {
                    vo.setLastmodifyuser(userVo.getRealName() + "/" + userVo.getAccount());
                }
            }
        }
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(portalPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }

    @GetMapping("/Selector")
    public ActionResult listSelcet(String type) {
        List<PortalEntity> list = portalService.getList().stream().filter(t->"1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        UserInfo userInfo=userProvider.get();
        if("1".equals(type)&&userInfo.getIsAdministrator()!=true){
        List<UserAllModel> model = usersApi.getDbUserAll().stream().filter(t->t.getId().equals(userProvider.get().getUserId())).collect(Collectors.toList());

        if(model.size()>0){
            List<String> roleIds;
            List<String> itemIds =new ArrayList<>();
            Set<String> set = new HashSet<>();
            List<PortalEntity> newPortalList =new ArrayList<>();

            roleIds=Arrays.asList(model.get(0).getRoleId().split(","));
            if(roleIds.size()>0){
                for(String ids:roleIds){
                    List<AuthorizeEntity> authorizeEntityList=authorizeApi.getListByObjectId(ids).stream().filter(t->"portal".equals(t.getItemType())).collect(Collectors.toList());
                    if(authorizeEntityList.size()>0){
                        for(AuthorizeEntity authorizeEntity:authorizeEntityList){
                            itemIds.add(authorizeEntity.getItemId());
                        }
                    }
                }
            }
            set.addAll(itemIds);
            for(PortalEntity entity:list){
                for(String iid:set){
                    if(iid.equals(entity.getId())){
                        newPortalList.add(entity);
                    }
                }
            }
            list=newPortalList;
        }else {
            return ActionResult.fail("您没有门户权限");
        }
        }
        List<PortalSelectModel> modelList = JsonUtil.getJsonToList(list, PortalSelectModel.class);

        for(PortalEntity portalEntity:list){
            DictionaryDataEntity dictionaryDataEntity=dictionaryDataApi.getInfo(portalEntity.getCategory()).getData();
            if(dictionaryDataEntity!=null){
                PortalSelectModel model=new PortalSelectModel();
                model.setId(dictionaryDataEntity.getId());
                model.setFullName(dictionaryDataEntity.getFullName());
                model.setParentId("0");
                if(!modelList.contains(model)){
                    modelList.add(model);
                }
            }
        }

        List<SumTree<PortalSelectModel>> sumTrees = TreeDotUtils.convertListToTreeDot(modelList);
        List<PortalSelectVO> listVO = JsonUtil.getJsonToList(sumTrees, PortalSelectVO.class);
        ListVO<PortalSelectVO> treeVo = new ListVO<>();
        treeVo.setList(listVO);
        return ActionResult.success(treeVo);
    }



    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) {
        PortalEntity entity = portalService.getInfo(id);
        PortalInfoVO vo = JsonUtil.getJsonToBean(JsonUtil.getObjectToStringDateFormat(entity, "yyyy-MM-dd HH:mm:ss"), PortalInfoVO.class);

        return ActionResult.success(vo);
    }

    @GetMapping("/{id}/auth")
    public ActionResult infoAuth(@PathVariable("id") String id) {
        UserInfo userInfo=userProvider.get();
        if((userInfo!=null&&userInfo.getRoleIds()!=null)){
            for(String roleId:userInfo.getRoleIds()){
                if (!StringUtil.isBlank(roleId) || !StringUtil.isEmpty(roleId)){
                    List<AuthorizeEntity> authorizeEntityList=authorizeApi.getListByObjectId(roleId).stream().filter(t->"portal".equals(t.getItemType())).collect(Collectors.toList());
                    for(AuthorizeEntity authorizeEntity:authorizeEntityList){
                        if(id.equals( authorizeEntity.getItemId())){
                            PortalEntity entity = portalService.getInfo(id);
                            PortalInfoAuthVO vo = JsonUtil.getJsonToBean(JsonUtil.getObjectToStringDateFormat(entity, "yyyy-MM-dd HH:mm:ss"), PortalInfoAuthVO.class);
                            return ActionResult.success(vo);
                        }
                    }
                }
            }
        }
        if(userInfo.getIsAdministrator()==true){
            PortalEntity entity = portalService.getInfo(id);
            PortalInfoAuthVO vo = JsonUtil.getJsonToBean(JsonUtil.getObjectToStringDateFormat(entity, "yyyy-MM-dd HH:mm:ss"), PortalInfoAuthVO.class);
            return ActionResult.success(vo);
        }
        return ActionResult.fail("您没有此门户使用权限，请重新设置");
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ActionResult delete(@PathVariable("id") String id) {
        PortalEntity entity = portalService.getInfo(id);
        if (entity != null) {
            portalService.delete(entity);
            QueryWrapper<AuthorizeEntity> queryWrapper=new QueryWrapper();
            queryWrapper.lambda().eq(AuthorizeEntity::getItemId,id);
            authorizeApi.remove(queryWrapper);
        }
        return ActionResult.success("删除成功");
    }

    @PostMapping()
    @Transactional
    public ActionResult create(@RequestBody @Valid PortalCrForm portalCrForm) {
        PortalEntity entity = JsonUtil.getJsonToBean(portalCrForm, PortalEntity.class);
        entity.setId(RandomUtil.uuId());
        portalService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 复制功能
     *
     * @param id
     * @return
     */
    @ApiOperation("复制功能")
    @PostMapping("/{id}/Actions/Copy")
    public ActionResult copyInfo(@PathVariable("id") String id) {
        PortalEntity entity = portalService.getInfo(id);
        entity.setEnabledMark(0);
        entity.setFullName(entity.getFullName() + "_副本");
        entity.setLastModifyTime(null);
        entity.setLastModifyUser(null);
        PortalEntity entity1 = JsonUtil.getJsonToBean(entity, PortalEntity.class);
        portalService.create(entity1);
        return ActionResult.success("新建成功");
    }


    @PutMapping("/{id}")
    @Transactional
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid PortalUpForm portalUpForm) {
        PortalEntity entity = JsonUtil.getJsonToBean(portalUpForm, PortalEntity.class);
        boolean flag = portalService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");

    }

    /**
     * 门户权限列表
     *
     * @param id 对象主键
     * @return
     */
    @Transactional
    @ApiOperation("设置默认门户")
    @PutMapping("/{id}/Actions/SetDefault")
    public ActionResult SetDefault(@PathVariable("id") String id) {
        UserEntity userEntity=usersApi.getInfoById(userProvider.get().getUserId());
        if(userEntity!=null){
            userEntity.setPortalId(id);
            usersApi.updateById(userEntity);
            String catchKey = cacheKeyUtil.getAllUser();
            if (redisUtil.exists(catchKey)) {
                redisUtil.remove(catchKey);
            }
        }else{
            return ActionResult.fail("设置失败，用户不存在");
        }
        return ActionResult.success("设置成功");
    }

}
