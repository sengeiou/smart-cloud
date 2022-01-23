package smart.service;

import smart.exception.WxErrorException;
import smart.model.mpmenu.MPMenuModel;

import java.util.List;

/**
 * 公众号菜单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface MPMenuService {

    /**
     * 列表
     *
     * @return
     */
    List<MPMenuModel> getList() throws WxErrorException;

    /**
     * 同步
     *
     * @param menuList
     */
    void SyncMenu(List<MPMenuModel> menuList) throws WxErrorException;

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean IsExistByFullName(String fullName, String id) throws WxErrorException;

    /**
     * 创建
     *
     * @param model 实体对象
     */
    void create(MPMenuModel model);

    /**
     * 更新
     *
     * @param id    主键值
     * @param model 实体对象
     */
    boolean update(String id, MPMenuModel model);

    /**
     * 删除
     *
     * @param id 主键
     */
    boolean delete(String id);

    /**
     * 上移
     *
     * @param id 主键值
     */
    void First(String id) throws WxErrorException;

    /**
     * 下移
     *
     * @param id 主键值
     */
    void Next(String id) throws WxErrorException;
}
