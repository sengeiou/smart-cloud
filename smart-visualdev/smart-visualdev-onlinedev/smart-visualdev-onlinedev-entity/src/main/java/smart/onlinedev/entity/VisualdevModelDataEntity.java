package smart.onlinedev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * 0代码功能数据表
 * 版本： V3.0.0
 * 版权： 智慧停车公司
 * 作者： 管理员/admin
 * 日期： 2020-07-24 11:59
 */
@Data
@TableName("base_visualdev_modeldata")
public class VisualdevModelDataEntity {


    @TableId("F_ID")
    private String id;


    @TableField("F_VISUALDEVID")
    private String visualDevId;


    @TableField("F_SORTCODE")
    private Long sortcode;


    @TableField("F_ENABLEDMARK")
    private Integer enabledmark;


    @TableField("F_CREATORTIME")
    private Date creatortime;


    @TableField("F_CREATORUSERID")
    private String creatoruserid;


    @TableField("F_LASTMODIFYTIME")
    private Date lastmodifytime;


    @TableField("F_LASTMODIFYUSERID")
    private String lastmodifyuserid;


    @TableField("F_DELETEMARK")
    private Integer deletemark;


    @TableField("F_DELETETIME")
    private Date deletetime;


    @TableField("F_DELETEUSERID")
    private String deleteuserid;
    @TableField("F_DATA")
    private String data;


}

