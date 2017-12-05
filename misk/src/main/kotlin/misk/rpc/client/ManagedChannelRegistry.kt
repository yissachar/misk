package misk.rpc.client

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.ConcurrentHashMap

class ManagedChannelRegistry(private val endpointLocator: ServerEndpointLocator) {
  private val managedChannels = ConcurrentHashMap<ServerEndpoint, ManagedChannel>()

  fun resolveChannel(serviceName: String) : ManagedChannel {
    // TODO(mmihic): idle timeout, compressor/decompressor, executor, interceptors,
    // keep alive, load balancer, etc
    val endpoint = endpointLocator.resolve(serviceName)
    return managedChannels.getOrPut(endpoint) {
      ManagedChannelBuilder.forAddress(endpoint.host, endpoint.port).build()
    }
  }
}