package misk.rpc.actions;

import com.google.inject.TypeLiteral;
import java.util.Map;
import misk.rpc.BoundAction;

interface Bindings {
  TypeLiteral<Map<String, BoundAction<?, ?, ?>>> BOUND_ACTIONS_MAP =
      new TypeLiteral<Map<String, BoundAction<?, ?, ?>>>() {};
}
