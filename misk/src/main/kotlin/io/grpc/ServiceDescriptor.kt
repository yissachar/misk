package io.grpc

val ServiceDescriptor.methodsByName: Map<String, MethodDescriptor<*, *>>
  get() {
    return methods.map { it.methodName to it }.toMap()
  }