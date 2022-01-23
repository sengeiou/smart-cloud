package smart.form.util;

import lombok.Data;

/**
 * 附件模型
 *
 * @author 开发平台组
 * @version V1.2.191207
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
public class FileModel {
    private String fileId;
    private String fileName;
    private String fileSize;
    private String fileTime;
    private String fileState;
    private String fileType;
}
