package misk.schemamigratorgradleplugin

import okio.BufferedSource
import okio.buffer
import okio.source
import wisp.resources.ResourceLoader
import java.io.File

class FilesystemResourceLoaderBackend : ResourceLoader.Backend() {

  override fun exists(path: String): Boolean {
    return File(path).exists()
  }

  override fun open(path: String): BufferedSource {
    return File(path).source().buffer()
  }

  override fun list(path: String): List<String> {
    return File(path).listFiles()?.map { "$path/${it.name}" }?.toList() ?: emptyList()
  }
}
