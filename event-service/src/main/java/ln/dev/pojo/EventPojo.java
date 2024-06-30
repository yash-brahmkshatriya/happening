package ln.dev.pojo;

import ln.dev.constants.MongoFieldNames;
import lombok.*;
import ln.dev.protos.event.EventType;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "events")
public class EventPojo extends BasePojo {

    @MongoId(FieldType.OBJECT_ID)
    private String id;

    @Field(MongoFieldNames.Event.NAME)
    private String name;

    @Field(MongoFieldNames.Event.TYPE)
    private EventType type;

    @Field(MongoFieldNames.Event.DESCRIPTION)
    private String description;

    @Field(MongoFieldNames.Event.START_TIMESTAMP)
    private Date startTimestamp;

    @Field(MongoFieldNames.Event.END_TIMESTAMP)
    private Date endTimestamp;

    @Field(MongoFieldNames.Event.LOCATION)
    private Location location;
    
}
