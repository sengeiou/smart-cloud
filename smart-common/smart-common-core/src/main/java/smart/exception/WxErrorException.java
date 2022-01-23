package smart.exception;

/**
 * 微信异常封装
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:57
 */
public class WxErrorException extends Exception {

  private static final long serialVersionUID = -6357149550353160810L;

  private WxError error;

  public WxErrorException(WxError error) {
    super(error.toString());
    this.error = error;
  }

  public WxErrorException(WxError error, Throwable cause) {
	    super(error.toString(), cause);
	    this.error = error;
  }

  public WxError getError() {
    return this.error;
  }

}
