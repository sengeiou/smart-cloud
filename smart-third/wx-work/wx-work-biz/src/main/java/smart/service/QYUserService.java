package smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.Page;
import smart.QYDepartmentEntity;
import smart.QYUserEntity;
import smart.exception.WxErrorException;
import smart.permission.entity.UserEntity;

import java.util.List;

/**
 * 企业号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface QYUserService extends IService<QYUserEntity> {


    /**
     * 同步成功列表
     *
     * @return
     */
    List<QYUserEntity> getListAll();

    /**
     * 列表
     * @param page
     * @return
     */
    List<QYUserEntity> getList(Page page);

    /**
     * 同步
     *
     * @param userList 用户列表
     * @param departList 同步成功的部门
     */
    void synchronization(List<UserEntity> userList, List<QYDepartmentEntity> departList) throws WxErrorException;
}
