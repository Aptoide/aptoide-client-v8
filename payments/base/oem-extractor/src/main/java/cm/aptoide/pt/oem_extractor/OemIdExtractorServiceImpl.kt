package cm.aptoide.pt.oem_extractor

import cm.aptoide.pt.oem_extractor.di.ExtractorV1
import cm.aptoide.pt.oem_extractor.di.ExtractorV2
import cm.aptoide.pt.oem_extractor.extractors.IExtractOemId
import cm.aptoide.pt.osp_handler.handler.OemIdExtractorService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OemIdExtractorServiceImpl @Inject constructor(
  @ExtractorV1 private val extractorV1: IExtractOemId,
  @ExtractorV2 private val extractorV2: IExtractOemId,
) : OemIdExtractorService {

  override suspend fun extractOemId(packageName: String?): String? {
    return packageName?.let {
      try {
        val oemId = extractorV2.extractOemId(it)
        check(oemId?.isNotEmpty() == true)
        oemId
      } catch (e: Throwable) {
        extractorV1.extractOemId(it)
      }
    }
  }
}
