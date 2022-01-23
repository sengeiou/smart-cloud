package smart.base.fallback;

import smart.base.VisualDevelopmentApi;
import smart.base.VisualdevEntity;
import org.springframework.stereotype.Component;

@Component
public class VisualDevelopmentApiFallback implements VisualDevelopmentApi {

    @Override
    public VisualdevEntity getInfo(String id) {
        return null;
    }
}
