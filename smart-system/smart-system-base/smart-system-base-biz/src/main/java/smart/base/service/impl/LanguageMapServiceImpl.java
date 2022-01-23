package smart.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.base.model.language.LanguageListDTO;
import smart.base.mapper.LanguageMapMapper;
import smart.base.service.DictionaryDataService;
import smart.base.service.LanguageMapService;
import smart.emnus.DbType;
import smart.util.*;
import smart.base.entity.LanguageMapEntity;
import smart.base.entity.DictionaryDataEntity;
import smart.util.type.SortType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 翻译数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Service
public class LanguageMapServiceImpl extends ServiceImpl<LanguageMapMapper, LanguageMapEntity> implements LanguageMapService {

    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Override
    public List<LanguageListDTO> getList(Pagination pagination, String languageTypeId) {
        List<DictionaryDataEntity> data = dictionaryDataService.getList("dc6b2542d94b407cac61ec1d59592901");
        List<String> datas = data.stream().map(u -> u.getEnCode()).collect(Collectors.toList());
        String[] language = datas.toArray(new String[datas.size()]);
        StringBuilder strSql = new StringBuilder();
        strSql.append("SELECT m.F_SORTCODE,m.F_ENCODE,m.F_SIGNKEY, m.f_fullname AS " + language[0].toLowerCase().replace("-","_") + " ");
        for (int i = 1; i < language.length; i++) {
            strSql.append(", m" + i + ".f_fullname AS " + language[i].toLowerCase().replace("-","_") + " ");
        }
        strSql.append("FROM base_languagemap m ");
        for (int j = 1; j < language.length; j++) {
            strSql.append("LEFT JOIN base_languagemap m" + j + " ON m" + j + ".F_EnCode = m.F_EnCode AND m" + j + ".F_Language = '" + language[j] + "' ");
        }
        strSql.append("WHERE m.F_Language = '" + language[0] + "' AND m.F_LanguageTypeId = " + " '" + languageTypeId + "' ");
        if (!StringUtils.isEmpty(pagination.getKeyword())) {
            strSql.append(" AND (m.f_fullname like '%" + pagination.getKeyword() + "%'");
            for (int i = 1; i < language.length; i++) {
                strSql.append("OR m" + i + ".f_fullname like '%" + pagination.getKeyword() + "%'");
            }
            strSql.append(")");
        }
        if (SortType.DESC.equals(pagination.getSort().toLowerCase())){
            strSql.append(" ORDER By m1.F_CreatorTime DESC ");
        }else {
            strSql.append(" ORDER By m1.F_CreatorTime ASC ");
        }
        //全部数据
        List<Map<String, Object>> list = this.baseMapper.getList(strSql.toString());
        //转换成这种格式数据返回
        List<LanguageListDTO> dtoList = new ArrayList<>();
        for (int m = 0; m < list.size(); m++) {
            JSONObject json = new JSONObject(list.get(m));
            LanguageListDTO dto = JsonUtil.getJsonToBean(json, LanguageListDTO.class);
            dtoList.add(dto);
        }
        for (Map<String, Object> map : list) {
            for (LanguageListDTO dto : dtoList) {
                if (map.get("F_ENCODE").equals(dto.getEncode())) {
                    //接收语言列表
                    Map<String, Object> languageEnCodes = new HashMap<>();
                    for (String key : map.keySet()) {
                        for (int i = 0; i < language.length; i++) {
                            if (!"F_ENCODE".equals(key) && !"F_SIGNKEY".equals(key) && !"F_SORTCODE".equals(key)) {
                                if (DbType.ORACLE.getMessage().equals(dataSourceUtil.getDataType().toLowerCase())){
                                    Clob clob =(Clob) map.get(key);
                                    try {
                                        languageEnCodes.put(key.toLowerCase().replace("_","-"), clob.getSubString(1,(int) clob.length()));
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                }else{
                                    languageEnCodes.put(key.toLowerCase().replace("_","-"), map.get(key));
                                }
                            }
                        }
                    }
                    dto.setLanguageEnCodes(languageEnCodes);
                }
            }
        }
        return pagination.setData(PageUtil.getListPage((int)pagination.getCurrentPage(), (int)pagination.getPageSize(), dtoList), dtoList.size());
    }

    @Override
    public List<LanguageMapEntity> getList(String fnCode) {
        QueryWrapper<LanguageMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LanguageMapEntity::getEnCode, fnCode).orderByAsc(LanguageMapEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public boolean isExistBySignKey(String fnCode, String signKey) {
        if (!StringUtils.isEmpty(fnCode)) {
            QueryWrapper<LanguageMapEntity> current = new QueryWrapper<>();
            current.lambda().eq(LanguageMapEntity::getEnCode, fnCode);
            long currentCount = this.count(current);
            QueryWrapper<LanguageMapEntity> countWrapper = new QueryWrapper<>();
            countWrapper.lambda().ne(LanguageMapEntity::getEnCode, fnCode).eq(LanguageMapEntity::getSignKey, signKey);
            long count = this.count(countWrapper);
            return (count >= currentCount);
        } else {
            QueryWrapper<LanguageMapEntity> countWrapper = new QueryWrapper<>();
            countWrapper.lambda().eq(LanguageMapEntity::getSignKey, signKey);
            long count = this.count(countWrapper);
            return count > 0;
        }
    }

    @Override
    @Transactional
    public void create(List<LanguageMapEntity> entitys) {
        String fnCode = RandomUtil.uuId();
        Long sortCode = RandomUtil.parses();
        QueryWrapper<LanguageMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LanguageMapEntity::getEnCode, fnCode);
        this.remove(queryWrapper);
        for (LanguageMapEntity entity : entitys) {
            entity.setId(RandomUtil.uuId());
            entity.setEnCode(fnCode);
            entity.setSortCode(sortCode);
            this.save(entity);
        }
    }

    @Override
    @Transactional
    public boolean update(String fnCode, List<LanguageMapEntity> entitys) {
        LanguageMapEntity beforeLanguageMap = this.getList(fnCode).stream().findFirst().get();
        QueryWrapper<LanguageMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LanguageMapEntity::getEnCode, fnCode);
        this.remove(queryWrapper);
        for (LanguageMapEntity entity : entitys) {
            entity.setId(RandomUtil.uuId());
            entity.setEnCode(fnCode);
            entity.setSortCode(beforeLanguageMap.getSortCode());
            entity.setCreatorTime(beforeLanguageMap.getCreatorTime());
            entity.setCreatorUserId(beforeLanguageMap.getCreatorUserId());
            this.save(entity);
        }
        return beforeLanguageMap==null?false:true;
    }

