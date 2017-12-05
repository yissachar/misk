package misk.rpc.server

import misk.config.Config

// TODO(mmihic): SSL configuration
data class RpcServerConfig(val port: Int, val idle_timeout: Long) : Config