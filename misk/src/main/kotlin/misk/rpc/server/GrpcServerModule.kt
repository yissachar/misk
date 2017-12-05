package misk.rpc.server

import com.google.common.util.concurrent.Service
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import misk.inject.newMultibinder
import misk.inject.to
import misk.rpc.BoundAction
import misk.rpc.actions.RpcBinding

class GrpcServerModule : AbstractModule() {
  override fun configure() {
    binder().newMultibinder<Service>().to<GrpcService>()
  }

  @Provides @Singleton
  private fun provideRpcActionsServices(
      @RpcBinding actions: Map<Class<*>, Map<String, BoundAction<*, *, *>>>
  ): List<RpcActionsService> {
    return actions.map { RpcActionsService(it.key, it.value) }
  }
}