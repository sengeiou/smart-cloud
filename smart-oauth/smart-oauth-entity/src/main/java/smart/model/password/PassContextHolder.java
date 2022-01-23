package smart.model.password;

/**
 * 给token提供username
 */
public class PassContextHolder {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    //设置用户名
    public static void setUserName(String dbName) {
        threadLocal.set(dbName);
    }
    //获取当前用户
    public static String getUserName() {
        String str = threadLocal.get();
        return str;
    }
    //移除当前线程变量
    public static void removeUserName() {
        threadLocal.remove();
    }
}
