package smart.emnus;

/**
 * 查询功能
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public enum SearchMethodEnum {
    /**
     * like
     */
    Contains("Contains"),
    /**
     * 等于
     */
    Equal("Equal"),
    /**
     * 不等于
     */
    NotEqual("NotEqual"),
    /**
     * 小于
     */
    LessThan("LessThan"),
    /**
     * 小于等于
     */
    LessThanOrEqual("LessThanOrEqual"),
    /**
     * 大于
     */
    GreaterThan("GreaterThan"),
    /**
     * 大于等于
     */
    GreaterThanOrEqual("GreaterThanOrEqual");

    private String message;

    SearchMethodEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
