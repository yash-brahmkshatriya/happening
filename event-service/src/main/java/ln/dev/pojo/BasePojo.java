package ln.dev.pojo;

import java.util.Date;
import ln.dev.constants.MongoFieldNames;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BasePojo {

    @Field(MongoFieldNames.Event.CREATED_AT)
    private Date createdAt;
}
