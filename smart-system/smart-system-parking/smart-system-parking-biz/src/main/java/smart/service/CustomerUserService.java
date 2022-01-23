package smart.service;

import smart.entity.CustomerUserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import smart.model.customeruser.CustomerUserPagination;
import java.util.*;
/**
 *
 * 车主用户
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-11-17 10:02:02
 */
public interface CustomerUserService extends IService<CustomerUserEntity> {

    List<CustomerUserEntity> getList(CustomerUserPagination customerUserPagination);

    List<CustomerUserEntity> getTypeList(CustomerUserPagination customerUserPagination,String dataType);



    CustomerUserEntity getInfo(String id);

    void delete(CustomerUserEntity entity);

    void create(CustomerUserEntity entity);

    boolean update( String id, CustomerUserEntity entity);

//  子表方法
}
