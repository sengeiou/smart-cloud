package smart.permission.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.Page;
import smart.permission.entity.UserRelationEntity;
import smart.permission.model.userrelation.UserRelationForm;
import smart.permission.model.userrelation.UserRelationIdsVO;
import smart.util.UserProvider;
import smart.permission.entity.UserEntity;
import smart.permission.model.user.UserAllModel;
import smart.permission.model.userrelation.UserRelationListVO;
import smart.permission.service.OrganizeService;
import smart.permission.service.UserRelationService;
import smart.permission.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户关系
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "用户关系", value = "UserRelation")
@RestController
@RequestMapping("/Permission/UserRelation")
public class UserRelationController {

    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private UserProvider userProvider;

    /**
     * 列表
     *
     * @param objectId 对象主键
     * @return
     */
//    @ApiOperation("获取岗位/角色成员列表")
//    @GetMapping("/{objectId}")
//    public ActionResult<ListVO<UserRelationListVO>> List(@PathVariable("objectId") String objectId,Pagination pagination) {
//        List<UserRelationEntity> data = userRelationService.GetListByObjectId(objectId);
//        //颠倒排序，变为降序排列
//        if("asc".equals(pagination.getSort()!=null?pagination.getSort().toLowerCase():"")){
//            Collections.reverse(data);
//        }
//        List<UserAllModel> userList=userService.getAll();
//        List<UserRelationListVO> relationListVOS =JSONUtil.getJsonToList(data,UserRelationListVO.class);
//        for(UserRelationListVO vo:relationListVOS){
//            for(UserAllModel user:userList){
//                if(user.getId().equals(vo.getUserId())){
//                    vo.setAccount(user.getAccount());
//                    vo.setDepartment(user.getDepartment());
//                    vo.setGender(user.getGender());
//                    vo.setOrganize(user.getOrganize());
//                    vo.setRealName(user.getRealName());
//                }
//            }
//        }
//        List<UserRelationListVO> vo=this.getResult(relationListVOS,pagination);
//        ListVO<UserRelationListVO> listVO=new ListVO<>();
//        listVO.setList(vo);
//        return ActionResult.success(listVO);
//    }

    /**
     * 列表
     *
     * @param objectId 对象主键
     * @return
     */
    @ApiOperation("获取岗位/角色/门户成员列表ids")
    @GetMapping("/{objectId}")
    public ActionResult listTree(@PathVariable("objectId") String objectId) {
        List<UserRelationEntity> data = userRelationService.getListByObjectId(objectId);
        List<String> ids = new ArrayList<>();
        for (UserRelationEntity entity : data) {
            ids.add(entity.getUserId());
        }
        UserRelationIdsVO vo = new UserRelationIdsVO();
        vo.setIds(ids);
        return ActionResult.success(vo);
    }


    /**
     * 岗位成员关键词搜索方法
     *
     * @param list
     * @param page
     * @return
     */
    private List<UserRelationListVO> getResult(List<UserRelationListVO> list, Page page) {
        List<UserRelationListVO> listResult = new ArrayList<>();
        for (UserRelationListVO vo : list) {
            if (vo.getAccount().contains(page.getKeyword()) || vo.getRealName().contains(page.getKeyword())) {
                listResult.add(vo);
            }
        }
        return listResult;
    }


