package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.mapper.ProjectGanttMapper;
import smart.service.ProjectGanttService;
import smart.entity.ProjectGanttEntity;
import smart.util.RandomUtil;
import smart.base.Page;
import smart.base.Pagination;
import smart.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 订单明细
 *
 * @author 开发平台组
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
public class ProjectGanttServiceImpl extends ServiceImpl<ProjectGanttMapper, ProjectGanttEntity> implements ProjectGanttService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<ProjectGanttEntity> getList(Page page) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getType, 1).orderByAsc(ProjectGanttEntity::getSortCode);
        if (!StringUtils.isEmpty(page.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(ProjectGanttEntity::getEnCode, page.getKeyword())
                           .or().like(ProjectGanttEntity::getFullName, page.getKeyword())
            );
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<ProjectGanttEntity> getTaskList(String projectId) {
        ProjectGanttEntity entity = this.getInfo(projectId);
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getType, 2).eq(ProjectGanttEntity::getProjectId, projectId).orderByAsc(ProjectGanttEntity::getSortCode);
        List<ProjectGanttEntity> list = this.list(queryWrapper);
        list.add(entity);
        return list;
    }

    @Override
    public ProjectGanttEntity getInfo(String id) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean allowDelete(String id) {
        Pagination pagination =new Pagination();
        Long subdataCount = this.getList(pagination).stream().filter(t -> String.valueOf(t.getProjectId()).equals(id)).count();
        return (subdataCount == 0);
    }

    @Override
    public void delete(ProjectGanttEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(ProjectGanttEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setSchedule(0);
        if (entity.getEnabledMark() == null) {
            entity.setEnabledMark(1);
        }
        entity.setSortCode(RandomUtil.parses());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ProjectGanttEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ProjectGanttEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProjectGanttEntity::getEnCode, enCode);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(ProjectGanttEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        ProjectGanttEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(ProjectGanttEntity::getSortCode, upSortCode)
                .eq(ProjectGanttEntity::getParentId, upEntity.getParentId())
                .orderByDesc(ProjectGanttEntity::getSortCode);
        List<ProjectGanttEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            this.updateById(downEntity.get(0));
            this.updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @Transactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        ProjectGanttEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<ProjectGanttEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(ProjectGanttEntity::getSortCode, upSortCode)
                .eq(ProjectGanttEntity::getParentId,downEntity.getParentId())
                .orderByAsc(ProjectGanttEntity::getSortCode);
        List<ProjectGanttEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            upEntity.get(0).setSortCode(temp);
            this.updateById(upEntity.get(0));
            this.updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }
}
