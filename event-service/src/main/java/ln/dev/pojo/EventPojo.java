package ln.dev.pojo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import ln.dev.protos.event.EventType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "events")
public class EventPojo {

    @Id
    private String id;

    private String name;

    private EventType type;

    private String description;

    private Date startTimestamp;

    private Date endTimestamp;

    private Location location;

    private Date createdAt;
    
}
