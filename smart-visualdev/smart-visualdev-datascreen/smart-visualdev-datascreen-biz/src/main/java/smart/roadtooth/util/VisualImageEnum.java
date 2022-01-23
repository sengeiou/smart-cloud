package smart.roadtooth.util;

public enum VisualImageEnum {

    BG(0,"bg"),//背景图片
    BORDER(1,"border"),//图片框
    SOURCE(2,"source"),//图片
    BANNER(3,"banner"),//banner
    SCREENSHOT(4,"screenShot");//大屏截图

    private int code;
    private String message;

    VisualImageEnum(int code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    /**
     * 判断名称是否存在
     * @return boolean
     */
    public static boolean getByMessage(String massage) {
        for (VisualImageEnum status : VisualImageEnum.values()) {
            if (status.getMessage().equals(massage)) {
                return true;
            }
        }
        return false;
    }
}
