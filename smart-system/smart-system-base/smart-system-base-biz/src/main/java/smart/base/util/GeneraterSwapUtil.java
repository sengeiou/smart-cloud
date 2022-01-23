package smart.base.util;

import smart.util.StringUtil;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.PositionEntity;
import smart.permission.model.user.UserAllModel;
import smart.permission.service.OrganizeService;
import smart.permission.service.PositionService;
import smart.permission.service.UserService;
import smart.util.context.SpringContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GeneraterSwapUtil {



    private static OrganizeService organizeService;


    private static PositionService positionService;


    private static UserService userService;

    /**
     * 日期时间戳字符串转换
     * @param date
     * @param format
     * @return
     */
    public static String DateSwap(String date,String format){
        if(StringUtil.isNotEmpty(date)){
            DateTimeFormatter ftf = DateTimeFormatter.ofPattern(format);
            if(date.contains(",")){
                String[] dates=date.split(",");
                long time1 = Long.parseLong(dates[0]);
                long time2 = Long.parseLong(dates[1]);
                String value1 = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time1), ZoneId.systemDefault()));
                String value2 = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time2), ZoneId.systemDefault()));
                return value1+"至"+value2;
            }
            long time = Long.parseLong(date);
            String value = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
            return value;
        }
        return date;
    }

    /**
     * 行政区划转换
     * @param data
     * @return
     */
    public static List<String> provinceData(String data){
        if(StringUtil.isNotEmpty(data)) {
            String[] strs = data.split("," );
            List<String> provList = new ArrayList(Arrays.asList(strs));
            for (String str : strs) {
                provList.add(str);
            }
            return provList;
        }
        return new ArrayList<>();
    }

    /**
     * 公司部门id转名称
     * @param id
     * @return
     */
    public static String comSelectValue(String id){
        if(StringUtil.isNotEmpty(id)) {
        organizeService = SpringContext.getBean(OrganizeService.class);
        List<OrganizeEntity> orgMapList = organizeService.getOrgRedisList();
        for (OrganizeEntity organizeEntity : orgMapList) {
            if (id.equals(organizeEntity.getId())) {
                return organizeEntity.getFullName();
            }
        }
        return id;
        }
        return id;
    }

    /**
     * 岗位id转名称
     * @param id
     * @return
     */
    public static String posSelectValue(String id){
        if(StringUtil.isNotEmpty(id)) {
        positionService = SpringContext.getBean(PositionService.class);
        List<PositionEntity> posMapList = positionService.getPosRedisList();
        for (PositionEntity positionEntity : posMapList) {
            if (id.equals(positionEntity.getId())) {
                return positionEntity.getFullName();
            }
        }
        return id;
        }
        return id;
    }

    /**
     * 岗位id转名称
     * @param id
     * @return
     */
    public static String userSelectValue(String id){
        if(StringUtil.isNotEmpty(id)) {
            userService = SpringContext.getBean(UserService.class);
            List<UserAllModel> userMapList = userService.getAll();
            for (UserAllModel userAllModel : userMapList) {
                if (id.equals(userAllModel.getId())) {
                    return userAllModel.getRealName() + "/" + userAllModel.getAccount();
                }
            }
            return id;
        }
        return id;
    }

    /**
     * 用户id转名称(多选)
     * @param ids
     * @return
     */
    public  String userSelectValues(String ids){
        String[] idList=ids.split(",");
        if(idList.length>0) {
            userService = SpringContext.getBean(UserService.class);
            List<UserAllModel> userMapList = userService.getAll();
            StringBuilder value=new StringBuilder();
            for(String id:idList){
                for (UserAllModel userAllModel : userMapList) {
                    if (id.equals(userAllModel.getId())) {
                        value.append(userAllModel.getRealName() + "/" + userAllModel.getAccount()+"-");
                    }
                }
            }
            return value.substring(0,value.length()-1);
        }
        return ids;
    }

}
