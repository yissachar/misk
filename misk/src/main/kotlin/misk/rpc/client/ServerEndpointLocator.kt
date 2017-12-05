package misk.rpc.client

// TODO(mmihic): Probably replace this with NameResolver / LoadBalancer
interface ServerEndpointLocator {
  fun resolve(serviceName: String): ServerEndpoint
}