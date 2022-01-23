package smart.permission.model.user;

import smart.base.Pagination;
import lombok.Data;

@Data
public class UserLogForm extends Pagination {
    private String startTime;
    private String endTime;
    private int category;
}
