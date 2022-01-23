package smart.base.vo;

import lombok.Data;

import java.util.List;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Data
public class ListVO<T> {
    private List<T>  list;

}
