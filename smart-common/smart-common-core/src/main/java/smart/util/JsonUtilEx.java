package smart.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import smart.exception.DataException;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
public class JsonUtilEx {


    /**
     * 功能描述：把java对象转换成JSON数据,时间格式化
     * @param object java对象
     * @return JSON数据
     */
    public static String getObjectToStringDateFormat(Object object,String dateFormat) {
        return JSON.toJSONStringWithDateFormat(object, dateFormat,SerializerFeature.WriteMapNullValue);
    }

//    /**
//     * 功能描述：把JSON数据转换成指定的java对象列表
//     * @param jsonData JSON数据
//     * @param clazz 指定的java对象
//     * @return List<T>
//     */
//    public static <T> List<T> getJsonToListStringDateFormat(String jsonData, Class<T> clazz,String dateFormat) {
//        JSONArray jsonArray=JSONUtil.getJsonToJsonArray(jsonData);
//        JSONArray newJsonArray=JSONUtil.getJsonToJsonArray(jsonData);
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            newJsonArray.add(JSON.toJSONStringWithDateFormat(jsonObject, dateFormat,SerializerFeature.WriteMapNullValue));
//        }
//        jsonData=JSONUtil.getObjectToString(newJsonArray);
//        return JSON.parseArray(jsonData, clazz);
//    }
//
//    public static void main(String[] args) {
//        Date date=new Date();
//        String obk="[" +
//                "{\"date\":\""+date+"\"},{\"date\":\"1603165505\"}" +
//                "]";
//       List<String> list1= getJsonToList(obk,String.class);
//        List<String> list11= getJsonToListStringDateFormat(obk,String.class,"yyyy-MM-dd");
//        System.out.println("aaa");
//    }


    /**
     * 功能描述：把java对象转换成JSON数据
     * @param object java对象
     * @return JSON数据
     */
    public static String getObjectToString(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     * @param dto dto对象
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    public static <T> T getJsonToBeanEx(Object dto, Class<T> clazz) throws DataException {
        if(dto==null){
            throw new DataException("此条数据不存在");
        }
        return JSON.parseObject(getObjectToString(dto), clazz);
    }


}
