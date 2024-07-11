package ln.dev.subscription.model;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Subscription<P, G, F> {

    private String subscriptionId;

    private Date timestamp;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<StreamObserver<G>> responseObserver;

    private F filters;

    protected abstract boolean applyFilter(P p);

}
