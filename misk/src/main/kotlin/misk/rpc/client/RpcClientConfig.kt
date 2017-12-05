package misk.rpc.client

import misk.config.Config

// TODO(mmihic): SSL configuration
data class RpcClientConfig(val services: Map<String, ServerEndpoint>) : Config