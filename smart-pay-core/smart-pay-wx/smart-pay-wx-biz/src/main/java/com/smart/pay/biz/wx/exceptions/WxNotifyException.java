package com.smart.pay.biz.wx.exceptions;

/**
 * <li>微信通知异常</li>
 *
 * @author wangpeng
 * @date 2021/11/18 14:55
 * @see com.smart.pay.biz.wx.exceptions
 * @since 1.0
 **/
public class WxNotifyException extends RuntimeException{

    private String message;
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public WxNotifyException(String message) {
        super(message);
        this.message=message;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public WxNotifyException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }
}
