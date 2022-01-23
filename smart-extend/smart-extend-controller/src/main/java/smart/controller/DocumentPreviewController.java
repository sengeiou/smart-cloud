package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.emnus.FileTypeEnum;
import smart.file.FileApi;
import smart.util.*;
import smart.model.documentpreview.FileInfoVO;
import smart.model.documentpreview.FileListVO;
import smart.base.ActionResult;
import smart.base.Page;
import smart.base.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档在线预览
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "文档在线预览", value = "DocumentPreview")
@RestController
@RequestMapping("/DocumentPreview")
public class DocumentPreviewController {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FileApi fileApi;

    /**
     * 列表
     *
     * @param page
     * @return
     */
    @ApiOperation("获取文档列表")
    @GetMapping
    public ActionResult list(Page page) {
        List<FileModel> data = new ArrayList<>();
        String path = fileApi.getPath(FileTypeEnum.DOCUMENTPREVIEWPATH);
        File filePath = new File(path);
        List<File> files = FileUtil.getFile(filePath);
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                File item = files.get(i);
                FileModel fileModel = new FileModel();
                fileModel.setFileId(i + "");
                fileModel.setFileName(item.getName());
                fileModel.setFileType(FileUtil.getFileType(item));
                fileModel.setFileSize(FileUtil.getSize(String.valueOf(item.length())));
                fileModel.setFileTime(FileUtil.getCreateTime(path + item.getName()));
                data.add(fileModel);
            }
            data = data.stream().filter(
                    m -> "xlsx".equals(m.getFileType()) || "xls".equals(m.getFileType())
                            || "docx".equals(m.getFileType()) || "doc".equals(m.getFileType())
                            || "pptx".equals(m.getFileType()) || "ppt".equals(m.getFileType())
            ).collect(Collectors.toList());
            if (page.getKeyword() != null && !StringUtils.isEmpty(page.getKeyword())) {
                data = data.stream().filter(t -> t.getFileName().contains(page.getKeyword())).collect(Collectors.toList());
            }
        }
        List<FileListVO> list = JsonUtil.getJsonToList(data, FileListVO.class);
        return ActionResult.success(list);
    }

    /**
     * 文件预览
     *
     * @param fileId 文件标记
     * @return
     */
    @ApiOperation("文件在线预览")
    @GetMapping("/{fileId}/Preview")
    public ActionResult list(@PathVariable("fileId") Integer fileId) {
        UserInfo userInfo = userProvider.get();
        String filePath = fileApi.getPath(FileTypeEnum.DOCUMENTPREVIEWPATH);
        File filePaths = new File(filePath);
        List<File> files = FileUtil.getFile(filePaths);
        File file = null;
        if (fileId > files.size()) {
            return ActionResult.fail("文件找不到!");
        }
        file = files.get(fileId);
        if (file != null) {
            FileInfoVO vo = new FileInfoVO();
            vo.setFileName(file.getName());
//            vo.setFilePath(UploaderUtil.UploaderFile(userInfo.getId() + "#" + file.getName() + "#preview"));
            vo.setFilePath("/api/Extend/DocumentPreview/down/" + file.getName());
            return ActionResult.success(vo);
//            if ("pdf".equals(FileUtil.getFileType(file))) {
//                DownUtil.dowloadFile(file);
//             } else {
//                DownUtil.dowloadFile(OpenOfficeUtil.office(filePath + file.getName()));
//            }
        }
        return ActionResult.fail("文件找不到!");
    }

    /**
     * 下载文件
     *
     * @param fileName
     */
    @GetMapping("/down/{fileName}")
    public void down(@PathVariable("fileName") String fileName) {
        String filePath = fileApi.getPath(FileTypeEnum.DOCUMENTPREVIEWPATH) + fileName;
        if (FileUtil.fileIsFile(filePath)) {
            DownUtil.dowloadFile(filePath, fileName);
        }
    }

}
