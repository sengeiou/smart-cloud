package smart.onlinedev.model.fields.slot;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SlotModel {
    private String prepend;
    private String append;
    @JSONField(name = "default")
    private String defaultName;
//    private List<SlotOptionModel> options;
    private String options;
    private String appOptions;
}
