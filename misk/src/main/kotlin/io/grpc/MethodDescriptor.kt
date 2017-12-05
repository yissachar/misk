package io.grpc

val <ReqT, RespT> MethodDescriptor<ReqT, RespT>.methodName: String
  get() {
    return fullMethodName.split("/")[1]
  }
