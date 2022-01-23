package smart.util.wxutil;

/**
 * 多媒体文件类型
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:53
 */
public enum MediaFileType {
	/**
	 * 图文
	 */
	News("news"),
	/**
	 * 图片
	 */
	Image("image"),
	/**
	 * 语音
	 */
	Voice("voice"),
	/**
	 * 视频
	 */
	Video("video"),
	/**
	 * 缩略图
	 */
	Thumb("thumb"),
	/**
	 * 文件
	 */
	File("file");

	private String message;

	MediaFileType(String message) {
	     this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
