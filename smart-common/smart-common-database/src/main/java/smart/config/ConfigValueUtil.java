package smart.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:47
 */
@Data
@Component
@ConfigurationProperties(prefix = "config")
public class ConfigValueUtil {

    /**
     *环境路径
     */
    @Value("${config.Path}")
    private String path;
    /**
     *数据库备份文件路径
     */
    @Value("${config.DataBackupFilePath}")
    private String dataBackupFilePath;
    /**
     *临时文件存储路径
     */
    @Value("${config.TemporaryFilePath}")
    private String temporaryFilePath;
    /**
     *系统文件存储路径
     */
    @Value("${config.SystemFilePath}")
    private String systemFilePath;
    /**
     *文件模板存储路径
     */
    @Value("${config.TemplateFilePath}")
    private String templateFilePath;
    /**
     *代码模板存储路径
     */
    @Value("${config.TemplateCodePath}")
    private String templateCodePath;
    /**
     *邮件文件存储路径
     */
    @Value("${config.EmailFilePath}")
    private String emailFilePath;
    /**
     *大屏图片存储目录
     */
    @Value("${config.BiVisualPath}")
    private String biVisualPath;
    /**
     *文档管理存储路径
     */
    @Value("${config.DocumentFilePath}")
    private String documentFilePath;
    /**
     *文件在线预览存储pdf
     */
    @Value("${config.DocumentPreviewPath}")
    private String documentPreviewPath;
    /**
     *用户头像存储路径
     */
    @Value("${config.UserAvatarFilePath}")
    private String userAvatarFilePath;
    /**
     *IM聊天图片+语音存储路径
     */
    @Value("${config.IMContentFilePath}")
    private String imContentFilePath;
    /**
     *微信公众号资源文件存储路径
     */
    @Value("${config.MPMaterialFilePath}")
    private String mpMaterialFilePath;
    /**
     *微信公众号允许上传文件类型
     */
    @Value("${config.MPUploadFileType}")
    private String mpUploadFileType;
    /**
     *微信允许上传文件类型
     */
    @Value("${config.WeChatUploadFileType}")
    private String weChatUploadFileType;
    /**
     *允许上传文件类型
     */
    @Value("${config.AllowUploadFileType}")
    private String allowUploadFileType;
    /**
     *允许图片类型
     */
    @Value("${config.AllowUploadImageType}")
    private String allowUploadImageType;
    /**
     *前端文件目录
     */
    @Value("${config.ServiceDirectoryPath}")
    private String serviceDirectoryPath;
    /**
     *代码生成器命名空间
     */
    @Value("${config.CodeAreasName}")
    private String codeAreasName;

    /**
     *后端文件目录
     */
    @Value("${config.WebDirectoryPath:}")
    private String webDirectoryPath;
    /**
     *前端附件文件目录
     */
    @Value("${config.WebAnnexFilePath}")
    private String webAnnexFilePath;

    public void setDataBackupFilePath(String dataBackupFilePath) {
        this.dataBackupFilePath = path +dataBackupFilePath+ File.separator;
    }

    public void setTemporaryFilePath(String temporaryFilePath) {
        this.temporaryFilePath = path +temporaryFilePath+ File.separator;
    }

    public void setSystemFilePath(String systemFilePath) {
        this.systemFilePath = path +systemFilePath+ File.separator;
    }

    public void setTemplateFilePath(String templateFilePath) {
        this.templateFilePath = path +templateFilePath+ File.separator;
    }

    public void setTemplateCodePath(String templateCodePath) {
        this.templateCodePath = path +templateCodePath+ File.separator;
    }

    public void setEmailFilePath(String emailFilePath) {
        this.emailFilePath = path +emailFilePath+ File.separator;
    }

    public void setDocumentPreviewPath(String documentPreviewPath) {
        this.documentPreviewPath = path +documentPreviewPath+ File.separator;
    }

    public void setUserAvatarFilePath(String userAvatarFilePath) {
        this.userAvatarFilePath = path +userAvatarFilePath+ File.separator;
    }

    public void setImContentFilePath(String imContentFilePath) {
        this.imContentFilePath = path + imContentFilePath + File.separator;
    }

    public void setMpMaterialFilePath(String mpMaterialFilePath) {
        this.mpMaterialFilePath = path + mpMaterialFilePath + File.separator;
    }

    public void setDocumentFilePath(String documentFilePath) {
        this.documentFilePath = path +documentFilePath+ File.separator;
    }

    public void setWebAnnexFilePath(String webAnnexFilePath) {
        this.webAnnexFilePath = path +webAnnexFilePath+ File.separator;
    }

    public void setBiVisualPath(String biVisualPath) {
        this.biVisualPath = path +biVisualPath+ File.separator;
    }


    /**
     *软件的错误报告
     */
    @Value("${config.ErrorReport:}")
    private String errorReport;
    /**
     *软件的错误报告发给谁
     */
    @Value("${config.ErrorReportTo:}")
    private String errorReportTo;
    /**
     *系统日志启用：true、false
     */
    @Value("${config.RecordLog:}")
    private String recordLog;
    /**
     *多租户启用：true、false
     */
    @Value("${config.MultiTenancy:false}")
    private String multiTenancy;
    /**
     *阿里云
     */
    @Value("${config.AccessKeyId:}")
    private String accessKeyId;
    /**
     *阿里云
     */
    @Value("${config.AccessKeySecret:}")
    private String accessKeySecret;
    /**
     *版本
     */
    @Value("${config.SoftVersion:}")
    private String softVersion;
    /**
     *推送是否启动：false、true
     */
    @Value("${config.IgexinEnabled:}")
    private String igexinEnabled;
    /**
     *APPID
     */
    @Value("${config.IgexinAppid:}")
    private String igexinAppid;
    /**
     *APPKEY
     */
    @Value("${config.IgexinAppkey:}")
    private String igexinAppkey;
    /**
     *MASTERSECRET
     */
    @Value("${config.IgexinMastersecret:}")
    private String igexinMastersecret;

    @Value("${config.AppUpdateContent:}")
    private String appUpdateContent;
    @Value("${config.AppVersion:}")
    private String appVersion;

    /**
     *-------------租户库配置-----------
     */



    /**
     *-------------跨域配置-----------
     */
    @Value("${config.Origins:}")
    private String origins;
    @Value("${config.Methods:}")
    private String methods;

    /**
     *-------------是否开启测试环境，admin账户可以无限登陆，并且无法修改密码-----------
     */
    @Value("${config.TestVersion:}")
    private String testVersion;
}
