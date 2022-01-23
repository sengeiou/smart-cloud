package smart.permission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import smart.permission.fallback.AuthorizeApiFallback;
import smart.permission.entity.AuthorizeEntity;
import smart.permission.model.authorize.AuthorizeVO;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 获取权限信息Api
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME, fallback = AuthorizeApiFallback.class, path = "/Permission/Authority")
public interface AuthorizeApi {

    /**
     * 通过是不是管理员获取权限Vo
     *
     * @param isCache
     * @return
     */
    @GetMapping("/permission/{isCache}")
    AuthorizeVO getEntity(@PathVariable("isCache") boolean isCache);

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    @GetMapping("/GetListByObjectId/{objectId}")
    List<AuthorizeEntity> getListByObjectId(@PathVariable("objectId") String objectId);

    /**
     * 将查出来的某个对象删除
     * @param queryWrapper
     * @return
     */
    @DeleteMapping("/remove")
    void remove(QueryWrapper<AuthorizeEntity> queryWrapper);

}
