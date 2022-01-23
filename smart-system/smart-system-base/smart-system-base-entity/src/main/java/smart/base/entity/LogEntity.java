package smart.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统日志
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("base_syslog")
public class LogEntity {
    /**
     * 日志主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 用户主键
     */
    @TableField("F_USERID")
    private String userId;

    /**
     * 用户主键
     */
    @TableField("F_USERNAME")
    private String userName;

    /**
     * 日志分类
     */
    @TableField("F_CATEGORY")
    private Integer category;

    /**
     * 日志类型
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 日志级别
     */
    @TableField("F_LEVEL")
    private Integer levels;

    /**
     * IP地址
     */
    @TableField("F_IPADDRESS")
    private String iPAddress;

    /**
     * IP所在城市
     */
    @TableField("F_IPADDRESSNAME")
    private String iPAddressName;

    /**
     * 请求地址
     */
    @TableField("F_REQUESTURL")
    private String requestURL;

    /**
     * 请求方法
     */
    @TableField("F_REQUESTMETHOD")
    private String requestMethod;

    /**
     * 请求耗时
     */
    @TableField("F_REQUESTDURATION")
    private Integer requestduration;

    /**
     * 日志摘要
     */
    @TableField("F_ABSTRACTS")
    private String abstracts;

    /**
     * 日志内容
     */
    @TableField("F_JSON")
    private String json;

    /**
     * 平台设备
     */
    @TableField("F_PLATFORM")
    private String platForm;

    /**
     * 操作日期
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 功能主键
     */
    @TableField("F_MODULEID")
    private String moduleId;

    /**
     * 功能名称
     */
    @TableField("F_MODULENAME")
    private String moduleName;

    /**
     * 对象id
     */
    @TableField("F_OBJECTID")
    private String objectId;

}
