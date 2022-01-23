package smart.service;

import smart.entity.CustomerCarEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.model.customercar.CustomerCarPagination;
import java.util.*;
/**
 *
 * 车辆信息
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-17 11:44:41
 */
public interface CustomerCarService extends IService<CustomerCarEntity> {

    List<CustomerCarEntity> getList(CustomerCarPagination customerCarPagination);

    List<CustomerCarEntity> getTypeList(CustomerCarPagination customerCarPagination,String dataType);



    CustomerCarEntity getInfo(String id);

    void delete(CustomerCarEntity entity);

    void create(CustomerCarEntity entity);

    boolean update( String id, CustomerCarEntity entity);

//  子表方法
}
