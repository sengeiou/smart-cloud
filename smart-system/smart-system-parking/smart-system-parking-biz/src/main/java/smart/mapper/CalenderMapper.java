package smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.entity.CalenderEntity;
import smart.model.calender.CalenderJspVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CalenderMapper extends BaseMapper<CalenderEntity> {
    List<String> getAllName();

    /**
     * 新增日历名称
     * @param entity
     */
    void createName(@Param("list") List<CalenderEntity> entity);

    /**
     * 删除日历名称
     * @param name
     */
    void deleteCalenderName(@Param("name") String name);

    /**
     * 通过日历名称查询存在的日历数
     * @param name
     * @return
     */
    Integer getNumberByName(@Param("name") String name);

    Integer existingConfiguration(@Param("name") String name);

}
