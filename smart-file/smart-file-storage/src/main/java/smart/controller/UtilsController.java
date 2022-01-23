package smart.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.DictionaryDataApi;
import smart.base.vo.ListVO;
import smart.base.UserInfo;
import smart.base.entity.DictionaryDataEntity;
import smart.base.model.util.DownloadVO;
import smart.base.model.util.LanguageVO;
import smart.base.model.util.UploaderVO;
import smart.config.ConfigValueUtil;
import smart.controller.util.FileSizeUtil;
import smart.emnus.FileTypeEnum;
import smart.exception.DataException;
import smart.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 通用控制器
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Api(tags = "公共", value = "file")
@RestController
public class UtilsController {

    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DictionaryDataApi dictionaryDataApi;

    /**
     * 语言列表
     *
     * @return
     */
    @ApiOperation("语言列表")
    @GetMapping("/Language")
    public ActionResult getList() {
        String dictionaryTypeId = "dc6b2542d94b407cac61ec1d59592901";
        List<DictionaryDataEntity> list = dictionaryDataApi.getList(dictionaryTypeId).getData();
        List<LanguageVO> language = JsonUtil.getJsonToList(list, LanguageVO.class);
        ListVO vo = new ListVO();
        vo.setList(language);
        return ActionResult.success(vo);
    }

    /**
     * 图形验证码
     *
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("图形验证码")
    @GetMapping("/ImageCode/{timestamp}")
    public void imageCode(@PathVariable("timestamp") String timestamp) {
        DownUtil.downCode();
        redisUtil.insert(timestamp, ServletUtil.getSession().getAttribute(CodeUtil.RANDOMCODEKEY), 120);
    }

    /**
     * 上传文件/图片
     *
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("上传文件/图片")
    @PostMapping("/Uploader/{type}")
    public ActionResult uploader(@PathVariable("type") String type, MultipartFile file) {
        String fileType = UpUtil.getFileType(file);
        //验证类型
        if (type.equals(FileTypeEnum.WEIXIN)) {
            if (!OptimizeUtil.fileType(configValueUtil.getWeChatUploadFileType(), fileType) && !OptimizeUtil.fileType(configValueUtil.getMpUploadFileType(), fileType)) {
                return ActionResult.fail("上传失败，文件格式不允许上传");
            }
            if (OptimizeUtil.fileSize(file.getSize(), FileSizeUtil.WEXINFILESIZE)) {
                return ActionResult.fail("上传失败，文件大小超过1M");
            }
        } else {
            if (!OptimizeUtil.fileType(configValueUtil.getAllowUploadFileType(), fileType)) {
                return ActionResult.fail("上传失败，文件格式不允许上传");
            }
            if (OptimizeUtil.fileSize(file.getSize(), FileSizeUtil.WEXINFILESIZE)) {
                return ActionResult.fail("上传失败，文件大小超过1M");
            }
        }
        String fileName = DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + "." + fileType;
        UploaderVO vo = UploaderVO.builder().name(fileName).build();
        String filePath = getFilePath(type);
        if (type.equals(FileTypeEnum.USERAVATAR)) {
            vo.setUrl(UploaderUtil.uploaderImg(fileName));
        } else if (type.equals(FileTypeEnum.ANNEX)) {
            UserInfo userInfo = userProvider.get();
            vo.setUrl(UploaderUtil.uploaderFile("/api/file/Download/", userInfo.getId() + "#" + fileName));
        } else if (type.equals(FileTypeEnum.ANNEXPIC)) {
            vo.setUrl(UploaderUtil.uploaderImg("/api/file/Image/annex/", fileName));
        }else if (type.equals(FileTypeEnum.WORKFLOW)){
            vo.setUrl(UploaderUtil.uploaderImg("/api/file/Image/annex/", fileName));
        }
        FileUtil.upFile(file, filePath, fileName);
        return ActionResult.success(vo);
    }

    /**
     * 获取下载文件链接
     *
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("获取下载文件链接")
    @GetMapping("/Download/{type}/{fileName}")
    public ActionResult downloadUrl(@PathVariable("type") String type, @PathVariable("fileName") String fileName) {
        UserInfo userInfo = userProvider.get();
        String filePath = getFilePath(type) + fileName;
        if (FileUtil.fileIsFile(filePath)) {
            DownloadVO vo = DownloadVO.builder().name(fileName).url(UploaderUtil.uploaderFile(userInfo.getId() + "#" + fileName + "#" + type)).build();
            return ActionResult.success(vo);
        }
        return ActionResult.fail("文件不存在");
    }

    /**
     * 下载文件链接
     *
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("下载文件链接")
    @GetMapping("/Download")
    public void downloadFile() throws DataException {
        HttpServletRequest request = ServletUtil.getRequest();
        String reqJson = request.getParameter("encryption");
        String fileNameAll = DesUtil.aesDecode(reqJson);
        if (!StringUtil.isEmpty(fileNameAll)) {
            String[] data = fileNameAll.split("#");
            String token = data.length > 0 ? data[0] : "";
            if (redisUtil.exists(token)) {
                String fileName = data.length > 1 ? data[1] : "";
                String type = data.length > 2 ? data[2] : "";
                String filePath = getFilePath(type.toLowerCase()) + fileName;
//                if (FileUtil.fileIsFile(filePath)) {
                System.out.println(Charset.defaultCharset() + "路径1是" + filePath);
                DownUtil.dowloadFile(filePath, fileName);
//                }
            } else {
                throw new DataException("token验证失败");
            }
        }
    }

    /**
     * 下载文件链接
     *
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("下载模板文件链接")
    @GetMapping("/DownloadModel")
    public void downloadModel() {
        HttpServletRequest request = ServletUtil.getRequest();
        String reqJson = request.getParameter("encryption");
        String fileNameAll = DesUtil.aesDecode(reqJson);
        if (!StringUtil.isEmpty(fileNameAll)) {
            String token = fileNameAll.split("#")[0];
            if (redisUtil.exists(token)) {
                String fileName = fileNameAll.split("#")[1];
                String filePath = configValueUtil.getTemplateFilePath() + fileName;
//                if (FileUtil.fileIsFile(filePath)) {
                System.out.println(Charset.defaultCharset() + "路径2是" + filePath);
                DownUtil.dowloadFile(filePath, fileName);
//                }
            }
        }
    }


    /**
     * 获取图片
     *
     * @param fileName
     * @param type
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("获取图片")
    @GetMapping("/Image/{type}/{fileName}")
    public void downLoadImg(@PathVariable("type") String type, @PathVariable("fileName") String fileName) {
        String filePath = getFilePath(type) + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            DownUtil.dowloadFile(filePath);
        }
    }

    /**
     * 获取IM聊天图片
     * 注意 后缀名前端故意把 .替换@
     *
     * @param fileName
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("获取IM聊天图片")
    @GetMapping("/IMImage/{fileName}")
    public void imImage(@PathVariable("fileName") String fileName) {
        String paths = configValueUtil.getImContentFilePath() + fileName;
        File file = new File(paths);
        if (file.exists()) {
            DownUtil.dowloadFile(paths);
        }
    }

    /**
     * 获取IM聊天语音
     * 注意 后缀名前端故意把 .替换@
     *
     * @param fileName
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("获取IM聊天语音")
    @GetMapping("/IMVoice/{fileName}")
    public void imVoice(@PathVariable("fileName") String fileName) {
        String paths = configValueUtil.getImContentFilePath() + fileName.replaceAll("@", ".");
        File file = new File(paths);
        if (file.exists()) {
            DownUtil.dowloadFile(paths);
        }
    }

    /**
     * app启动获取信息
     *
     * @param appName
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("app启动获取信息")
    @GetMapping("/AppStartInfo/{appName}")
    public ActionResult getAppStartInfo(@PathVariable("appName") String appName) {
        JSONObject object = new JSONObject();
        object.put("AppVersion", configValueUtil.getAppVersion());
        object.put("AppUpdateContent", configValueUtil.getAppUpdateContent());
        return ActionResult.success(object);
    }

    //----------------------

    /**
     * 通过type获取路径
     *
     * @param type 类型
     * @return
     */
    @NoDataSourceBind()
    @GetMapping("/getPath/{type}")
    public String getPath(@PathVariable("type") String type) {
        return getFilePath(type);
    }

