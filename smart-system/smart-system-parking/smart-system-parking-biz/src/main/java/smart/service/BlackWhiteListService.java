package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.entity.BlackWhiteListEntity;
import smart.model.blackwhitelist.BlackWhiteListPagination;

import java.util.List;

/**
 * p_black_white_list
 * 版本： V3.1.0
 * 版权： 智慧停车公司
 * 作者： 开发平台组
 * 日期： 2021-12-17 17:24:37
 */
public interface BlackWhiteListService extends IService<BlackWhiteListEntity> {

    List<BlackWhiteListEntity> getList(BlackWhiteListPagination blackWhiteListPagination);

    List<BlackWhiteListEntity> getTypeList(BlackWhiteListPagination blackWhiteListPagination, String dataType);


    BlackWhiteListEntity getInfo(String id);

    void delete(BlackWhiteListEntity entity);

    void create(BlackWhiteListEntity entity);

    boolean update(String id, BlackWhiteListEntity entity);

//  子表方法
}
