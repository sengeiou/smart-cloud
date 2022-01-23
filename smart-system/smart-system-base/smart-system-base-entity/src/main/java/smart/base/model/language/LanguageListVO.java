package smart.base.model.language;

import smart.base.Pagination;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LanguageListVO {

    private Pagination pagination;

    private List<LanguageListDTO> list;
}
