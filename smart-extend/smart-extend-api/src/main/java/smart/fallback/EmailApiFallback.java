package smart.fallback;

import smart.EmailApi;
import smart.entity.EmailReceiveEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailApiFallback implements EmailApi {
    @Override
    public List<EmailReceiveEntity> getReceiveList() {
        return null;
    }
}
