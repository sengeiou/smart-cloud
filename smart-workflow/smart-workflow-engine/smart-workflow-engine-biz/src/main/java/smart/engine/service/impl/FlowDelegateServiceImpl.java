package smart.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.engine.entity.FlowDelegateEntity;
import smart.engine.mapper.FlowDelegateMapper;
import smart.engine.service.FlowDelegateService;
import smart.util.DateUtil;
import smart.util.RandomUtil;
import smart.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 流程委托
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class FlowDelegateServiceImpl extends ServiceImpl<FlowDelegateMapper, FlowDelegateEntity> implements FlowDelegateService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<FlowDelegateEntity> getList(Pagination pagination) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<FlowDelegateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowDelegateEntity::getCreatorUserId, userId);
        if (!StringUtils.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(FlowDelegateEntity::getFlowName, pagination.getKeyword())
                            .or().like(FlowDelegateEntity::getToUserName, pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByDesc(FlowDelegateEntity::getFSortCode);
        Page page = new Page(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<FlowDelegateEntity> flowDelegateEntityPage = this.page(page, queryWrapper);
        return pagination.setData(flowDelegateEntityPage.getRecords(), page.getTotal());
    }


    @Override
    public List<FlowDelegateEntity> getList() {
        String userId = userProvider.get().getUserId();
        QueryWrapper<FlowDelegateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowDelegateEntity::getCreatorUserId, userId);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public FlowDelegateEntity getInfo(String id) {
        QueryWrapper<FlowDelegateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowDelegateEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(FlowDelegateEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(FlowDelegateEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setFSortCode(RandomUtil.parses());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public List<FlowDelegateEntity> getUser(String userId) {
        Date thisTime = DateUtil.getNowDate();
        QueryWrapper<FlowDelegateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowDelegateEntity::getFTouserid, userId).le(FlowDelegateEntity::getStartTime, thisTime).ge(FlowDelegateEntity::getEndTime, thisTime);
        return this.list(queryWrapper);
    }

    @Override
    public boolean update(String id, FlowDelegateEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }
}
