package misk.rpc.client

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton

class GrpcClientModule : AbstractModule() {
  override fun configure() {}

  @Provides
  @Singleton
  fun provideServerEndpointLocator(config: RpcClientConfig): ServerEndpointLocator {
    return StaticServerEndpointLocator(config.services)
  }

  @Provides
  @Singleton
  fun provideManagedChannelRegister(endpointLocator: ServerEndpointLocator): ManagedChannelRegistry {
    return ManagedChannelRegistry(endpointLocator)
  }
}