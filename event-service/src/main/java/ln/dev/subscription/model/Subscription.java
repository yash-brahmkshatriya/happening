package ln.dev.subscription.model;

import io.grpc.stub.StreamObserver;
import java.util.Date;
import java.util.Optional;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Subscription<G, T> {

    private String subscriptionId;

    private Date timestamp;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<StreamObserver<G>> responseObserver;

    @Setter(AccessLevel.NONE)
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<T> requestData;

    public void updateRequestData(T requestData) {
        this.requestData = Optional.of(requestData);
    }
}
