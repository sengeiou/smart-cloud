package smart.base;

import smart.base.ActionResultCode;

/**
 * @Author wangpeng
 * @Date 2018/4/1216:24
 */
public interface ApplicationCode {

    Integer getCode();
    void setCode(Integer code);

    String getMessage();
    void setMessage(String message);

    default boolean isSuccess() {
        return ActionResultCode.Success.getCode().equals(this.getCode());
    }

}
