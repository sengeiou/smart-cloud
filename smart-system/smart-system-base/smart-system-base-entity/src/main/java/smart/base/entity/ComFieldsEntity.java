package smart.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * 常用字段表
 * 版本： V3.0.0
 * 版权： 智慧停车公司
 * 作者： 管理员/admin
 * 日期： 2020-07-23 09:54
 */
@Data
@TableName("base_comfields")
public class ComFieldsEntity {


    @TableId("F_ID")
    private String id;


    @TableField("F_FIELDNAME")
    private String fieldName;

    @TableField("F_FIELD")
    private String field;


    @TableField("F_DATATYPE")
    private String datatype;


    @TableField("F_DATALENGTH")
    private String datalength;

    @TableField("F_ALLOWNULL")
    private String allowNull;


    @TableField("F_DESCRIPTION")
    private String description;


    @TableField("F_SORTCODE")
    private Long sortcode;


    @TableField("F_ENABLEDMARK")
    private Integer enabledmark;


    @TableField("F_CREATORTIME")
    private Date creatortime;


    @TableField("F_CREATORUSERID")
    private String creatoruserid;


    @TableField("F_LASTMODIFYTIME")
    @JSONField(name = "F_LastModifyTime")
    private Date lastmodifytime;


    @TableField("F_LASTMODIFYUSERID")
    private String lastmodifyuserid;


    @TableField("F_DELETEMARK")
    private Integer deletemark;


    @TableField("F_DELETETIME")
    private Date deletetime;


    @TableField("F_DELETEUSERID")
    private String deleteuserid;

}

