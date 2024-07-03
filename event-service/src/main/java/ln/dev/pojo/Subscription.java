package ln.dev.pojo;

import ln.dev.constants.MongoFieldNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Field(MongoFieldNames.Event.Subscription.SUBSCRIPTION_ID)
    private String subscriptionId;

    @Field(MongoFieldNames.Event.Subscription.IS_ACTIVE)
    private boolean active;

    @Field(MongoFieldNames.Event.Subscription.ACTIVE_FROM_TIMESTAMP)
    private Date activeFrom;

}
