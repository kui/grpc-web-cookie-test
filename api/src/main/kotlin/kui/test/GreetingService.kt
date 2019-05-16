package kui.test

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.Logger

@GRpcService
class GreetingService(val logger: Logger) : GreetingServiceGrpc.GreetingServiceImplBase() {
    override fun greet(request: Greeting.GreetRequest, responseObserver: StreamObserver<Greeting.GreetResponse>) {
        logger.info("greet: req={}", request);
        responseObserver.onNext(
                Greeting.GreetResponse.newBuilder()
                        .setMessage("Hi")
                        .build())
        responseObserver.onCompleted()
    }
}
