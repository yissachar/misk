package misk.rpc

import com.google.inject.Guice
import helpers.protos.Dinosaur
import misk.rpc.actions.Rpc
import misk.rpc.actions.RpcAction
import misk.rpc.wire.WireMethod
import misk.rpc.wire.WireService
import org.junit.Before
import org.junit.Test

class RpcClientServerTest {
  @WireService(packageName = "com.squareup.mook")
  abstract class SampleService {
    @WireMethod abstract fun callMe(request: Dinosaur): Dinosaur
    @WireMethod abstract fun pokeMe(request: Dinosaur): Dinosaur
  }

  class CallMeHandler : RpcAction {
    @Rpc(method = "callMe")
    fun callMe(request: Dinosaur) : Dinosaur {
      return request.newBuilder()
          .name(request.name + " called")
          .build()
    }
  }

  class PokeMeHandler : RpcAction {
    @Rpc(method = "pokeMe")
    fun callMe(request: Dinosaur) : Dinosaur {
      return request.newBuilder()
          .name(request.name + " poked")
          .build()
    }
  }

  @Before
  fun buildServer() {
    val injector = Guice.createInjector(

    )
  }


  @Test
  fun clientServerSuccessAsync() {

  }

  @Test
  fun clientServerErrorAsync() {

  }

  @Test
  fun clientServerSuccess() {

  }

  @Test
  fun clientServerError() {
  }

}