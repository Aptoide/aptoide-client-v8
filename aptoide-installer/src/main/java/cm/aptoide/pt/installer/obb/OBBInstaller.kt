package cm.aptoide.pt.installer.obb

import android.os.Environment
import cm.aptoide.pt.installer.platform.copyWithProgressTo
import java.io.File

private val OBB_FOLDER = Environment.getExternalStorageDirectory().absolutePath + "/Android/obb/"

internal fun removeObbFromStore(packageName: String): Boolean =
  File("$OBB_FOLDER$packageName/").deleteRecursively()

internal suspend fun Collection<File>.installOBBs(
  packageName: String,
  progress: suspend (Long) -> Unit,
) {
  val outputPath = "$OBB_FOLDER$packageName/"
  val prepared = File(outputPath).run {
    deleteRecursively()
    mkdirs()
  }
  if (!prepared) throw IllegalStateException("Can't create OBB folder: $outputPath")

  var processedSize: Long = 0
  forEach { file ->
    val size = file.length()
    val destinationFile = File(outputPath + file.name)
    // Try to move first
    file.renameTo(destinationFile)
      .takeUnless { it }
      ?.also {
        file.inputStream().use { inputStream ->
          destinationFile.createNewFile()
          destinationFile.outputStream().use { outputStream ->
            inputStream
              .copyWithProgressTo(outputStream)
              .collect {
                progress(processedSize + it)
              }
          }
        }
      }
    processedSize += size
  }
}
