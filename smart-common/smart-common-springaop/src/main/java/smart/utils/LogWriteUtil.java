package smart.utils;

import lombok.Data;

/**
 * 日志分类
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021年3月13日 上午9:18
 */
@Data
public class LogWriteUtil {
    public static final String NOTWRITE = "/Base/Log/writeLogRequest";

    public static final String NOTWRITETWO = "/Base/SysConfig/getInfo";

    public static final String WRITELOG = "/Logout";

}
