package misk.rpc.client

internal class StaticServerEndpointLocator(
    private val endpoints: Map<String, ServerEndpoint>
) : ServerEndpointLocator {
  override fun resolve(serviceName: String): ServerEndpoint {
    return endpoints.get(serviceName)
        ?: throw IllegalArgumentException("no endpoints registered for ${serviceName}")
  }
}