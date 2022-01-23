package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.MPMaterialEntity;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.Pagination;
import smart.base.vo.PaginationVO;
import smart.exception.WxErrorException;
import smart.model.mpmaterial.MPMaterialForm;
import smart.model.mpmaterial.MPMaterialListVO;
import smart.permission.UsersApi;
import smart.permission.model.user.UserAllModel;
import smart.service.MPMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 公众号素材
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "公众号素材", description = "MPMaterial")
@RestController
@RequestMapping("/WeChat/MPMaterial")
public class MPMaterialController {

    @Autowired
    private MPMaterialService mpMaterialService;
    @Autowired
    private UsersApi usersApi;

    /**
     * 列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("公众号素材列表")
    @GetMapping("/{type}")
    public ActionResult List(Pagination pagination, @PathVariable("type") int type) {
        List<MPMaterialEntity> data = mpMaterialService.getList(type, pagination);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        List<MPMaterialListVO> listVO = JsonUtil.getJsonToList(data, MPMaterialListVO.class);
        List<UserAllModel> userList = usersApi.getAll().getData();
        for (MPMaterialListVO mpMaterial : listVO) {
            UserAllModel model = userList.stream().filter(t -> t.getId().equals(mpMaterial.getUploadUser())).findFirst().orElse(new UserAllModel());
            mpMaterial.setUploadUser(model.getRealName());
            if (StringUtil.isNotEmpty(mpMaterial.getReturnUrl())) {
                String url = "/api/Common/Image/weixin/" + mpMaterial.getFileJson();
                mpMaterial.setReturnUrl(url);
            }
        }
        return ActionResult.page(listVO,paginationVO);
    }

    /**
     * 显示图片
     *
     * @param fileName 头像文件
     * @return
     */
//    @ApiOperation("显示图片")
//    @GetMapping("/ShowImg/{fileName}")
//    public ActionResult<DownloadVO> GetShowImg(@PathVariable("fileName") String fileName) {
//        UserInfo userInfo = userProvider.get();
//        String filePath = configValueUtil.getMPMaterialFilePath() + fileName;
//        DownloadVO vo = DownloadVO.builder().name(fileName).build();
//        String fileType = FileUtil.getFileType(fileName);
//        String fileNewName = DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + "."+fileType;
//        if (FileUtil.fileIsFile(filePath)) {
//            FileUtil.copyFile(filePath, configValueUtil.getTemporaryFilePath(), fileNewName);
//        }
//        vo.setUrl("/api/Common/Download/" + UploaderUtil.UploaderFile(userInfo.getId()+"#"+fileNewName));
//        return ActionResult.success(vo);
//    }

    /**
     * 上传封面
     *
     * @return
     */
//    @PostMapping("/UploadCover")
//    public ActionResult UploadCover() {
//        List<MultipartFile> files = UpUtil.getFileAll();
//        MultipartFile file = files.get(0);
//        String fileType = UpUtil.getFileType(file);
//        String type = "jpg,png";
//        if (!OptimizeUtil.imageType(type, fileType)) {
//            return ActionResult.fail("上传失败，图片格式不正确");
//        }
//        if (OptimizeUtil.fileSize(file.getSize(), 1024000)) {
//            return ActionResult.fail("上传失败，图片大小超过1M");
//        }
//        String filePath = configValueUtil.getTemplateFilePath();
//        String fileName = DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + "." + fileType;
//        FileUtil.upFile(file, filePath, fileName);
//        return ActionResult.success("上传成功", fileName);
//    }

    /**
     * 上传永久素材
     *
     * @param mpMaterialForm 实体对象
     * @return
     */
    @ApiOperation("上传永久素材")
    @PostMapping
    public ActionResult UploadForeverMedia(@RequestBody @Valid MPMaterialForm mpMaterialForm) throws WxErrorException {
        MPMaterialEntity entity = JsonUtil.getJsonToBean(mpMaterialForm, MPMaterialEntity.class);
        if ("2".equals(String.valueOf(entity.getMaterialsType())) || "3".equals(String.valueOf(entity.getMaterialsType()))) {
            if (StringUtil.isEmpty(entity.getFileJson())) {
                return ActionResult.fail("素材必填");
            }
            mpMaterialService.UploadForeverMedia(entity);
        } else if ("4".equals(String.valueOf(entity.getMaterialsType()))) {
            if (StringUtil.isEmpty(entity.getTitle())) {
                return ActionResult.fail("标题必填");
            }
            if (StringUtil.isEmpty(entity.getFileJson())) {
                return ActionResult.fail("素材必填");
            }
            mpMaterialService.UploadForeverVideo(entity);
        } else if ("5".equals(String.valueOf(entity.getMaterialsType()))) {
            if (StringUtil.isEmpty(entity.getFileJson())) {
                return ActionResult.fail("封面素材必填");
            }
            if (StringUtil.isEmpty(entity.getContent())) {
                return ActionResult.fail("内容必填");
            }
            if (StringUtil.isEmpty(entity.getTitle())) {
                return ActionResult.fail("标题必填");
            }
            if (StringUtil.isEmpty(entity.getAuthor())) {
                return ActionResult.fail("作者必填");
            }
            mpMaterialService.UploadNews(entity);
        } else {
            return ActionResult.fail("没有此类型");
        }
        return ActionResult.success("上传成功");
    }

    /**
     * 删除永久素材
     *
     * @param mediaId 永久素材主键
     * @return
     */
    @ApiOperation("删除永久素材")
    @DeleteMapping("/{id}")
    public ActionResult DeleteForeverMedia(@PathVariable("id") String mediaId) throws WxErrorException {
        MPMaterialEntity entity = mpMaterialService.getInfo(mediaId);
        boolean flag = mpMaterialService.DeleteForeverMedia(entity);
        if (flag == false) {
            return ActionResult.fail("删除失败，数据不存在");
        }
        return ActionResult.success("删除成功");
    }

}
