package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.mapper.WorkLogMapper;
import smart.service.WorkLogService;
import smart.service.WorkLogShareService;
import smart.entity.WorkLogEntity;
import smart.entity.WorkLogShareEntity;
import smart.util.RandomUtil;
import smart.base.Pagination;
import smart.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 工作日志
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
public class WorkLogServiceImpl extends ServiceImpl<WorkLogMapper, WorkLogEntity> implements WorkLogService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private WorkLogShareService workLogShareService;

    @Override
    public List<WorkLogEntity> getSendList(Pagination pageModel) {
        QueryWrapper<WorkLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogEntity::getCreatorUserId, userProvider.get().getUserId());
        //排序
        if (StringUtils.isEmpty(pageModel.getSidx())) {
            queryWrapper.lambda().orderByDesc(WorkLogEntity::getCreatortime);
        } else {
            queryWrapper = "asc".equals(pageModel.getSort().toLowerCase()) ? queryWrapper.orderByAsc(pageModel.getSidx()) : queryWrapper.orderByDesc(pageModel.getSidx());
        }
        Page page = new Page(pageModel.getCurrentPage(), pageModel.getPageSize());
        IPage<WorkLogEntity> IPages = this.page(page, queryWrapper);
        return pageModel.setData(IPages.getRecords(), page.getTotal());
    }

    @Override
    public List<WorkLogEntity> getReceiveList(Pagination pageModel) {
        QueryWrapper<WorkLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(WorkLogEntity::getToUserId, userProvider.get().getUserId());
        //排序
        if (StringUtils.isEmpty(pageModel.getSidx())) {
            queryWrapper.lambda().orderByDesc(WorkLogEntity::getCreatortime);
        } else {
            queryWrapper = "asc".equals(pageModel.getSort().toLowerCase()) ? queryWrapper.orderByAsc(pageModel.getSidx()) : queryWrapper.orderByDesc(pageModel.getSidx());
        }
        Page page = new Page(pageModel.getCurrentPage(), pageModel.getPageSize());
        IPage<WorkLogEntity> IPages = this.page(page, queryWrapper);
        return pageModel.setData(IPages.getRecords(), page.getTotal());
    }

    @Override
    public WorkLogEntity getInfo(String id) {
        QueryWrapper<WorkLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void create(WorkLogEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSortCode(RandomUtil.parses());
        entity.setCreatortime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
        String[] toUserIds = entity.getToUserId().split(",");
        for (String userIds : toUserIds) {
            WorkLogShareEntity workLogShare = new WorkLogShareEntity();
            workLogShare.setId(RandomUtil.uuId());
            workLogShare.setShareTime(new Date());
            workLogShare.setWorkLogId(entity.getId());
            workLogShare.setShareUserId(userIds);
            workLogShareService.save(workLogShare);
        }
    }

    @Override
    @Transactional
    public boolean update(String id, WorkLogEntity entity) {
        boolean flag = false;
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        flag = this.updateById(entity);
        QueryWrapper<WorkLogShareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogShareEntity::getWorkLogId,entity.getId());
        workLogShareService.remove(queryWrapper);
        String[] toUserIds = entity.getToUserId().split(",");
        for (String userIds : toUserIds) {
            WorkLogShareEntity workLogShare = new WorkLogShareEntity();
            workLogShare.setId(RandomUtil.uuId());
            workLogShare.setShareTime(new Date());
            workLogShare.setWorkLogId(entity.getId());
            workLogShare.setShareUserId(userIds);
            workLogShareService.save(workLogShare);
        }
        return flag;
    }

    @Override
    @Transactional
    public void delete(WorkLogEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
        QueryWrapper<WorkLogShareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WorkLogShareEntity::getWorkLogId,entity.getId());
        workLogShareService.remove(queryWrapper);
    }

}
