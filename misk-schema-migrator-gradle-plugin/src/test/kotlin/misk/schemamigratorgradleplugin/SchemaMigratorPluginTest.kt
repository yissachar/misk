package misk.schemamigratorgradleplugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import org.junit.jupiter.api.Test

class SchemaMigratorPluginTest {
  @Test
  fun `schema migrator plugin migrates schems`() {
    val testProjectDir = File(this.javaClass.getResource("/schema-migrator-plugin-test")!!.file)

    val result = GradleRunner.create()
      .withProjectDir(testProjectDir)
      .withArguments("migrateSchema")
      .withPluginClasspath()
      .build()

    // TODO:
    assertTrue(result.output.contains("Found 1 file(s) starting with \"foo\""))
  }

}
