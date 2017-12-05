package misk.rpc.server

import com.google.common.util.concurrent.AbstractIdleService
import io.grpc.Server
import io.grpc.ServerBuilder
import misk.logging.getLogger
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GrpcService
@Inject constructor(
    private val config: RpcServerConfig,
    services: MutableList<RpcActionsService>)
  : AbstractIdleService() {

  companion object {
    @JvmStatic val logger = getLogger<GrpcService>()
  }

  val server: Server

  init {
    // TODO(mmihic): Executor, compression, tracer
    val serverBuilder = ServerBuilder.forPort(config.port)
    services.forEach { service -> serverBuilder.addService(service) }
    this.server = serverBuilder.build()
  }


  override fun startUp() {
    logger.info("starting GRPC server on ${config.port}")
    server.start()
  }

  override fun shutDown() {
    logger.info("stopping GRPC server")
    server.shutdownNow()

    // TODO(mmihic): Maybe make wait time configurable
    server.awaitTermination(30, TimeUnit.SECONDS)
  }
}