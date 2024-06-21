package ln.dev.service;

import io.grpc.stub.StreamObserver;
import ln.dev.grpc.EventRequest;
import ln.dev.grpc.EventServiceGrpc;
import ln.dev.grpc.HeartbeatResponse;
import ln.dev.protos.common.Common;
import ln.dev.protos.event.Event;
import net.devh.boot.grpc.server.service.GrpcService;

import java.text.SimpleDateFormat;
import java.util.Date;

@GrpcService
public class EventServiceImpl extends EventServiceGrpc.EventServiceImplBase {
    @Override
    public void activeEvents(EventRequest request, StreamObserver<Event> responseObserver) {
        responseObserver.onNext(Event.newBuilder()
                        .setName("Work in progress")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void heartbeat(Common.Empty request, StreamObserver<HeartbeatResponse> responseObserver) {
        HeartbeatResponse heartbeatResponse = HeartbeatResponse.newBuilder()
                .setMessage("Heartbeat success")
                .setTimestamp(
                        new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date())
                )
                .build();

        responseObserver.onNext(heartbeatResponse);
        responseObserver.onCompleted();
    }
}
