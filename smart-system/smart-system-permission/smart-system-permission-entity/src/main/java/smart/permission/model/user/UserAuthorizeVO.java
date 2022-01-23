package smart.permission.model.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAuthorizeVO {
    private List<UserAuthorizeModel> button;
    private List<UserAuthorizeModel> column;
    private List<UserAuthorizeModel> module;
    private List<UserAuthorizeModel> resource;
}
