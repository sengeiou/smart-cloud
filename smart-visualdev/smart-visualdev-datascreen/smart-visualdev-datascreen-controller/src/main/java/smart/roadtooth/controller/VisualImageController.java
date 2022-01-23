package smart.roadtooth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.config.ConfigValueUtil;
import smart.roadtooth.model.ImageVO;
import smart.roadtooth.util.VisualImageEnum;
import smart.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 大屏图片
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "大屏图片",description = "Images")
@RestController
@RequestMapping("/DataScreen/Images")
public class VisualImageController {

    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 上传图片
     *
     * @param file 文件
     * @param type 哪个文件夹
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("上传图片")
    @PostMapping("/{type}/Uploader")
    public ActionResult uploader(MultipartFile file, @PathVariable("type") String type) {
        ImageVO vo = new ImageVO();
        if(VisualImageEnum.getByMessage(type)) {
            String filePath = configValueUtil.getBiVisualPath() + File.separator + type;
            String fileName = RandomUtil.uuId() + "." + UpUtil.getFileType(file);
            //保存文件
            FileUtil.upFile(file, filePath, fileName);
            vo.setFileName(fileName);
        }
        return ActionResult.success(vo);
    }

    /**
     * 查看图片
     *
     * @param type     哪个文件夹
     * @param fileName 文件名称
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("查看图片")
    @GetMapping("/{type}/{fileName}")
    public void img(@PathVariable("type") String type, @PathVariable("fileName") String fileName) {
        String paths = configValueUtil.getBiVisualPath() + type + File.separator + fileName;
        File file = new File(paths);
        if (file.exists()) {
            if (fileName.contains(".svg")) {
                DownUtil.dowloadSvgFile(file);
            } else {
                DownUtil.dowloadFile(paths);
            }
        }
    }

    /**
     * 获取图片列表
     *
     * @param type 哪个文件夹
     * @return
     */
    @NoDataSourceBind()
    @ApiOperation("获取图片列表")
    @GetMapping("/{type}")
    public ActionResult<ImageVO> getFile(@PathVariable("type") String type) {
        List<ImageVO> vo = new ArrayList<>();
        String paths = configValueUtil.getBiVisualPath() + type;
        List<File> fileList = FileUtil.getFile(new File(paths));
        for (File file : fileList) {
            ImageVO imageVO = new ImageVO();
            imageVO.setFileName(file.getName());
            vo.add(imageVO);
        }
        return ActionResult.success(vo);
    }

}

