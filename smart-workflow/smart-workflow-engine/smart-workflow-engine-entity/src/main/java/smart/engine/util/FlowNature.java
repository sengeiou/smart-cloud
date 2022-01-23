package smart.engine.util;

import lombok.Data;

/**
 * 在线工作流开发
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
public class FlowNature {

    /**驳回开始**/
    public static String START = "0";

    /**驳回上一节点**/
    public static String UP = "1";

    /**系统表单**/
    public static Integer SYSTEM = 1;

    /**自定义表单**/
    public static Integer CUSTOM = 2;

    /**待办事宜**/
    public static String WAIT = "1";

    /**已办事宜**/
    public static String TRIAL = "2";

    /**抄送事宜**/
    public static String CIRCULATE = "3";

}
