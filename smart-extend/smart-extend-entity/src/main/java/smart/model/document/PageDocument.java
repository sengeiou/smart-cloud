package smart.model.document;

import smart.base.Page;
import lombok.Data;

@Data
public class PageDocument extends Page {
    private String parentId;
}
