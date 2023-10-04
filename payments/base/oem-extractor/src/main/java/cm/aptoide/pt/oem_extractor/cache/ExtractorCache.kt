package cm.aptoide.pt.oem_extractor.cache

import android.content.SharedPreferences
import cm.aptoide.pt.oem_extractor.di.OemIdSharedPreferences
import com.aptoide.apk.injector.extractor.IExtractorCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ExtractorCache @Inject constructor(
  @OemIdSharedPreferences private val sharedPreferences: SharedPreferences
) : IExtractorCache {
  override fun put(
    key: String?,
    value: String?,
  ) = sharedPreferences.edit().also { it.putString(key, value) }.apply()

  override fun get(key: String?) = sharedPreferences.getString(key, null)
}
