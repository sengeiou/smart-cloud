package smart.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

public class JSONUtil {

    /**
     * list转成JSONField
     * @param lists
     * @return
     */
    public static List ListToJSONField(List lists){
        //空的也显示
        String jsonStr = JSONArray.toJSONString(lists, SerializerFeature.WriteMapNullValue);
        //空的不显示
//        String jsonStr = JSONArray.toJSONString(lists);
        List list = JSONArray.parseObject(jsonStr,List.class);
        return  list;
    }

    /**
     * 对象转成Map
     * @param object
     * @return
     */
    public static Map<String, Object> EntityToMap(Object object){
        String jsonStr = JSONObject.toJSONString(object);
        Map<String,Object> map = JSONObject.parseObject(jsonStr, new TypeReference<Map<String, Object>>(){});
        return  map;
    }

    /**
     * String转成Map
     * @param object
     * @return
     */
    public static Map<String, Object>  StringToMap(String object){
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
        return JSONArray.parseArray(JSONUtil.getObjectToString(list));
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
