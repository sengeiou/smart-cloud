package smart.service;

import smart.entity.ChargesEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.model.charges.ChargesPagination;
import java.util.*;
/**
 *
 * 收费标准
 * 版本： V3.1.0
 * 版权： SmartCloud项目开发组
 * 作者： SmartCloud
 * 日期： 2021-12-10 15:18:55
 */
public interface ChargesService extends IService<ChargesEntity> {

    List<ChargesEntity> getList(ChargesPagination chargesPagination);

    List<ChargesEntity> getTypeList(ChargesPagination chargesPagination,String dataType);

    ChargesEntity getInfo(String id);

    void delete(ChargesEntity entity);

    void create(ChargesEntity entity);

    boolean update( String id, ChargesEntity entity);

//  子表方法
}
