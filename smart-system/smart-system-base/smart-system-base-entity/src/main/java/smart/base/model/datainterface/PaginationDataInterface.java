package smart.base.model.datainterface;

import smart.base.Pagination;
import lombok.Data;

@Data
public class PaginationDataInterface extends Pagination {
    private String categoryId;
}
