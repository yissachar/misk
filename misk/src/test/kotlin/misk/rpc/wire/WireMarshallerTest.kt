package misk.rpc.wire

import com.google.common.truth.Truth.assertThat
import helpers.protos.Dinosaur
import org.junit.Test

internal class WireMarshallerTest {
  @Test
  fun streamAndParse() {
    val marshaller = WireMarshaller(Dinosaur.ADAPTER)
    val dinosaur = Dinosaur.Builder()
        .name("trex")
        .picture_urls(listOf("a", "b", "c"))
        .build()

    assertThat(marshaller.parse(marshaller.stream(dinosaur))).isEqualTo(dinosaur)
  }
}