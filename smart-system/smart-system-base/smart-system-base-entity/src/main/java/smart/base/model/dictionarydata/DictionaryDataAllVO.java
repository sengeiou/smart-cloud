package smart.base.model.dictionarydata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DictionaryDataAllVO {
    private String  id;
    private String  fullName;
    private String parentId;
    private List<DictionaryDataAllVO> children;
}
