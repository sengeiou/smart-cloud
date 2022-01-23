package smart.controller;

import com.alibaba.druid.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.emnus.FileTypeEnum;
import smart.file.FileApi;
import smart.model.document.*;
import smart.permission.model.user.UserAllModel;
import smart.service.DocumentService;
import smart.base.vo.DownloadVO;
import smart.util.*;
import smart.entity.DocumentEntity;
import smart.entity.DocumentShareEntity;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Page;
import smart.base.UserInfo;
import smart.exception.DataException;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.permission.UsersApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档管理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "知识管理", value = "Document")
@RestController
@RequestMapping("/Document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private FileApi fileApi;
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private UserProvider userProvider;



    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取文件/文件夹信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        DocumentEntity entity = documentService.getInfo(id);
        DocumentInfoVO vo = JsonUtil.getJsonToBeanEx(entity, DocumentInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建
     *
     * @param documentCrForm 实体对象
     * @return
     */
    @ApiOperation("添加文件夹")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DocumentCrForm documentCrForm) {
        DocumentEntity entity = JsonUtil.getJsonToBean(documentCrForm, DocumentEntity.class);
        if (documentService.isExistByFullName(documentCrForm.getFullName(), entity.getId())) {
            return ActionResult.fail("文件夹名称不能重复");
        }
        documentService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id             主键值
     * @param documentUpForm 实体对象
     * @return
     */
    @ApiOperation("修改文件名/文件夹名")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid DocumentUpForm documentUpForm) {
        DocumentEntity entity = JsonUtil.getJsonToBean(documentUpForm, DocumentEntity.class);
        if (documentService.isExistByFullName(documentUpForm.getFullName(), id)) {
            return ActionResult.fail("文件夹名称不能重复");
        }
        boolean flag = documentService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除知识管理")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        DocumentEntity entity = documentService.getInfo(id);
        if (entity != null) {
            documentService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 列表（文件夹树）
     *
     * @return
     */
    @ApiOperation("获取知识管理列表（文件夹树）")
    @GetMapping("/FolderTree")
    public ActionResult folderTree() {
        List<DocumentEntity> data = documentService.getFolderList();
        List<DocumentFolderTreeModel> treeList = new ArrayList<>();
        DocumentFolderTreeModel model = new DocumentFolderTreeModel();
        model.setId("-1");
        model.setFullName("全部文档");
        model.setParentId("0");
        model.setIcon("0");
        treeList.add(model);
        for (DocumentEntity entity : data) {
            DocumentFolderTreeModel treeModel = new DocumentFolderTreeModel();
            treeModel.setId(entity.getId());
            treeModel.setFullName(entity.getFullName());
            treeModel.setParentId(entity.getParentId());
            treeModel.setIcon("fa fa-folder");
            treeList.add(treeModel);
        }
        List<SumTree<DocumentFolderTreeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<DocumentFolderTreeVO> listVO = JsonUtil.getJsonToList(trees, DocumentFolderTreeVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    /**
     * 列表（全部文档）
     *
     * @param page
     * @return
     */
    @ApiOperation("获取知识管理列表（全部文档）")
    @GetMapping
    public ActionResult allList(PageDocument page) {
        List<DocumentEntity> data = documentService.getAllList(page.getParentId());
        if (!StringUtils.isEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> t.getFullName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<DocumentListVO> list = JsonUtil.getJsonToList(data, DocumentListVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 列表（我的分享）
     *
     * @param page
     * @return
     */
    @ApiOperation("知识管理（我的共享列表）")
    @GetMapping("/Share")
    public ActionResult shareOutList(Page page) {
        List<DocumentEntity> data = documentService.getShareOutList();
        if (!StringUtils.isEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> t.getFullName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        ListVO vo = new ListVO();
        vo.setList(data);
        return ActionResult.success(vo);
    }

    /**
     * 列表（共享给我）
     *
     * @param page
     * @return
     */
    @ApiOperation("获取知识管理列表（共享给我）")
    @GetMapping("/ShareTome")
    public ActionResult shareTomeList(Page page) {
        List<DocumentEntity> list = documentService.getShareTomeList();
        if (!StringUtils.isEmpty(page.getKeyword())) {
            list = list.stream().filter(t -> t.getFullName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<UserAllModel> data = usersApi.getAll().getData();
        for (DocumentEntity entity : list) {
            for (UserAllModel userAllVO : data) {
                if (userAllVO.getId().equals(entity.getCreatorUserId())) {
                    entity.setCreatorUserId(userAllVO.getRealName() + "/" + userAllVO.getAccount());
                }
            }
        }
        List<DocumentStomeListVO> vos = JsonUtil.getJsonToList(list, DocumentStomeListVO.class);
        ListVO vo = new ListVO();
        vo.setList(vos);
        return ActionResult.success(vo);
    }

    /**
     * 列表（回收站）
     *
     * @param page
     * @return
     */
    @ApiOperation("获取知识管理列表（回收站）")
    @GetMapping("/Trash")
    public ActionResult trashList(Page page) {
        List<DocumentEntity> data = documentService.getTrashList();
        if (!StringUtils.isEmpty(page.getKeyword())) {
            data = data.stream().filter(t -> t.getFullName().contains(page.getKeyword())).collect(Collectors.toList());
        }
        ListVO vo = new ListVO();
        vo.setList(data);
        return ActionResult.success(vo);
    }

    /**
     * 列表（共享人员）
     *
     * @param documentId 文档主键
     * @return
     */
    @ApiOperation("获取知识管理列表（共享人员）")
    @GetMapping("/ShareUser/{documentId}")
    public ActionResult shareUserList(@PathVariable("documentId") String documentId) {
        List<DocumentShareEntity> data = documentService.getShareUserList(documentId);
        List<DocumentSuserListVO> list = JsonUtil.getJsonToList(data, DocumentSuserListVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        return ActionResult.success(vo);
    }



    /**
     * 上传文件
     *
     * @return
     */
    @ApiOperation("知识管理上传文件")
    @PostMapping("/Uploader")
    public ActionResult uploader(DocumentUploader documentUploader) {
        String fileName = documentUploader.getFile().getOriginalFilename();
        List<DocumentEntity> data = documentService.getAllList(documentUploader.getParentId());
        String finalFileName = fileName;
        data=data.stream().filter(t-> finalFileName.equals(t.getFullName())).collect(Collectors.toList());
        if(data.size()>0){
            fileName=DateUtil.getNow("+8")+"-"+fileName;
        }
        String fileType = UpUtil.getFileType(documentUploader.getFile());
        String name = RandomUtil.uuId();
        String filePath = fileApi.getPath(FileTypeEnum.DOCUMENT);
        //验证类型
        if (!OptimizeUtil.fileType(fileApi.getPath(FileTypeEnum.ALLOWUPLOADFILETYPE), fileType)) {
            return ActionResult.fail("上传失败，文件格式不允许上传");
        }
        FileUtil.upFile(documentUploader.getFile(), filePath, name + "." + fileType);
        DocumentEntity entity = new DocumentEntity();
        entity.setType(1);
        entity.setFullName(fileName);
        entity.setParentId(documentUploader.getParentId());
        entity.setFileExtension(fileType);
        entity.setFilePath(name + "." + fileType);
        entity.setFileSize(String.valueOf(documentUploader.getFile().getSize()));
        entity.setDeleteMark(0);
        documentService.create(entity);
        return ActionResult.success("上传成功");
    }

    /**
     * 合并文件
     *
     * @param guid
     * @return
     */
    /*@ApiOperation("（未找到）知识管理合并文件")
    @PostMapping("/Merge/{guid}")
    public ActionResult Merge(@PathVariable("guid") String guid, String fileName, String folderId) {
        //临时文件
        String temp = configValueUtil.getTemporaryFilePath() + guid;
        File file = new File(temp);
        //保存文件
        UserInfo userInfo = userProvider.get();
        String userId = userInfo.getUserId();
        String tenantId = userInfo.getTenantId();
        String time = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        String path = configValueUtil.getDocumentFilePath() + "\\" + tenantId + "\\" + userId + "\\" + time;
        String fileType = "";
        String name = RandomUtil.uuId();
        File partFile = null;
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                    partFile = new File(path);
                    if (!partFile.exists()) {
                        partFile.mkdirs();
                    }
                    partFile = new File(path + "\\" + name + "." + fileType);
                    for (int i = 0; i < files.length; i++) {
                        File s = new File(temp, i + ".part");
                        FileOutputStream destTempfos = new FileOutputStream(partFile, true);
                        FileUtils.copyFile(s, destTempfos);
                        destTempfos.close();
                    }
                    FileUtils.deleteDirectory(file);
                }
            }
            DocumentEntity entity = new DocumentEntity();
            entity.setFType(1);
            entity.setFParentId(folderId);
            entity.setFFullName(fileName);
            entity.setFFileExtension(fileType);
            entity.setFFilePath(tenantId + "\\" + userId + "\\" + time + "\\" + name + "." + fileType);
            entity.setFFileSize(String.valueOf(partFile.length()));
            entity.setFDeleteMark(0);
            documentService.create(entity);
            return ActionResult.success("合并成功");
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (Exception e1) {
                System.out.println(e1.getMessage());
            }
            System.out.println(e.getMessage());
            return ActionResult.fail("上传失败");
        }
    }*/

    /**
     * 下载文件
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取下载文件链接")
    @PostMapping("/Download/{id}")
    public ActionResult download(@PathVariable("id") String id) {
        UserInfo userInfo = userProvider.get();
        DocumentEntity entity = documentService.getInfo(id);
        DownloadVO vo = DownloadVO.builder().build();
        if (entity != null) {
            String filePath = fileApi.getPath(FileTypeEnum.DOCUMENT) + entity.getFilePath();
            String name =entity.getFilePath();
            if (FileUtil.fileIsFile(filePath)) {
                String fileName = userInfo.getId() + "#" + name+"#"+"document#"+entity.getFullName()+"."+entity.getFileExtension();
                vo = DownloadVO.builder().name(entity.getFullName()).url(UploaderUtil.uploaderFile(fileName)).build();
            }
            return ActionResult.success(vo);
        }
        return ActionResult.fail("文件不存在");
    }

    /**
     * 回收站（彻底删除）
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("回收站（彻底删除）")
    @DeleteMapping("/Trash/{id}")
    public ActionResult trashdelete(@PathVariable("id") String id) {
        documentService.trashdelete(id);
        return ActionResult.success("删除成功");
    }

    /**
     * 回收站（还原文件）
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("回收站（还原文件）")
    @PostMapping("/Trash/{id}/Actions/Recovery")
    public ActionResult trashRecovery(@PathVariable("id") String id) {
        boolean flag = documentService.trashRecovery(id);
        if (flag == false) {
            return ActionResult.fail("还原失败，数据不存在");
        }
        return ActionResult.success("还原成功");
    }

    /**
     * 共享文件（创建）
     *
     * @return
     */
    @ApiOperation("分享文件/文件夹")
    @PostMapping("/{id}/Actions/Share")
    public ActionResult shareCreate(@PathVariable("id") String id, @RequestBody DocumentShareForm documentShareForm) {
        String[] shareUserId = documentShareForm.getUserId().split(",");
        boolean flag = documentService.sharecreate(id, shareUserId);
        if (flag == false) {
            return ActionResult.fail("操作失败，原文件不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 共享文件（取消）
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("取消分享文件/文件夹")
    @DeleteMapping("/{id}/Actions/Share")
    public ActionResult shareCancel(@PathVariable("id") String id) {
        boolean flag = documentService.shareCancel(id);
        if (flag == false) {
            return ActionResult.fail("操作失败，原文件不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 文件/夹移动到
     *
     * @param id   主键值
     * @param toId 将要移动到Id
     * @return
     */
    @ApiOperation("移动文件/文件夹")
    @PutMapping("/{id}/Actions/MoveTo/{toId}")
    public ActionResult moveTo(@PathVariable("id") String id, @PathVariable("toId") String toId) {
        if(id.equals(toId)){
            return ActionResult.fail("不能移动到自己的文件夹");
        }
        boolean flag = documentService.moveTo(id, toId);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

}
