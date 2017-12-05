package misk.rpc.client

import com.google.common.util.concurrent.FluentFuture
import com.google.common.util.concurrent.SettableFuture
import io.grpc.CallOptions
import io.grpc.ClientCall
import io.grpc.Metadata
import io.grpc.Status
import io.grpc.methodsByName
import misk.rpc.wire.WireServices

abstract class Stub internal constructor(
    serviceClass: Class<*>,
    private val channels: ManagedChannelRegistry) {

  val serviceDescriptor = WireServices.buildServiceDescriptor(serviceClass)

  fun <ReqT, RespT> invokeAsync(methodName: String, request: ReqT, callOpts: CallOptions): FluentFuture<RespT> {
    val channel = channels.resolveChannel(serviceDescriptor.name)
    val method = serviceDescriptor.methodsByName[methodName]
        ?: throw IllegalArgumentException("no method ${methodName} on ${serviceDescriptor.name}")

    @Suppress("UNCHECKED_CAST")
    val call = channel.newCall(method, callOpts) as ClientCall<ReqT, RespT>

    // TODO(mmihic): Send metadata (trace id, auth info, etc)
    val listener = CallListener<RespT>()
    call.start(listener, Metadata())
    call.sendMessage(request)
    return listener.future
  }

  fun <ReqT, RespT> invoke(methodName: String, request: ReqT, callOpts: CallOptions) : RespT {
    return invokeAsync<ReqT, RespT>(methodName, request, callOpts).get()
  }

  private class CallListener<RespT> : ClientCall.Listener<RespT>() {
    val future = SettableFuture.create<RespT>()
    var response: RespT? = null

    override fun onClose(status: Status?, trailers: Metadata?) {
      if (status == null || status.isOk) {
        if (response != null)
          future.set(response)
        else
          future.setException(IllegalStateException("no response received"))
      } else {
        future.setException(status.asRuntimeException())
      }
    }

    override fun onMessage(message: RespT) {
      if (response != null) {
        future.setException(IllegalStateException("multiple responses received"))
        return
      }

      response = message
    }
  }
}