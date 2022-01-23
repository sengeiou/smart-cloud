package smart.base.model.dbtable;


import smart.base.Pagination;
import lombok.Data;

@Data
public class DbTableDataForm extends Pagination {
     private String field;
}
