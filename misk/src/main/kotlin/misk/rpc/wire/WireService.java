package misk.rpc.wire;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotates a generic wire service interface */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WireService {
  /** @return The name of the protobuf package containing the service */
  String packageName();
}
