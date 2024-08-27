package ln.dev.pojo;

import java.util.List;
import ln.dev.constants.MongoFieldNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Field(MongoFieldNames.Event.LOCATION_TYPE)
    private String type;

    @Field(MongoFieldNames.Event.COORDINATES)
    private List<Double> coordinates;
}