    /**
     * 保存
     *
     * @param userRelationForm 对象主键
     * @return
     */
    @Transactional
    @ApiOperation("添加岗位或角色成员")
    @PostMapping("/{objectId}")
    public ActionResult save(@PathVariable("objectId") String objectId, @RequestBody UserRelationForm userRelationForm) {
        //清理被删除和被添加成员的token，原本的成员保持不变
        List<UserRelationEntity> data = userRelationService.getListByObjectId(objectId);
        //清除原有成员数据
        userRelationService.deleteListByObjectId(objectId);
        if (userRelationForm.getUserIds() != null) {
            List<String> oldIds = new ArrayList<>();
            for (UserRelationEntity entity : data) {
                oldIds.add(entity.getUserId());
            }
            List<String> newIds = new ArrayList(Arrays.asList(userRelationForm.getUserIds()));
            List<String> repeatIds = StringUtil.removeRepeatFactor(oldIds, newIds);
            List<UserAllModel> userAllModels= userService.getAll();
            for (String userId : repeatIds) {
                for(UserAllModel userAllModel:userAllModels){
                    if(userId.equals(userAllModel.getId())&&userAllModel.getIsAdministrator()!=1){
                        userProvider.removeOnLine(userId);
                    }
                }
            }

            //用户信息的角色岗位id中移除
            for (String repeatId : repeatIds) {
                if (oldIds.remove(repeatId)) {
                    UserEntity userEntity = userService.getInfo(repeatId);
                    if (userEntity != null) {
                        if ("Role".equals(userRelationForm.getObjectType())) {
                            //更新用户角色id
                            String originalId = userEntity.getRoleId();
                            if (StringUtil.isNotEmpty(originalId) && originalId.contains(objectId)) {
                                originalId = originalId.replace(objectId, "" );
                                if (originalId.contains(",," )) {
                                    originalId = originalId.replace(",," , "," );
                                } else if (!StringUtil.isEmpty(originalId) && ",".equals(originalId.substring(0, 1))) {
                                    originalId = originalId.substring(1);
                                } else if (!StringUtil.isEmpty(originalId)) {
                                    originalId = originalId.substring(0, originalId.length() - 1);
                                }
                                userEntity.setRoleId(originalId);
                                userService.updateById(userEntity);
                            }
                        }
                        if ("Position".equals(userRelationForm.getObjectType())) {
                            //更新用户岗位id
                            String originalId = userEntity.getPositionId();
                            if (originalId.contains(objectId)) {
                                originalId = originalId.replace(objectId, "" );
                                if (originalId.contains(",," )) {
                                    originalId = originalId.replace(",," , "," );
                                } else if (!StringUtil.isEmpty(originalId) && ",".equals(originalId.substring(0, 1))) {
                                    originalId = originalId.substring(1);
                                } else if (!StringUtil.isEmpty(originalId)) {
                                    originalId = originalId.substring(0, originalId.length() - 1);
                                }
                                userEntity.setPositionId(originalId);
                                userService.updateById(userEntity);
                            }
                        }

                    }
                }
            }


            for (String userId : userRelationForm.getUserIds()) {
                UserEntity userEntity = userService.getInfo(userId);
                if (userEntity != null) {
                    if ("Role".equals(userRelationForm.getObjectType())) {
                        //更新用户角色id
                        String originalId = userEntity.getRoleId();
                        if(StringUtil.isEmpty(originalId)){
                            userEntity.setRoleId(objectId);
                            userService.updateById(userEntity);
                        }else{
                            StringBuilder newRoleId = new StringBuilder();
                            newRoleId.append(originalId);
                            if (StringUtil.isNotEmpty(originalId) && !originalId.contains(objectId)) {
                                newRoleId.append("," + objectId);
                                userEntity.setRoleId(newRoleId.toString());
                                userService.updateById(userEntity);
                            }
                        }
                    }
                    if ("Position".equals(userRelationForm.getObjectType())) {
                        //更新用户岗位id
                        String originalId = userEntity.getPositionId();
                        if(StringUtil.isEmpty(originalId)){
                            userEntity.setPositionId(objectId);
                            userService.updateById(userEntity);
                        }else{
                            StringBuilder newPositionId = new StringBuilder();
                            newPositionId.append(originalId);
                            if (!originalId.contains(objectId)) {
                                newPositionId.append("," + objectId);
                                userEntity.setPositionId(newPositionId.toString());
                                userService.updateById(userEntity);
                            }
                        }
                    }

                }
            }
        }
        List<UserRelationEntity> list = new ArrayList<>();
        for (String userId : userRelationForm.getUserIds()) {
            UserRelationEntity entity = new UserRelationEntity();
            entity.setObjectId(objectId);
            entity.setObjectType(userRelationForm.getObjectType());
            entity.setUserId(userId);
            list.add(entity);
        }
        userRelationService.save(objectId, list);
        return ActionResult.success("保存成功" );
    }

    /**
     * 获取岗位
     * @return
     */
    @GetMapping("/getList/{userId}")
    public List<UserRelationEntity> getList(@PathVariable("userId") String userId){
        List<UserRelationEntity> list = userRelationService.getListByUserId(userId);
        return list;
    }

    /**
     * 获取岗位
     * @return
     */
    @GetMapping("/getObjectList/{objectId}")
    public List<UserRelationEntity> getObjectList(@PathVariable("objectId") String objectId){
        List<String> objectList = Arrays.asList(objectId.split(","));
        List<UserRelationEntity> list = userRelationService.getListByObjectIdAll(objectList);
        return list;
    }

}
