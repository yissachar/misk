package misk.rpc.actions;

/** Marks a method as being an RPC handler */
public @interface Rpc {
  String method();
}
