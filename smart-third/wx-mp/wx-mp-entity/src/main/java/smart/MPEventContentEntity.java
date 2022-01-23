package smart;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 事件内容
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("wechat_mpeventcontent")
public class MPEventContentEntity {
    /**
     * 事件key
     */
    @TableId("F_EVENTKEY")
    private String eventKey;

    /**
     * 文本内容
     */
    @TableField(value = "F_CONTENT",fill = FieldFill.UPDATE)
    private String content;
}
