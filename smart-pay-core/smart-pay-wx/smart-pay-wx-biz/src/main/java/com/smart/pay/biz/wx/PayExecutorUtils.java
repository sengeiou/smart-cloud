package com.smart.pay.biz.wx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/12/31 17:11
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
public class PayExecutorUtils {

    //线程池
    private static ExecutorService executorService = new ThreadPoolExecutor(0, 2000,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    //
    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
