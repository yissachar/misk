plugins {
  id("misk.schemamigratorgradleplugin")
}

miskSchemaMigrator {
  database = "meh"
  username = "root"
  password = ""
  schemaDir.set(layout.projectDirectory.dir("src/main/resources/db-migrations"))
}