    @Override
    public boolean delete(String fnCode) {
        QueryWrapper<LanguageMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LanguageMapEntity::getEnCode, fnCode);
        return this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean first(String fnCode) {
        boolean isOk = false;
        QueryWrapper<LanguageMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LanguageMapEntity::getEnCode, fnCode);
        List<LanguageMapEntity> upList = this.list(queryWrapper);
        Long upSortCode = upList.get(0).getSortCode() == null ? 0 : upList.get(0).getSortCode();
        String typeId = upList.get(0).getLanguageTypeId();
        QueryWrapper<LanguageMapEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(LanguageMapEntity::getLanguageTypeId, typeId)
                .lt(LanguageMapEntity::getSortCode, upSortCode)
                .orderByDesc(LanguageMapEntity::getSortCode);
        List<LanguageMapEntity> downList = this.list(wrapper).stream().limit(upList.size()).collect(Collectors.toList());
        if (downList.size() > 0) {
            for (LanguageMapEntity currentEntity : upList) {
                currentEntity.setSortCode(downList.get(0).getSortCode());
                this.updateById(currentEntity);
            }
            for (LanguageMapEntity upEntity : downList) {
                upEntity.setSortCode(upSortCode);
                this.updateById(upEntity);
            }
            isOk = true;
        }
        return isOk;
    }

    @Override
    @Transactional
    public boolean next(String fnCode) {
        boolean isOk = false;
        QueryWrapper<LanguageMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LanguageMapEntity::getEnCode, fnCode);
        List<LanguageMapEntity> downList = this.list(queryWrapper);
        Long upSortCode = downList.get(0).getSortCode() == null ? 0 : downList.get(0).getSortCode();
        String typeId = downList.get(0).getLanguageTypeId();
        //查询下几条记录
        QueryWrapper<LanguageMapEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(LanguageMapEntity::getLanguageTypeId, typeId)
                .gt(LanguageMapEntity::getSortCode, upSortCode)
                .orderByAsc(LanguageMapEntity::getSortCode);
        List<LanguageMapEntity> upList = this.list(wrapper).stream().limit(downList.size()).collect(Collectors.toList());
        if (upList.size() > 0) {
            for (LanguageMapEntity lme : downList) {
                lme.setSortCode(upList.get(0).getSortCode());
                this.updateById(lme);
            }
            for (LanguageMapEntity lm : upList) {
                lm.setSortCode(upSortCode);
                this.updateById(lm);
            }
            isOk = true;
        }
        return isOk;
    }
}
