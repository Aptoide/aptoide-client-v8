package com.appcoins.payments.di

import android.content.Context
import com.appcoins.oem_extractor.BuildConfig
import com.appcoins.oem_extractor.OemIdExtractor
import com.appcoins.oem_extractor.OemIdExtractorImpl
import com.appcoins.oem_extractor.OemPackageExtractor
import com.appcoins.oem_extractor.OemPackageExtractorImpl
import com.appcoins.oem_extractor.cache.ExtractorCache
import com.appcoins.payments.arch.Environment.DEV
import com.appcoins.payments.arch.Environment.PROD
import com.appcoins.payments.arch.PaymentsInitializer
import com.aptoide.apk.injector.extractor.IExtractorCache
import com.aptoide.apk.injector.extractor.data.Extractor
import com.aptoide.apk.injector.extractor.data.ExtractorV1
import com.aptoide.apk.injector.extractor.data.ExtractorV2
import com.aptoide.apk.injector.extractor.domain.IExtract
import com.aptoide.apk.injector.extractor.utils.Environment

var PaymentsInitializer.oemPackageExtractor: OemPackageExtractor
  get() =
    oemPackageExt ?: OemPackageExtractorImpl(context)
      .also { oemPackageExt = it }
  set(value) {
    oemPackageExt = value
  }
private var oemPackageExt: OemPackageExtractor? = null

var PaymentsInitializer.oemIdExtractor: OemIdExtractor
  get() =
    oemIdExt ?: OemIdExtractorImpl(
      context,
      extractor,
      environment = when (environment) {
        DEV -> Environment.DEVELOPMENT
        PROD -> Environment.PRODUCTION
      },
      extractorCache,
    )
      .also { oemIdExt = it }
  set(value) {
    oemIdExt = value
  }
private var oemIdExt: OemIdExtractor? = null

private val PaymentsInitializer.extractorCache: IExtractorCache by lazy {
  ExtractorCache(
    PaymentsInitializer.context.getSharedPreferences(
      "${BuildConfig.LIBRARY_PACKAGE_NAME}.oem_id_prefs",
      Context.MODE_PRIVATE
    )
  )
}

private val PaymentsInitializer.extractor: IExtract by lazy {
  Extractor(
    ExtractorV1(),
    ExtractorV2()
  )
}
