package smart.permission.model.user;

import smart.base.Pagination;
import lombok.Data;

@Data
public class PaginationUser extends Pagination {
    private String organizeId;
}
