package smart.base.util.genUtil.custom;


import smart.util.JsonUtil;
import smart.base.service.DataInterfaceService;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.util.CacheKeyUtil;
import smart.util.RedisUtil;
import smart.util.context.SpringContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DynamicUtil {

    private RedisUtil redisUtil;
    private CacheKeyUtil cacheKeyUtil;
    private DataInterfaceService dataInterfaceService;

    //获取远端数据转换关键词返回
    public Map<String, Object> dynamicKeyData(FieLdsModel model, Map<String, Object> keyJsonMap) throws IOException {

//        long startTime = System.currentTimeMillis(); //获取开始时间
        redisUtil = SpringContext.getBean(RedisUtil.class);
        cacheKeyUtil = SpringContext.getBean(CacheKeyUtil.class);
        if (redisUtil.exists(cacheKeyUtil.getDynamic() + model.getConfig().getPropsUrl())) {
            model = JsonUtil.getJsonToBean(redisUtil.getString(cacheKeyUtil.getDynamic() + model.getConfig().getPropsUrl()).toString(), FieLdsModel.class);
        } else {
            //获取远端数据
            model = dynamicData(model);
            redisUtil.insert(cacheKeyUtil.getDynamic() + model.getConfig().getPropsUrl(), model);
        }
        String keyStr = keyJsonMap.get(model.getVModel()).toString();

        if (model.getSlot() != null && model.getSlot().getOptions() != null) {
            List<Map<String, Object>> modelOpt = JsonUtil.getJsonToListMap(model.getSlot().getOptions());
            for (Map<String, Object> map : modelOpt) {
                if (map.get(model.getConfig().getProps().getValue()).toString().equals(keyStr)) {
                    keyJsonMap.put(model.getVModel(), map.get(model.getConfig().getProps().getLabel()).toString());
                }

            }
        }
//        long endTime = System.currentTimeMillis(); //获取结束时间
//        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
        return keyJsonMap;
    }


    //获取远端数据
    public FieLdsModel dynamicData(FieLdsModel model) throws IOException {
        dataInterfaceService = SpringContext.getBean(DataInterfaceService.class);
        redisUtil = SpringContext.getBean(RedisUtil.class);
        cacheKeyUtil = SpringContext.getBean(CacheKeyUtil.class);
        if (redisUtil.exists(cacheKeyUtil.getDynamic() + model.getConfig().getPropsUrl())) {
            model = JsonUtil.getJsonToBean(String.valueOf(redisUtil.getString(cacheKeyUtil.getDynamic() + model.getConfig().getPropsUrl())), FieLdsModel.class);
        } else {
            //获取远端数据
            Object object = dataInterfaceService.infoToId(model.getConfig().getPropsUrl());
            Map<String, Object> dynamicMap= JsonUtil.entityToMap(object);
            if (dynamicMap.get("data") != null) {
                List<Map<String, Object>> dataList = JsonUtil.getJsonToListMap(dynamicMap.get("data").toString());
                model.getSlot().setOptions(JsonUtil.getObjectToString(dataList));
            }
            redisUtil.insert(cacheKeyUtil.getDynamic() + model.getConfig().getPropsUrl(), model);
        }
        return model;
    }

}
