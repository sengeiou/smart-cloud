package smart.scheduletask.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.base.UserInfo;
import smart.base.entity.DbLinkEntity;
import smart.base.service.DblinkService;
import smart.emnus.TimetaskTypes;
import smart.scheduletask.entity.TimeTaskEntity;
import smart.scheduletask.model.ContentModel;
import smart.util.*;
import smart.scheduletask.entity.TimeTaskLogEntity;
import smart.scheduletask.jobutil.CronUtil;
import smart.scheduletask.jobutil.JobManager;
import smart.scheduletask.mapper.TimeTaskMapper;
import smart.scheduletask.service.TimeTaskLogService;
import smart.scheduletask.service.TimetaskService;
import smart.util.type.StringNumber;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时任务
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class TimetaskServiceImpl extends ServiceImpl<TimeTaskMapper, TimeTaskEntity> implements TimetaskService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private JobManager jobManager;
    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private TimeTaskLogService timeTaskLogService;
    @Autowired
    private DblinkService dblinkService;

    @Override
    public List<TimeTaskEntity> getList(Pagination pagination) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        if (pagination.getKeyword() != null) {
            queryWrapper.lambda().and(
                    t -> t.like(TimeTaskEntity::getEnCode, pagination.getKeyword())
                            .or().like(TimeTaskEntity::getFullName, pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByDesc(TimeTaskEntity::getCreatorTime);
        Page page = new Page(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<TimeTaskEntity> iPage = this.page(page, queryWrapper);
        List<String> list = iPage.getRecords().stream().map(t->t.getId()).collect(Collectors.toList());
        List<TimeTaskLogEntity> taskList = timeTaskLogService.getTaskList(list);
        for(TimeTaskEntity entity : iPage.getRecords()){
            List<TimeTaskLogEntity> collect = taskList.stream().filter(t -> t.getTaskId().equals(entity.getId())).collect(Collectors.toList());
            entity.setRunCount(collect.size());
        }
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public List<TimeTaskLogEntity> getTaskLogList(Pagination pagination, String taskId) {
        QueryWrapper<TimeTaskLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TimeTaskLogEntity::getTaskId, taskId);
        //关键字查询
        if (pagination.getKeyword() != null) {
            queryWrapper.lambda().and(
                    t -> t.like(TimeTaskLogEntity::getDescription, pagination.getKeyword())
                            .or().like(TimeTaskLogEntity::getRunResult, pagination.getKeyword())
                            .or().like(TimeTaskLogEntity::getRunTime, pagination.getKeyword())
            );
        }
        //排序
        if (StringUtils.isEmpty(pagination.getSidx())) {
            queryWrapper.lambda().orderByDesc(TimeTaskLogEntity::getRunTime);
        } else {
            queryWrapper = "asc".equals(pagination.getSort().toLowerCase()) ? queryWrapper.orderByAsc(pagination.getSidx()) : queryWrapper.orderByDesc(pagination.getSidx());
        }
        Page page = new Page(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<TimeTaskLogEntity> userIPage = timeTaskLogService.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public TimeTaskEntity getInfo(String id) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TimeTaskEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TimeTaskEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(TimeTaskEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TimeTaskEntity::getEnCode, enCode);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(TimeTaskEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(TimeTaskEntity entity) {
        UserInfo userInfo = userProvider.get();
        boolean tenant = StringUtil.isNotEmpty(userInfo.getTenantDbConnectionString());
        String dbName = tenant ? userInfo.getTenantDbConnectionString() : userInfo.getUserAccount();
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(1);
        entity.setCreatorUserId(userInfo.getUserId());
        ContentModel model = JsonUtil.getJsonToBean(entity.getExecuteContent(), ContentModel.class);
        model.setName(entity.getFullName());
        entity.setEnabledMark(1);
        //定时任务赋值
        CronUtil.task(model, entity, 0);
        DbLinkEntity link = dblinkService.getInfo(model.getDatabase());
        CronUtil.database(entity, model, dbName, link, dataSourceUtil);
        entity.setExecuteContent(JsonUtil.getObjectToString(model));
        this.save(entity);
        CronUtil.startJob(entity, model, dbName, tenant, dataSourceUtil, jobManager);
    }

    @Override
    public boolean update(String id, TimeTaskEntity entity) {
        UserInfo userInfo = userProvider.get();
        boolean tenant = StringUtil.isNotEmpty(userInfo.getTenantDbConnectionString());
        String dbName = tenant ? userInfo.getTenantDbConnectionString() : userInfo.getUserAccount();
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
        entity.setLastModifyUserId(userInfo.getUserId());
        ContentModel model = JsonUtil.getJsonToBean(entity.getExecuteContent(), ContentModel.class);
        //定时任务赋值
        CronUtil.task(model, entity, 1);
        if (StringNumber.ONE.equals(String.valueOf(entity.getEnabledMark()))) {
            DbLinkEntity link = dblinkService.getInfo(model.getDatabase());
            CronUtil.database(entity, model, dbName, link, dataSourceUtil);
            CronUtil.startJob(entity, model, dbName, tenant, dataSourceUtil, jobManager);
        } else if (StringNumber.ZERO.equals(String.valueOf(entity.getEnabledMark()))) {
            if (!String.valueOf(TimetaskTypes.Three.getCode()).equals(model.getFrequency())) {
                jobManager.removeJob(model.getName(), dbName);
            } else {
                String[] cycle = entity.getExecuteCycleJson().split(";");
                for (int i = 0; i < cycle.length; i++) {
                    jobManager.removeJob(model.getName(), dbName + "_" + i);
                }
            }
        }
        model.setName(entity.getFullName());
        entity.setExecuteContent(JsonUtil.getObjectToString(model));
        return this.updateById(entity);
    }

    @Override
    public void delete(TimeTaskEntity entity) {
        UserInfo userInfo = userProvider.get();
        String tenantId = !StringUtils.isEmpty(userInfo.getTenantId()) ? userInfo.getTenantId() : userInfo.getUserAccount();
        if (isjson(entity.getExecuteContent())) {
            ContentModel model = JsonUtil.getJsonToBean(entity.getExecuteContent(), ContentModel.class);
            if (!StringNumber.THREE.equals(model.getFrequency())) {
                jobManager.removeJob(entity.getFullName(), tenantId);
            } else {
                String[] cycle = entity.getExecuteCycleJson().split(";");
                for (int i = 0; i < cycle.length; i++) {
                    jobManager.removeJob(entity.getFullName(), tenantId + "-" + i);
                }
            }
            this.removeById(entity.getId());
            QueryWrapper<TimeTaskLogEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TimeTaskLogEntity::getTaskId, entity.getId());
            timeTaskLogService.remove(queryWrapper);
        } else {
            this.removeById(entity.getId());
            QueryWrapper<TimeTaskLogEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TimeTaskLogEntity::getTaskId, entity.getId());
            timeTaskLogService.remove(queryWrapper);
        }
    }

    @Override
    @Transactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        TimeTaskEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(TimeTaskEntity::getSortCode, upSortCode)
                .orderByDesc(TimeTaskEntity::getSortCode);
        List<TimeTaskEntity> downEntity = this.list(queryWrapper);
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
        TimeTaskEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<TimeTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(TimeTaskEntity::getSortCode, upSortCode)
                .orderByAsc(TimeTaskEntity::getSortCode);
        List<TimeTaskEntity> upEntity = this.list(queryWrapper);
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

    @Override
    public void createTaskLog(TimeTaskLogEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setRunTime(DateUtil.getNowDate());
        timeTaskLogService.save(entity);
    }

    @Override
    public void startAll() {
        jobManager.shutdownJobs();
    }


    private boolean isjson(String string) {
        try {
            JSONObject jsonStr = JSONObject.parseObject(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
