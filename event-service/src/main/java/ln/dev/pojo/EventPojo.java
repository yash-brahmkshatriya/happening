package ln.dev.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ln.dev.protos.event.EventType;
import org.springframework.data.mongodb.core.mapping.Document;
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

    private String name;

    private EventType type;

    private String description;

    private Date startTimestamp;

    private Date endTimestamp;

    private Location location;

    private Date createdAt;
    
}
