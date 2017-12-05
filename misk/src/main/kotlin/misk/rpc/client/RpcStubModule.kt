package misk.rpc.client

import com.google.inject.AbstractModule
import kotlin.reflect.KClass

class RpcStubModule<S: Any>private constructor (
    val serviceClass: KClass<S>
) : AbstractModule() {
  override fun configure() {
  }

  companion object {
    inline fun <reified S : Any> create(): RpcStubModule<S> = create(S::class)

    @JvmStatic fun <S: Any> create(serviceClass: Class<S>): RpcStubModule<S> {
      return create(serviceClass.kotlin)
    }

    fun <S : Any> create(serviceClass: KClass<S>): RpcStubModule<S> {
      return RpcStubModule(serviceClass)
    }
  }
}