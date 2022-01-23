package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.MPMessageEntity;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.exception.DataException;
import smart.exception.WxErrorException;
import smart.model.mpmessage.*;
import smart.model.mpuser.MPUserModel;
import smart.permission.UsersApi;
import smart.permission.model.user.UserAllModel;
import smart.service.MPMessageService;
import smart.service.MPUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 公众号群发消息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "公众号消息", description = "MPMessage")
@RestController
@RequestMapping("/WeChat/MPMessage")
public class MPMessageController {

    @Autowired
    private MPMessageService mpMessageService;
    @Autowired
    private MPUserService mpUserService;
    @Autowired
    private UsersApi usersApi;

    /**
     * 列表
     *
     * @param paginationMPMessage
     * @return
     */
    @ApiOperation("公众号消息列表")
    @GetMapping
    public ActionResult List(PaginationMPMessage paginationMPMessage) {
        List<MPMessageEntity> data = mpMessageService.getList(paginationMPMessage);
        List<UserAllModel> userAll = usersApi.getAll().getData();
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationMPMessage, PaginationVO.class);
        for (MPMessageEntity entity : data) {
            UserAllModel model = userAll.stream().filter(t -> t.getId().equals(String.valueOf(entity.getSendUser()))).findFirst().orElse(null);
            if (model != null) {
                entity.setSendUser(model.getRealName() + "/" + model.getAccount());
            }
        }
        List<MPMessageListVO> listVO = JsonUtil.getJsonToList(data, MPMessageListVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 公众号用户
     *
     * @return
     */
    @ApiOperation("公众号用户")
    @GetMapping("/Users")
    public ActionResult GetUsers() throws WxErrorException {
        List<MPUserModel> data = mpUserService.getList();
        List<MPMessageUserVO> list = new ArrayList<>();
        for (MPUserModel model : data) {
            MPMessageUserVO user = new MPMessageUserVO();
            user.setId(model.getOpenid());
            user.setIcon("fa fa-user");
            user.setNickName(model.getNickname());
            list.add(user);
        }
        ListVO vo = new ListVO();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 预览
     *
     * @param openId               主键值 公众号用户Id
     * @param mpMessagePreviewForm 实体对象
     * @return
     */
    @ApiOperation("预览")
    @PostMapping("/{openId}/Actions/Preview")
    public ActionResult Preview(@PathVariable("openId") String openId, @RequestBody @Valid MPMessagePreviewForm mpMessagePreviewForm) throws WxErrorException, DataException {
        MPMessageEntity entity = JsonUtil.getJsonToBeanEx(mpMessagePreviewForm, MPMessageEntity.class);
        if ("0".equals(entity.getIsToAll()) && StringUtil.isEmpty(entity.getTagId())) {
            return ActionResult.fail("标签必填");
        }
        switch (entity.getMsgType()) {
            case 1:
                if (StringUtil.isEmpty(entity.getTxtContent())) {
                    return ActionResult.fail("内容必填");
                }
                break;
            case 2:
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("图片素材必填");
                }
                break;
            case 3:
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("语音素材必填");
                }
                break;
            case 4:
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("视频素材必填");
                }
                if (StringUtil.isEmpty(entity.getTitle())) {
                    return ActionResult.fail("标题必填");
                }
                if (StringUtil.isEmpty(entity.getTxtContent())) {
                    return ActionResult.fail("视频描述必填");
                }
                break;
            case 5:
                if (StringUtil.isEmpty(entity.getContent())) {
                    return ActionResult.fail("内容必填");
                }
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("封面必填");
                }
                if (StringUtil.isEmpty(entity.getAuthor())) {
                    return ActionResult.fail("作者必填");
                }
                if (StringUtil.isEmpty(entity.getTitle())) {
                    return ActionResult.fail("标题必填");
                }
                break;
            default:
                return ActionResult.fail("没有此类型");
        }
        mpMessageService.Preview(openId, entity);
        return ActionResult.success("操作成功");
    }

    /**
     * 发送全员
     *
     * @param mpMessageSentForm 实体对象
     * @return
     */
    @ApiOperation("发送全员")
    @PostMapping
    public ActionResult SendGroupMessageByTagId(@RequestBody @Valid MPMessageSentForm mpMessageSentForm) throws WxErrorException, DataException {
        MPMessageEntity entity = JsonUtil.getJsonToBeanEx(mpMessageSentForm, MPMessageEntity.class);
        if ("0".equals(entity.getIsToAll()) && StringUtil.isEmpty(entity.getTagId())) {
            return ActionResult.fail("标签必填");
        }
        switch (entity.getMsgType()) {
            case 1:
                if (StringUtil.isEmpty(entity.getTxtContent())) {
                    return ActionResult.fail("内容必填");
                }
                break;
            case 2:
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("图片素材必填");
                }
                break;
            case 3:
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("语音素材必填");
                }
                break;
            case 4:
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("视频素材必填");
                }
                if (StringUtil.isEmpty(entity.getTitle())) {
                    return ActionResult.fail("标题必填");
                }
                if (StringUtil.isEmpty(entity.getTxtContent())) {
                    return ActionResult.fail("视频描述必填");
                }
                break;
            case 5:
                if (StringUtil.isEmpty(entity.getContent())) {
                    return ActionResult.fail("内容必填");
                }
                if (StringUtil.isEmpty(entity.getFileJson())) {
                    return ActionResult.fail("封面必填");
                }
                if (StringUtil.isEmpty(entity.getAuthor())) {
                    return ActionResult.fail("作者必填");
                }
                if (StringUtil.isEmpty(entity.getTitle())) {
                    return ActionResult.fail("标题必填");
                }
                break;
            default:
                return ActionResult.fail("没有此类型");
        }
        mpMessageService.SendGroupMessageByTagId(entity);
        return ActionResult.success("发送成功");
    }

    /**
     * 上传封面
     *
     * @return
     */
//    @PostMapping("/UploadCover")
//    public ActionResult UploadCover() {
//        MultipartFile file = UpUtil.getFileAll().get(0);
//        String fileType = UpUtil.getFileType(file);
//        String type = "jpg,png";
//        if(!OptimizeUtil.imageType(type,fileType)){
//            return ActionResult.fail("上传失败，图片格式不正确");
//        }
//        if(OptimizeUtil.fileSize(file.getSize(),1024000)){
//            return ActionResult.fail("上传失败，图片大小超过1M");
//        }
//        String filePath = configValueUtil.getTemporaryFilePath();
//        String fileName = DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + "." + fileType;
//        FileUtil.upFile(file, filePath, fileName);
//        return ActionResult.success("上传成功", fileName);
//    }
//
//    /**
//     * 上传附件
//     *
//     * @return
//     */
//    @PostMapping("/UploadFile")
//    public ActionResult UploadFile() {
//        MultipartFile file = UpUtil.getFileAll().get(0);
//        String fileType = UpUtil.getFileType(file);
//        if (!OptimizeUtil.fileType(configValueUtil.getMPUploadFileType(),fileType)) {
//            return ActionResult.fail("上传失败，文件格式不允许上传");
//        }
//        String filePath = configValueUtil.getTemporaryFilePath();
//        String fileName = DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + "." + fileType;
//        FileUtil.upFile(file, filePath, fileName);
//        return ActionResult.success("上传成功", fileName);
//    }
}
