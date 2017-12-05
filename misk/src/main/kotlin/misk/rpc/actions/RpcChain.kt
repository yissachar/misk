package misk.rpc.actions

import misk.Chain
import misk.Interceptor
import kotlin.reflect.KFunction

internal class RpcChain(
    private val _action: RpcAction,
    private val _args: List<Any?>,
    private val interceptors: List<Interceptor>,
    private val _function: KFunction<*>,
    private val index: Int = 0
) : Chain {
  override val action: RpcAction get() = _action;
  override val args: List<Any?> get() = _args;
  override val function: KFunction<*> get() = _function

  override fun proceed(args: List<Any?>): Any? {
    check(index < interceptors.size) { "final interceptor must be terminal" }
    val next = RpcChain(_action, args, interceptors, function, index + 1)
    return interceptors[index].intercept(next)
  }
}

