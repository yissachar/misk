package misk.rpc.wire

import com.squareup.wire.ProtoAdapter
import io.grpc.MethodDescriptor
import java.io.InputStream

/** A {@link Marshaller} that uses wire for the messages */
class WireMarshaller<T>(
    private val adapter: ProtoAdapter<T>
) : MethodDescriptor.Marshaller<T> {

  override fun parse(stream: InputStream?): T {
    // TODO(mmihic): Deal with null streams better
    if (stream == null) {
      throw IllegalArgumentException("stream is null")
    }

    return adapter.decode(stream)
  }

  override fun stream(value: T): InputStream {
    return adapter.encode(value).inputStream()
  }
}