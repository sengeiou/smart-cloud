package smart.message.model;

import smart.base.Pagination;
import lombok.Data;

@Data
public class PaginationMessage extends Pagination {
    private String type;
}
