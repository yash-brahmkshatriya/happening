package ln.dev.service.grpc;

import io.grpc.stub.StreamObserver;
import ln.dev.grpc.ClientSubscription;
import ln.dev.grpc.EventSubscriptionRequest;
import ln.dev.grpc.SubscriptionServiceGrpc;
import ln.dev.protos.common.Common;
import ln.dev.protos.event.Event;
import ln.dev.subscription.model.EventSubscription;
import ln.dev.subscription.service.EventSubscriptionService;
import ln.dev.util.EventConvertor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class SubscriptionServiceGrpcImpl extends SubscriptionServiceGrpc.SubscriptionServiceImplBase {

    private final EventSubscriptionService eventSubscriptionService;

    public SubscriptionServiceGrpcImpl(EventSubscriptionService eventSubscriptionService) {
        this.eventSubscriptionService = eventSubscriptionService;
    }

    @Override
    public void subscribeToEvents(
            EventSubscriptionRequest request, StreamObserver<ClientSubscription> responseObserver) {
        EventSubscription eventSubscription =
                eventSubscriptionService.subscribe(request.getFilters(), responseObserver);
        responseObserver.onNext(ClientSubscription.newBuilder()
                .setSubscriptionId(eventSubscription.getSubscriptionId())
                .setTimestamp(EventConvertor.formatISODate(eventSubscription.getTimestamp()))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void listenSubscription(ClientSubscription request, StreamObserver<Event> responseObserver) {
        eventSubscriptionService.listenSubscription(request.getSubscriptionId(), responseObserver);
    }

    @Override
    public void unsubscribe(ClientSubscription request, StreamObserver<Common.Empty> responseObserver) {
        eventSubscriptionService.unsubscribe(request.getSubscriptionId());
        responseObserver.onNext(Common.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
