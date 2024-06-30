package ln.dev.pojo;

import ln.dev.constants.MongoFieldNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

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
