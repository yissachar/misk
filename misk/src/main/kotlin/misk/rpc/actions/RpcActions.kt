package misk.rpc.actions

fun RpcAction.asChain(
    function: kotlin.reflect.KFunction<*>,
    args: List<Any?>,
    vararg _interceptors: misk.Interceptor
): misk.Chain {
  val interceptors = com.google.common.collect.Lists.newArrayList(_interceptors.iterator())
  interceptors.add(object : misk.Interceptor {
    override fun intercept(chain: misk.Chain): Any? {
      return function.call(chain.action, *chain.args.toTypedArray())
    }
  })
  return RpcChain(this, args, interceptors, function, 0)
}
