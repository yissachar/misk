package misk.rpc.actions

import com.google.inject.AbstractModule
import com.google.inject.Key
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.MapBinder
import com.google.inject.name.Names
import misk.Interceptor
import misk.asAction
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

class RpcActionModule<A : RpcAction> private constructor(
    private val serviceClass: KClass<*>,
    private val actionClass: KClass<A>,
    private val rpcAnnotation: Rpc,
    private val member: KFunction<*>
) : AbstractModule() {
  override fun configure() {
    // All actions are exposed in a Map<Class, Map<String, BoundAction>> qualified by @RpcBinding
    // The map for each service's actions is qualified by a @Named annotation containing the
    // service class name
    val boundActionsMapQualifier = Names.named("RpcService/${serviceClass.java}")
    val boundActionMapKey = Key.get(Bindings.BOUND_ACTIONS_MAP, boundActionsMapQualifier)

    MapBinder.newMapBinder(binder(),
        TypeLiteral.get(Class::class.java),
        Bindings.BOUND_ACTIONS_MAP,
        RpcBinding::class.java)
        .addBinding(serviceClass.java)
        .to(boundActionMapKey)

    val actionProvider = getProvider(actionClass.java)
    val boundActionProvider = BoundActionProvider(actionProvider, member)
    MapBinder.newMapBinder(binder(),
        TypeLiteral.get(String::class.java),
        Bindings.BOUND_ACTIONS_MAP,
        boundActionsMapQualifier)
        .addBinding(rpcAnnotation.method)
        .to(boundActionProvider)
  }

  companion object {
    inline fun <reified S : Any, reified A : RpcAction> create(): RpcActionModule<A> = create(S::class, A::class)

    @JvmStatic fun <A : RpcAction> create(serviceClass: Class<*>, actionClass: Class<A>): RpcActionModule<A> {
      return create(serviceClass.kotlin, actionClass.kotlin)
    }

    fun <A : RpcAction> create(serviceClass: KClass<*>, actionClass: KClass<A>): RpcActionModule<A> {
      var result: RpcActionModule<A>? = null
      for (member in actionClass.members) {
        for (annotation in member.annotations) {
          if (annotation !is Rpc) continue
          if (member !is KFunction<*>) throw IllegalArgumentException("expected $member to be a function")
          if (result != null) throw IllegalArgumentException("multiple annotated methods in $actionClass")

          result = RpcActionModule(serviceClass, actionClass, annotation, member)
        }
      }

      if (result == null) {
        throw IllegalArgumentException("no annotated @Rpc methods in $actionClass")
      }
      return result
    }
  }
}

internal class BoundActionProvider<A : RpcAction, RespT>(
    val provider: Provider<A>,
    val function: KFunction<RespT>
) : Provider<misk.rpc.BoundAction<A, Any, RespT>> {

  @Inject lateinit var interceptorFactories: List<Interceptor.Factory>

  override fun get(): misk.rpc.BoundAction<A, Any, RespT> {
    val action = function.asAction()

    val interceptors = ArrayList<Interceptor>()
    interceptorFactories.mapNotNullTo(interceptors) { it.create(action) }
    return misk.rpc.BoundAction(provider, interceptors, function)
  }
}

internal val KType.typeLiteral: TypeLiteral<*> get() = TypeLiteral.get(javaType)
