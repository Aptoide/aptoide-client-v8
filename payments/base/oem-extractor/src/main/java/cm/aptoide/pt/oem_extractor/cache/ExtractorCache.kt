package cm.aptoide.pt.oem_extractor.cache

import com.aptoide.apk.injector.extractor.IExtractorCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ExtractorCache @Inject constructor(
  private val cache: OemIdPreferences
) : IExtractorCache {
  override fun put(
    key: String?,
    value: String?,
  ) = cache.putOemId(key, value)

  override fun get(key: String?) = cache.getOemId(key) ?: ""
}
