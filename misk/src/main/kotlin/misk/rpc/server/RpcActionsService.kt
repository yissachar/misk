package misk.rpc.server

import io.grpc.BindableService
import io.grpc.MethodDescriptor
import io.grpc.MethodDescriptor.generateFullMethodName
import io.grpc.ServerMethodDefinition
import io.grpc.ServerServiceDefinition
import io.grpc.ServiceDescriptor
import io.grpc.methodName
import io.grpc.methodsByName
import misk.rpc.BoundAction
import misk.rpc.actions.RpcAction
import misk.rpc.wire.WireServices
import javax.inject.Inject

/** Provides a GRPC service given a set of actions for all of the methods on the service */
internal class RpcActionsService @Inject constructor(
    serviceClass: Class<*>,
    boundActions: Map<String, BoundAction<out RpcAction, *, *>>
) : BindableService {

  val serviceDescriptor: ServiceDescriptor = WireServices.buildServiceDescriptor(serviceClass)
  val serviceDefinition: ServerServiceDefinition

  init {
    val serviceDefinitionBuilder = ServerServiceDefinition.builder(serviceDescriptor.name)

    // Confirm every method has a corresponding action
    for (methodDescriptor in serviceDescriptor.methods) {
      if (!boundActions.containsKey(methodDescriptor.methodName)) {
        throw IllegalArgumentException("no method ${methodDescriptor.fullMethodName} defined")
      }
    }

    // And bind the method definitions for each action
    for (methodDefinition in boundActions.map { buildMethodDefinition(it.key, it.value) }) {
      serviceDefinitionBuilder.addMethod(methodDefinition)
    }
    serviceDefinition = serviceDefinitionBuilder.build();
  }

  override fun bindService(): ServerServiceDefinition {
    return serviceDefinition
  }

  private fun <ReqT, RespT> buildMethodDefinition(methodName: String, action: BoundAction<*, ReqT, RespT>): ServerMethodDefinition<ReqT, RespT> {
    val fullMethodName = generateFullMethodName(serviceDescriptor.name, methodName)
    val methodDescriptor = serviceDescriptor.methodsByName[methodName]
        ?: throw IllegalArgumentException("no method ${fullMethodName} defined")

    @Suppress("UNCHECKED_CAST")
    return ServerMethodDefinition.create(methodDescriptor as MethodDescriptor<ReqT, RespT>, action)
  }
}