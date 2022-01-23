package smart.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.base.mapper.VisualDataMapMapper;
import smart.base.service.VisualDataMapService;
import smart.util.DateUtil;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.base.entity.VisualDataMapEntity;
import smart.util.UserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏地图
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
public class VisualDataMapServiceImpl extends ServiceImpl<VisualDataMapMapper, VisualDataMapEntity> implements VisualDataMapService {

	@Autowired
    private UserProvider userProvider;

    @Override
    public List<VisualDataMapEntity> getList(Pagination pagination){
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(VisualDataMapEntity::getFullName, pagination.getKeyword())
                            .or().like(VisualDataMapEntity::getEnCode, pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByAsc(VisualDataMapEntity::getSortCode);
        Page page = new Page(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<VisualDataMapEntity> IPages = this.page(page, queryWrapper);
        return pagination.setData(IPages.getRecords(), page.getTotal());
    }

    @Override
    public List<VisualDataMapEntity> getList() {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualDataMapEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public VisualDataMapEntity getInfo(String id) {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDataMapEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualDataMapEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setCreatorUser(userProvider.get().getUserId());
        entity.setEnabledMark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualDataMapEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
        entity.setLastModifyUser(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualDataMapEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDataMapEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(VisualDataMapEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<VisualDataMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDataMapEntity::getEnCode, enCode);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(VisualDataMapEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

}
