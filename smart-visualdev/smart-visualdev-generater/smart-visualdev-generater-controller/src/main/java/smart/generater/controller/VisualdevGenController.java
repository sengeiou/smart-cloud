package smart.generater.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.*;
import smart.base.model.DownloadCodeForm;
import smart.base.service.VisualdevService;
import smart.base.util.genUtil.ReadFile;
import smart.base.vo.DownloadVO;
import smart.base.vo.ListVO;
import smart.config.ConfigValueUtil;
import smart.exception.DataException;
import smart.generater.service.VisualdevGenService;
import smart.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

/**
 * 可视化开发功能表
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-04-02
 */
@Api(tags = "代码生成器", description = "VisualDevelopmentGen")
@RestController
@RequestMapping("/Generater")
public class VisualdevGenController {

    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VisualdevGenService visualdevGenService;


    /**
     * 下载文件
     *
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("下载文件")
    @GetMapping("/DownloadVisCode")
    public void downloadCode() throws DataException {
        HttpServletRequest request = ServletUtil.getRequest();
        String reqJson = request.getParameter("encryption");
        String fileNameAll = DesUtil.aesDecode(reqJson);
        if (!StringUtil.isEmpty(fileNameAll)) {
            String token = fileNameAll.split("#")[0];
            if (redisUtil.exists(token)) {
                String fileName = fileNameAll.split("#")[1];
                String path = configValueUtil.getServiceDirectoryPath() + fileName;
                if (FileUtil.fileIsExists(path)) {
                    String zipFile = configValueUtil.getTemporaryFilePath() + fileName + ".zip";
                    // 调用压缩方法
                    FileUtil.toZip(zipFile, true, path);
                    DownUtil.dowloadFile(zipFile, fileName + ".zip");
                }else {
                    throw new DataException("文件不存在");
                }
            }
        }else {
            throw new DataException("token验证失败");
        }
    }

    @ApiOperation("获取命名空间")
    @GetMapping("/AreasName")
    public ActionResult getAreasName() {
        String areasName = configValueUtil.getCodeAreasName();
        List<String> areasNameList = new ArrayList(Arrays.asList(areasName.split(",")));
        return ActionResult.success(areasNameList);
    }

    @ApiOperation("下载代码")
    @PostMapping("/{id}/Actions/DownloadCode")
    @Transactional
    public ActionResult downloadCode(@PathVariable("id") String id, @RequestBody DownloadCodeForm downloadCodeForm) throws SQLException {
        UserInfo userInfo = userProvider.get();
        smart.base.vo.DownloadVO vo;
        String fileName = visualdevGenService.codeGengerate(id, downloadCodeForm);
        vo = DownloadVO.builder().name(fileName).url(UploaderUtil.uploaderVisualFile(userInfo.getId() + "#" + fileName)).build();
        if (vo == null) {
            return ActionResult.fail("下载失败，数据不存在");
        }
        return ActionResult.success(vo);
    }


    /**
     * 输出移动开发模板
     *
     * @return
     */
    @ApiOperation("预览代码")
    @PostMapping("/{id}/Actions/CodePreview")
    public ActionResult codePreview(@PathVariable("id") String id, @RequestBody DownloadCodeForm downloadCodeForm) throws SQLException {
        String fileName = visualdevGenService.codeGengerate(id, downloadCodeForm);
        List<Map<String, Object>> dataList = ReadFile.priviewCode(configValueUtil.getServiceDirectoryPath() + fileName);
        if (dataList == null && dataList.size() == 0) {
            return ActionResult.fail("预览失败，数据不存在");
        }
        ListVO<Map<String, Object>> datas = new ListVO<>();
        datas.setList(dataList);
        return ActionResult.success(datas);
    }

    /**
     * App预览(后台APP表单设计)
     *
     * @return
     */
    @ApiOperation("App预览(后台APP表单设计)")
    @PostMapping("/App/Preview")
    public ActionResult appPreview(String data) {
        String id = RandomUtil.uuId();
        redisUtil.insert(id, data, 300);
        return ActionResult.success((Object) id);
    }

    /**
     * App预览(后台APP表单设计)
     *
     * @return
     */
    @ApiOperation("App预览查看")
    @GetMapping("/App/{id}/Preview")
    public ActionResult preview(@PathVariable("id") String id) {
        if (redisUtil.exists(id)) {
            Object object = redisUtil.getString(id);
            return ActionResult.success(object);
        } else {
            return ActionResult.fail("已失效");
        }
    }

}


