package smart.roadtooth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 大屏数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("visual_data")
public class VisualDataEntity {

    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 分类id
     */
    @TableField("F_CATEGORYID")
    private String categoryId;

    /**
     * 大屏截图
     */
    @TableField("F_SCREENSHOT")
    private String screenShot;

    /**
     * 大屏密码
     */
    @TableField("F_PASSWORD")
    private String password;

    /**
     * 控件属性JSON包
     */
    @TableField("F_COMPONENT")
    private String component;

    /**
     * 控件属性JSON包
     */
    @TableField("F_DETAIL")
    private String detail;

    /**
     * 描述说明
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 发布状态 0:未发布 1已发布
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 复制id
     */
    @TableField("F_COPYID")
    private String copyId;

    /**
     * 创建时间
     */
    @TableField("F_CREATORTIME")
    private Date creatorTime;

    /**
     * 创建人
     */
    @TableField("F_CREATORUSERID")
    private String creatorUserId;

    /**
     * 修改时间
     */
    @TableField("F_LASTMODIFYTIME")
    private Date lastModifyTime;

    /**
     * 修改人
     */
    @TableField("F_LASTMODIFYUSERID")
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DELETETIME")
    private Date deleteTime;

    /**
     * 删除人
     */
    @TableField("F_DELETEUSERID")
    private String deleteUserId;

}

