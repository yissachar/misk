package misk.rpc.server

import com.google.common.collect.Lists.newArrayList
import io.grpc.ServerCall
import io.grpc.ServerCall.Listener
import misk.Interceptor
import misk.rpc.actions.RpcAction
import misk.rpc.actions.asChain
import kotlin.reflect.KFunction

internal class ActionCallListener<ReqT, RespT>(
    private val action: RpcAction,
    private val call: ServerCall<ReqT, RespT>,
    private val interceptors: MutableList<Interceptor>,
    private val function: KFunction<RespT>
) : Listener<ReqT>() {
  override fun onMessage(message: ReqT) {
    // TODO(mmihic): For now we assume that the action function takes just the request object,
    // later we'll want to support injected parameters and context parameters derived from
    // the headers metadata
    // TODO(mmihic): Eventually support streaming
    val chain = action.asChain(function, newArrayList(message), *interceptors.toTypedArray())

    // TODO(mmihic): Would be nice to verify vs throw a cast exception, but we can't due to
    // type erasure
    @Suppress("UNCHECKED_CAST")
    val response = chain.proceed(chain.args) as RespT
    call.sendMessage(response)
  }
}
