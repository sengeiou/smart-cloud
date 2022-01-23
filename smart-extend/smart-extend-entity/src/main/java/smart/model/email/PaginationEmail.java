package smart.model.email;

import smart.base.PaginationTime;
import lombok.Data;

@Data
public class PaginationEmail extends PaginationTime {
    private String type;
}
