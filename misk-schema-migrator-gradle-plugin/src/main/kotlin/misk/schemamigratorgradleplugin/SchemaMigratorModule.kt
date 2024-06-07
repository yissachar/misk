package misk.schemamigratorgradleplugin

import com.google.inject.multibindings.MapBinder
import misk.MiskCommonServiceModule
import misk.environment.DeploymentModule
import misk.inject.KAbstractModule
import misk.jdbc.DataSourceConfig
import misk.jdbc.DataSourceType
import misk.jdbc.JdbcModule
import misk.jdbc.RealDatabasePool
import wisp.deployment.TESTING
import wisp.resources.ResourceLoader
import java.io.File
import java.time.Clock

class SchemaMigratorModule(
  private val database: String,
  private val dbType: String,
  private val username: String,
  private val password: String,
  private val schemaDir: File
): KAbstractModule() {

  override fun configure() {
    val schemaMigratorClusterConfig = DataSourceConfig(
      type = DataSourceType.valueOf(dbType),
      migrations_resource = "filesystem:$schemaDir",
      database = database,
      username = username,
      password = password,
    )
    val mapBinder = MapBinder.newMapBinder(
      binder(), String::class.java, ResourceLoader.Backend::class.java
    )
    mapBinder.addBinding("filesystem:").toInstance(FilesystemResourceLoaderBackend())
    bind<Clock>().toInstance(Clock.systemUTC())
    install(MiskCommonServiceModule())
    install(DeploymentModule(TESTING))
    install(
      JdbcModule(
        qualifier = SchemaMigratorDatabase::class,
        config = schemaMigratorClusterConfig,
        databasePool = RealDatabasePool
      )
    )
  }
}
