package smart.portal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.portal.model.PortalPagination;
import smart.portal.entity.PortalEntity;

import java.util.List;


/**
 *
 * base_portal
 * 版本： V3.0.0
 * 版权： 智慧停车公司
 * 作者： 管理员/admin
 * 日期： 2020-10-21 14:23:30
 *
 */

public interface PortalService extends IService<PortalEntity> {

    List<PortalEntity> getList(PortalPagination pagination);

    List<PortalEntity> getList();

    PortalEntity getInfo(String id);

    void create(PortalEntity entity);

    boolean update(String id, PortalEntity entity);

    void delete(PortalEntity entity);




}
