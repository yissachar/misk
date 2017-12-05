package misk.rpc.wire

import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import io.grpc.MethodDescriptor
import io.grpc.ServiceDescriptor
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

object WireServices {
  fun buildServiceDescriptor(clazz: Class<*>): ServiceDescriptor {
    return buildServiceDescriptor(clazz.kotlin)
  }

  fun buildServiceDescriptor(kclass: KClass<*>) : ServiceDescriptor {
    val serviceName = serviceName(kclass)
    val descriptorBuilder = ServiceDescriptor.newBuilder(serviceName)
    methodDescriptors(serviceName, kclass).forEach { method -> descriptorBuilder.addMethod(method) }
    return descriptorBuilder.build()
  }

  private fun serviceName(kclass: KClass<*>): String {
    val wireService = kclass.annotations.find { a -> a is WireService } as? WireService
        ?: throw IllegalArgumentException("$kclass is not annotated with @WireService")

    return "${wireService.packageName}.${kclass.simpleName}"
  }

  private fun wireMarshaller(methodName: String, type: KType): MethodDescriptor.Marshaller<*> {
    @Suppress("UNCHECKED_CAST")
    val messageClass = type.javaType as? Class<out Message<*, *>>
        ?: throw IllegalArgumentException("$methodName invalid: $type is not a Message")
    val protoAdapter = ProtoAdapter.get(messageClass)
    return WireMarshaller(protoAdapter)
  }

  private fun methodDescriptors(serviceName: String, kclass: KClass<*>): List<MethodDescriptor<*, *>> {
    return kclass.members.filter { member ->
      member.annotations.find { annotation -> annotation is WireMethod } != null
    }.map { member -> buildMethodDescriptor(serviceName, member) }
  }

  private fun buildMethodDescriptor(serviceName : String, member: KCallable<*>): MethodDescriptor<*, *> {
    val fullMemberName = "${serviceName}#${member.name}"

    if (member !is KFunction<*>) {
      throw IllegalArgumentException("${fullMemberName} is not a method")
    }

    if (member.parameters.size != 2) {
      throw IllegalArgumentException("${fullMemberName} does not take a message parameter")
    }

    val requestMarshaller = wireMarshaller(member.name, member.parameters[1].type)
    val responseMarshaller = wireMarshaller(member.name, member.returnType)

    // TODO(mmihic): Idempotent and safe
    return MethodDescriptor.newBuilder(requestMarshaller, responseMarshaller)
        .setType(MethodDescriptor.MethodType.UNARY)
        .setFullMethodName(MethodDescriptor.generateFullMethodName(serviceName, member.name))
        .build()
  }
}