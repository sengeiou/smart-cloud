package smart.roadtooth.model;

import smart.base.Pagination;
import lombok.Data;

@Data
public class PaginationData extends Pagination {
    private String categoryId;
}
