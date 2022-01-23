package smart.model.employee;

import smart.base.PaginationTime;
import lombok.Data;

@Data
public class PaginationEmployee extends PaginationTime {
    private String condition;
}
