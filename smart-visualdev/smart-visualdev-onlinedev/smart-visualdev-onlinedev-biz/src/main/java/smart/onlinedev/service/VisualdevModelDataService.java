package smart.onlinedev.service;


import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.VisualdevEntity;
import smart.exception.DataException;
import smart.onlinedev.entity.VisualdevModelDataEntity;
import smart.onlinedev.model.*;
import smart.onlinedev.model.fields.FieLdsModel;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 *
 * 0代码功能数据表
 * 版本： V3.0.0
 * 版权： 智慧停车公司
 * 作者： 管理员/admin
 * 日期： 2020-07-24 11:59
 */
public interface VisualdevModelDataService extends IService<VisualdevModelDataEntity> {


    List<Map<String, Object>> getListResult(VisualdevEntity visualdevEntity , PaginationModel paginationModel) throws IOException, ParseException, DataException, SQLException;

    List<VisualdevModelDataEntity> getList(String modelId);

    VisualdevModelDataEntity getInfo(String id);

    VisualdevModelDataInfoVO infoDataChange(String id, VisualdevEntity visualdevEntity) throws IOException, ParseException, DataException, SQLException;

    VisualdevModelDataInfoVO tableInfo(String id, VisualdevEntity visualdevEntity) throws DataException, ParseException, SQLException, IOException;

    void create(VisualdevEntity visualdevEntity, VisualdevModelDataCrForm visualdevModelDataCrForm) throws DataException, SQLException;

    boolean update(String id,VisualdevEntity visualdevEntity, VisualdevModelDataUpForm visualdevModelDataUpForm) throws DataException, SQLException;

    void delete(VisualdevModelDataEntity entity);

    boolean tableDelete(String id,VisualdevEntity visualdevEntity) throws DataException, SQLException;

    boolean tableDeleteMore(String id,VisualdevEntity visualdevEntity) throws DataException, SQLException;

    void  importData(List<VisualdevModelDataEntity> list);

    List<Map<String, Object>> exportData(String[] keys, PaginationModelExport paginationModelExport, VisualdevEntity visualdevEntity) throws IOException, ParseException, SQLException, DataException;

    VisualdevModelDataInfoVO tableInfoDataChange(String id, VisualdevEntity visualdevEntity) throws DataException, ParseException, IOException, SQLException;
}
