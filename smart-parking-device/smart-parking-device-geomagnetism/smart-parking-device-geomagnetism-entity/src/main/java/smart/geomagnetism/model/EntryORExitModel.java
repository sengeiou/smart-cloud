package smart.geomagnetism.model;

import lombok.Data;

/**
 * 描述：
 */
@Data
public class EntryORExitModel {
    private String imei;
    //0：进场 1：离场 2：距离异常 3：解除距离异常 4：修改车牌

    private String deviceStatus;

    private String images;

    private String plate;

    private String pre;

    //取证是否更新车位状态 0:不更新 1：更新 默认为0
    private String isUpdate;

    private int color;

    //消息id(具有唯一性)
    private String messageId;

    //方法类型(MqMethodType枚举)
    private int mqMethodType;

    //重试次数对应的redis的key
    private String tryCountKey;
}
