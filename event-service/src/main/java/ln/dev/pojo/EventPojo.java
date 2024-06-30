package ln.dev.pojo;

import ln.dev.constants.MongoFieldNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ln.dev.protos.event.EventType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class EventPojo {

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

    @Field(MongoFieldNames.Event.CREATED_AT)
    private Date createdAt;
    
}
