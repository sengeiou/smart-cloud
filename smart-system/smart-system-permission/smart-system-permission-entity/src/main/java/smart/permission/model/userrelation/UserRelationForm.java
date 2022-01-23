package smart.permission.model.userrelation;

import lombok.Data;

@Data
public class UserRelationForm {
   private String objectType;
   private String[] userIds;
}
