package kui.test

import com.google.common.collect.Iterables
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import org.lognet.springboot.grpc.GRpcGlobalInterceptor
import org.slf4j.Logger

@GRpcGlobalInterceptor
class DumpMetadataInterceptor(val logger: Logger) : ServerInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(call: ServerCall<ReqT, RespT>, headers: Metadata, next: ServerCallHandler<ReqT, RespT>): ServerCall.Listener<ReqT> {

        logger.info("{} header entries", headers.keys().size);
        for (keyName in headers.keys()) {
            val key = Metadata.Key.of(keyName, Metadata.ASCII_STRING_MARSHALLER)
            val values = headers.getAll(key)?.asSequence()?.toList()
            logger.info("{}: {}", key.name(), values)
        }

        return next.startCall(call, headers)
    }
}
