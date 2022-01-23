package smart.model.mpmenu;

import lombok.Data;

import java.util.List;

@Data
public class MPMenuButtonModel {
    private String key;
    private String name;
    private String type;
    private String url;
    private List<MPMenuSubButtonModel> sub_button;
}
