package smart.permission.model.authorize;

import lombok.Data;

@Data
public class SaveAuthForm {
    private String itemType;
    private String objectType;
    private String[] objectId;
}
