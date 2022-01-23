package com.bstek.ureport.console.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class RandomUtil {

    /**
     * 生成主键id
     * @return
     */
    public static String uuId() {
        String uuid = UUID.randomUUID().toString();
        String temp = uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
        return temp;
    }

    /**
     * 生成排序编码
     * @return
     */
    public static Long parses() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Long time = Long.parseLong(sdf.format(date));
        return time;
    }

}
