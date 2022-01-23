package smart.onlinedev.model;

/**
 * 0代码开发枚举类
 */
public enum VMDEnum {

    DICTIONARY("dictionary","数据字典DataType"),
    STATIC("static","静态数据DataType"),
    KEYJSONMAP("keyJsonMap","查询字段数据"),
    VALUE("value","级联选择静态模板值"),
    DYNAMIC("dynamic","远程数据DataType"),
    TIMECONTROL("timeControl","远程数据"),
    LIST("list","可视化数据列表结果key");

    private String type;
    private String message;

    VMDEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }


    public String getType() {
        return type;
    }
}
