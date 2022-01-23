package smart.form.util;

import smart.file.FileApi;
import smart.emnus.FileTypeEnum;
import smart.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

 /**
  * 上传附件处理
  *
  * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
  * @date 2021/3/15 11:55
  */
@Component
public class FileManageUtil {

    @Autowired
    private FileApi fileApi;

    // 添加附件：将临时文件夹的文件拷贝到正式文件夹里面

    /**
     * 添加附件：将临时文件夹的文件拷贝到正式文件夹里面
     * @param data list集合
     */
    public void createFile(List<FileModel> data) {
        if (data != null && data.size() > 0) {
            String temporaryFilePath = fileApi.getPath(FileTypeEnum.TEMPORARY);
            String systemFilePath = fileApi.getPath(FileTypeEnum.WORKFLOW);
            for (FileModel item : data) {
                FileUtil.copyFile(temporaryFilePath + item.getFileId(), systemFilePath + item.getFileId());
            }
        }
    }

    /**
     * 更新附件
     * @param data list集合
     */
    public void updateFile(List<FileModel> data) {
        if (data != null && data.size() > 0) {
            String temporaryFilePath = fileApi.getPath(FileTypeEnum.TEMPORARY);
            String systemFilePath = fileApi.getPath(FileTypeEnum.WORKFLOW);
            for (FileModel item : data) {
                if ("add".equals(item.getFileType())) {
                    FileUtil.copyFile(temporaryFilePath + item.getFileId(), systemFilePath + item.getFileId());
                } else if ("delete".equals(item.getFileType())) {
                    FileUtil.deleteFile(systemFilePath + item.getFileId());
                }
            }
        }
    }

    /**
     * 删除附件
     * @param data list集合
     */
    public void deleteFile(List<FileModel> data) {
        if (data != null && data.size() > 0) {
            String systemFilePath = fileApi.getPath(FileTypeEnum.WORKFLOW);
            for (FileModel item : data) {
                FileUtil.deleteFile(systemFilePath + item.getFileId());
            }
        }
    }
}
