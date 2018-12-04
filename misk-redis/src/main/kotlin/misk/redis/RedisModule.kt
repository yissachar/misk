package misk.redis

import com.google.common.util.concurrent.Service
import com.google.inject.Provides
import com.google.inject.Singleton
import misk.inject.KAbstractModule
import redis.clients.jedis.Jedis

class RedisModule(val config: RedisConfig): KAbstractModule() {
  override fun configure() {
    bind<RedisConfig>().toInstance(config)
    multibind<Service>().to<RedisService>()
  }

  @Provides @Singleton
  internal fun provideRedisClient(config: RedisConfig): Redis {
    val jedis = Jedis(config.host_name, config.port)
    jedis.auth(config.auth_password)
    return RealRedis(jedis)
  }
}
