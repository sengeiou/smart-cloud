package smart.exception;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 微信错误码说明，请阅读： <a href="http://mp.weixin.qq.com/wiki/10/6380dc743053a91c544ffd2b7c959166.html">全局返回码说明</a>
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:57
 */
public class WxError implements Serializable {

  private static final long serialVersionUID = 7869786563361406291L;

  @JSONField(name = "errcode")
  private int errorCode;

  @JSONField(name = "errmsg")
  private String errorMsg;

  private String json;

  public static WxError fromJson(String json) {
	WxError error = JSONObject.parseObject(json, WxError.class);
  error.setJson(json);
    return error;
  }

  public static WxError fromJson(JSONObject jsonObject) {
    WxError error = WxError.newBuilder().setErrorCode(jsonObject.getInteger("errcode")).setErrorMsg(jsonObject.getString("errmsg")).build();

    return error;
  }
  public static Builder newBuilder() {
    return new Builder();
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMsg() {
    return this.errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public String getJson() {
    return this.json;
  }

  public void setJson(String json) {
    this.json = json;
  }

  @Override
  public String toString() {
    if (this.json != null) {
      return this.json;
    }
    return "错误: Code=" + this.errorCode + ", Msg=" + this.errorMsg;
  }

  public static class Builder {
    private int errorCode;
    private String errorMsg;

    public Builder setErrorCode(int errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    public Builder setErrorMsg(String errorMsg) {
      this.errorMsg = errorMsg;
      return this;
    }

    public WxError build() {
      WxError wxError = new WxError();
      wxError.setErrorCode(this.errorCode);
      wxError.setErrorMsg(this.errorMsg);
      return wxError;
    }
  }
}