    /**
     * 通过fileType获取文件夹名称
     *
     * @param fileType 文件类型
     * @return
     */
    public String getFilePath(String fileType) {
        String filePath = null;
        //判断是那种类型得到相应的文件夹
        switch (fileType){
            //用户头像存储路径
            case FileTypeEnum.USERAVATAR:
                filePath = configValueUtil.getUserAvatarFilePath();
                break;
            //邮件文件存储路径
            case FileTypeEnum.MAIL:
                filePath = configValueUtil.getEmailFilePath();
                break;
            //前端附件文件目录
            case FileTypeEnum.ANNEX:
                filePath = configValueUtil.getWebAnnexFilePath();
                break;
            case FileTypeEnum.ANNEXPIC:
                filePath = configValueUtil.getWebAnnexFilePath();
                break;
            //IM聊天图片+语音存储路径
            case FileTypeEnum.IM:
                filePath = configValueUtil.getImContentFilePath();
                break;
            //临时文件存储路径
            case FileTypeEnum.WEIXIN:
                filePath = configValueUtil.getMpMaterialFilePath();
                break;
            //临时文件存储路径
            case FileTypeEnum.WORKFLOW:
                filePath = configValueUtil.getSystemFilePath();
                break;
            //文档管理存储路径
            case FileTypeEnum.DOCUMENT:
                filePath = configValueUtil.getDocumentFilePath();
                break;
            //数据库备份文件路径
            case FileTypeEnum.DATABACKUP:
                filePath = configValueUtil.getDataBackupFilePath();
                break;
            //临时文件存储路径
            case FileTypeEnum.TEMPORARY:
                filePath = configValueUtil.getTemporaryFilePath();
                break;
            //允许上传文件类型
            case FileTypeEnum.ALLOWUPLOADFILETYPE:
                filePath = configValueUtil.getAllowUploadFileType();
                break;
            //文件在线预览存储pdf
            case FileTypeEnum.DOCUMENTPREVIEWPATH:
                filePath = configValueUtil.getDocumentPreviewPath();
                break;
            //文件模板存储路径
            case FileTypeEnum.TEMPLATEFILE:
                filePath = configValueUtil.getTemplateFilePath();
                break;
            //前端文件目录
            case FileTypeEnum.SERVICEDIRECTORY:
                break;
            //后端文件目录
            case FileTypeEnum.WEBDIRECTORY:
                filePath = configValueUtil.getWebDirectoryPath();
                break;
            default:
                break;
        }
        return filePath;
    }

}
