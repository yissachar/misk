package misk.rpc.wire

import com.google.common.truth.Truth.assertThat
import helpers.protos.Dinosaur
import io.grpc.MethodDescriptor
import io.grpc.methodsByName
import org.junit.Test

internal class WireServicesTest {
  class NonMessageClass

  @Test
  fun buildServiceDescriptor() {
    @WireService(packageName = "com.squareup.mook")
    abstract class SampleService {
      @WireMethod abstract fun callMe(request: Dinosaur): Dinosaur
      @WireMethod abstract fun pokeMe(request: Dinosaur): Dinosaur
    }

    val serviceDescriptor = WireServices.buildServiceDescriptor(SampleService::class)
    assertThat(serviceDescriptor.name).isEqualTo("com.squareup.mook.SampleService")
    assertThat(serviceDescriptor.methods.size).isEqualTo(2)

    val callMe = serviceDescriptor.methodsByName["callMe"] ?: throw IllegalArgumentException("no callMe MethodDescriptor")
    assertThat(callMe.type).isEqualTo(MethodDescriptor.MethodType.UNARY)
    assertThat(callMe.requestMarshaller).isInstanceOf(WireMarshaller::class.java)
    assertThat(callMe.responseMarshaller).isInstanceOf(WireMarshaller::class.java)

    val pokeMe = serviceDescriptor.methodsByName["pokeMe"] ?: throw IllegalArgumentException("no pokeMe MethodDescriptor")
    assertThat(pokeMe.type).isEqualTo(MethodDescriptor.MethodType.UNARY)
    assertThat(pokeMe.requestMarshaller).isInstanceOf(WireMarshaller::class.java)
    assertThat(pokeMe.responseMarshaller).isInstanceOf(WireMarshaller::class.java)
  }

  @Test(expected = IllegalArgumentException::class)
  fun noWireServiceAnnotation() {
    abstract class NoWireServiceAnnotation {
      @WireMethod abstract fun callMe(request: Dinosaur): Dinosaur
    }

    WireServices.buildServiceDescriptor(NoWireServiceAnnotation::class)
  }


  @Test(expected = IllegalArgumentException::class)
  fun parameterNotClass() {
    @WireService(packageName = "com.squareup.mook")
    abstract class ParameterNotClass {
      @WireMethod abstract fun callMe(request: Int): Dinosaur
    }

    WireServices.buildServiceDescriptor(ParameterNotClass::class)
  }

  @Test(expected = IllegalArgumentException::class)
  fun parameterNotMessage() {
    @WireService(packageName = "com.squareup.mook")
    abstract class ParameterNotMessage {
      @WireMethod abstract fun callMe(request: NonMessageClass): Dinosaur
    }

    WireServices.buildServiceDescriptor(ParameterNotMessage::class)
  }

  @Test(expected = IllegalArgumentException::class)
  fun returnTypeNotMessage() {
    @WireService(packageName = "com.squareup.mook")
    abstract class ReturnTypeNotMessage {
      @WireMethod abstract fun callMe(request: Dinosaur): NonMessageClass
    }

    WireServices.buildServiceDescriptor(ReturnTypeNotMessage::class)
  }

  @Test(expected = IllegalArgumentException::class)
  fun returnTypeNotClass() {
    @WireService(packageName = "com.squareup.mook")
    abstract class ReturnTypeNotClass {
      @WireMethod abstract fun callMe(request: Dinosaur): Int
    }

    WireServices.buildServiceDescriptor(ReturnTypeNotClass::class)
  }
}