package ln.dev.pojo;

import ln.dev.constants.MongoFieldNames;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BasePojo {

    @Field(MongoFieldNames.Event.CREATED_AT)
    private Date createdAt;

}
