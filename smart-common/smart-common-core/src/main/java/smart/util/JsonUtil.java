package smart.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import smart.exception.DataException;

import java.util.List;
import java.util.Map;

/**
 * JSON转换工具类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
public class JsonUtil {

    /**
     * list转成JSONField
     * @param lists
     * @return
     */
    public static List listToJsonfield(List lists){
        //空的也显示
        String jsonStr = JSONArray.toJSONString(lists, SerializerFeature.WriteMapNullValue);
        //空的不显示
        List list = JSONArray.parseObject(jsonStr,List.class);
        return  list;
    }

    /**
     * 对象转成Map
     * @param object
     * @return
     */
    public static Map<String, Object> entityToMap(Object object){
        String jsonStr = JSONObject.toJSONString(object);
        Map<String,Object> map = JSONObject.parseObject(jsonStr, new TypeReference<Map<String, Object>>(){});
        return  map;
    }

    public static Map<String, String> entityToMaps(Object object){
        String jsonStr = JSONObject.toJSONString(object);
        Map<String,String> map = JSONObject.parseObject(jsonStr, new TypeReference<Map<String, String>>(){});
        return  map;
    }

    /**
     * String转成Map
     * @param object
     * @return
     */
    public static Map<String, Object> stringToMap(String object){
        Map<String,Object> map = JSONObject.parseObject(object, new TypeReference<Map<String, Object>>(){});
        return  map;
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     * @param jsonData JSON数据
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    public static <T> T getJsonToBean(String jsonData, Class<T> clazz) {
        return JSON.parseObject(jsonData, clazz);
    }

    /**
     * 功能描述：把JSON数据转换成JSONArray数据
     * @param json
     * @return
     */
    public static JSONArray getJsonToJsonArray(String json) {   return JSONArray.parseArray(json);    }

    /**
     * 功能描述：把List数据转换成JSONArray数据
     * @param list
     * @param <T>
     * @return
     */
    public static <T>JSONArray getListToJsonArray(List<T> list){
        return JSONArray.parseArray(JsonUtil.getObjectToString(list));
    }

    /**
     * 功能描述：把java对象转换成JSON数据
     * @param object java对象
     * @return JSON数据
     */
    public static String getObjectToString(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 功能描述：把java对象转换成JSON数据,时间格式化
     * @param object java对象
     * @return JSON数据
     */
    public static String getObjectToStringDateFormat(Object object,String dateFormat) {
        return JSON.toJSONStringWithDateFormat(object, dateFormat,SerializerFeature.WriteMapNullValue);
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


    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     * @param jsonData JSON数据
     * @param clazz 指定的java对象
     * @return List<T>
     */
    public static <T> List<T> getJsonToList(String jsonData, Class<T> clazz) {
        return JSON.parseArray(jsonData, clazz);
    }

    /**
     * 功能描述：把JSON数据转换成较为复杂的List<Map<String, Object>>
     * @param jsonData JSON数据
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getJsonToListMap(String jsonData) {
        return JSON.parseObject(jsonData, new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * 功能描述：把JSONArray数据转换成较为复杂的List<Map<String, Object>>
     * @param jsonArray JSONArray数据
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getJsonToList(JSONArray jsonArray) {
        return JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     * @param dto dto对象
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    public static <T> T getJsonToBean(Object dto, Class<T> clazz){
        return JSON.parseObject(getObjectToString(dto), clazz);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     * @param dto dto对象
     * @param clazz 指定的java对象
     * @return List<T>
     */
    public static <T> List<T> getJsonToList(Object dto, Class<T> clazz) {
        return JSON.parseArray(getObjectToString(dto), clazz);
    }

}
