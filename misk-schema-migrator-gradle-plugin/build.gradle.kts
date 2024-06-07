import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  id("com.vanniktech.maven.publish.base")
  `java-test-fixtures`
  id("com.autonomousapps.testkit") version "0.10"
}

gradlePlugin {
  plugins {
    create("MiskSchemaMigratorPlugin") {
      id = "misk.schemamigratorgradleplugin"
      implementationClass = "misk.schemamigratorgradleplugin.MiskSchemaMigratorPlugin"
    }
  }
}

dependencies {
  implementation(project(":misk"))
  implementation(project(":misk-inject"))
  implementation(project(":misk-jdbc"))

  testImplementation(libs.assertj)
  testImplementation(gradleTestKit())
  testImplementation(libs.junitApi)
}

configure<MavenPublishBaseExtension> {
  configure(
    KotlinJvm(javadocJar = JavadocJar.Dokka("dokkaGfm"))
  )
}
