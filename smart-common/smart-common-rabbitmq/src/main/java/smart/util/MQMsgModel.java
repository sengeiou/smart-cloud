package smart.util;

import lombok.Data;
import java.io.Serializable;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Data
public class MQMsgModel implements Serializable {
    private static final long serialVersionUID = -5156733452111427492L;

    private Object msg;
    /**
     *  推送时间点
     */
    private long time = System.currentTimeMillis();
}
