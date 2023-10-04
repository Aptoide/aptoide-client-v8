package cm.aptoide.pt.oem_extractor.extractors

import android.content.Context
import cm.aptoide.pt.oem_extractor.di.ExtractorV2
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Properties
import java.util.zip.ZipFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExtractorV2
internal class OemIdExtractorV1 @Inject constructor(
  @ApplicationContext context: Context,
) : BaseOemIdExtractor(context) {

  companion object {
    private const val KEY = "oemid"
  }

  override suspend fun extractOemId(packageName: String): String? {
    return try {
      val sourceDir = getPackageName(packageName)
      val myZipFile = ZipFile(sourceDir)
      val entry = myZipFile.getEntry("META-INF/attrib")
      if (entry == null) null
      val inputStream = myZipFile.getInputStream(entry)
      val properties = Properties().also { it.load(inputStream) }
      if (properties.containsKey(KEY)) {
        properties.getProperty(KEY)
      } else
        null
    } catch (e: Exception) {
      null
    }
  }
}
