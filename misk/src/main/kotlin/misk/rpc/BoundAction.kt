package misk.rpc

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCall.Listener
import io.grpc.ServerCallHandler
import misk.Interceptor
import misk.rpc.actions.RpcAction
import misk.rpc.server.ActionCallListener
import javax.inject.Provider
import kotlin.reflect.KFunction

internal class BoundAction<A: RpcAction, ReqT, RespT>(
    private val actionProvider: Provider<A>,
    private val interceptors: MutableList<Interceptor>,
    private val function: KFunction<RespT>
    ) : ServerCallHandler<ReqT, RespT> {
  // TODO(mmihic): Implement header -> context propagation
  override fun startCall(call: ServerCall<ReqT, RespT>?, headers: Metadata?): Listener<ReqT> {
    return ActionCallListener(actionProvider.get(), call!!, interceptors, function)
  }
}