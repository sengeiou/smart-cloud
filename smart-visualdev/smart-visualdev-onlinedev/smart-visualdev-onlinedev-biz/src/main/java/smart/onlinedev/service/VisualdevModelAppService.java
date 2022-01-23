package smart.onlinedev.service;


import smart.base.VisualdevEntity;
import smart.onlinedev.model.PaginationModel;
import smart.exception.DataException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface VisualdevModelAppService {
    /**
     * 列表
     *
     * @param modelId         模板id
     * @param paginationModel 条件
     * @return
     */
    List<Map<String, Object>> resultList(String modelId, PaginationModel paginationModel) throws DataException, ParseException, SQLException, IOException;

    /**
     * 新增
     *
     * @param entity 实体
     * @param data   数据
     */
    void create(VisualdevEntity entity, String data) throws DataException, SQLException;

    /**
     * 修改
     *
     * @param id     主键
     * @param entity 实体
     * @param data   数据
     */
    boolean update(String id, VisualdevEntity entity, String data) throws DataException, SQLException;

    /**
     * 删除
     *
     * @param id     主键
     * @param entity 实体
     */
    boolean delete(String id, VisualdevEntity entity) throws DataException, SQLException;

    /**
     * 信息
     *
     * @param id     主键
     * @param entity 实体
     * @return
     */
    Map<String, Object> info(String id, VisualdevEntity entity) throws DataException, ParseException, SQLException, IOException;
}

