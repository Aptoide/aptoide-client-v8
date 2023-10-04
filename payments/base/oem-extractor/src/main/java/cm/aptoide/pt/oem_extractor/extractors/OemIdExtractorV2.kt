package cm.aptoide.pt.oem_extractor.extractors

import android.content.Context
import cm.aptoide.pt.oem_extractor.di.ExtractorV2
import com.aptoide.apk.injector.extractor.IExtractorCache
import com.aptoide.apk.injector.extractor.domain.IExtract
import com.aptoide.apk.injector.extractor.utils.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExtractorV2
internal class OemIdExtractorV2 @Inject constructor(
  @ApplicationContext context: Context,
  private val extractor: IExtract,
  private val extractorCache: IExtractorCache,
  private val environment: Environment,
) : BaseOemIdExtractor(context) {

  override suspend fun extractOemId(packageName: String): String? {
    val sourceDir = getPackageName(packageName)
    val result = extractor.extract(File(sourceDir), environment, extractorCache)
    val splitResult = result.split(",")
    return if (splitResult.isEmpty())
      null
    else
      splitResult[0]
  }
}
