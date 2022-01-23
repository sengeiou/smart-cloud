package smart.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统配置
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("base_sysconfig")
public class SysConfigEntity {
    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 名称
     */
    @TableField("F_NAME")
    private String name;

    /**
     * 键
     */
    @TableField("F_KEY")
    private String fkey;

    /**
     * 值
     */
    @TableField("F_VALUE")
    private String value;

    /**
     * 分类
     */
    @TableField("F_CATEGORY")
    private String category;
}
