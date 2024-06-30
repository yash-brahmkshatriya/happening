package ln.dev.config;

import ln.dev.pojo.BasePojo;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MongoListener extends AbstractMongoEventListener<BasePojo> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<BasePojo> event) {
        event.getSource().setCreatedAt(new Date());
        super.onBeforeConvert(event);
    }
}
