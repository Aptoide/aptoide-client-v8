package com.appcoins.payments.oem_extractor.cache

import android.content.SharedPreferences
import com.aptoide.apk.injector.extractor.IExtractorCache

internal class ExtractorCache(private val sharedPreferences: SharedPreferences) : IExtractorCache {
  override fun put(
    key: String?,
    value: String?,
  ) = sharedPreferences.edit().also { it.putString(key, value) }.apply()

  override fun get(key: String?) = sharedPreferences.getString(key, null)
}
