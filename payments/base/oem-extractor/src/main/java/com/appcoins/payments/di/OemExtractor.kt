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
import com.aptoide.apk.injector.extractor.IExtractorCache
import com.aptoide.apk.injector.extractor.data.Extractor
import com.aptoide.apk.injector.extractor.data.ExtractorV1
import com.aptoide.apk.injector.extractor.data.ExtractorV2
import com.aptoide.apk.injector.extractor.domain.IExtract
import com.aptoide.apk.injector.extractor.utils.Environment

var Payments.oemPackageExtractor: OemPackageExtractor by lateInit {
  OemPackageExtractorImpl(context)
}

var Payments.oemIdExtractor: OemIdExtractor by lateInit {
  OemIdExtractorImpl(
    context,
    extractor,
    environment = when (environment) {
      DEV -> Environment.DEVELOPMENT
      PROD -> Environment.PRODUCTION
    },
    extractorCache,
  )
}

private val Payments.extractorCache: IExtractorCache by lazyInit {
  ExtractorCache(
    context.getSharedPreferences(
      "${BuildConfig.LIBRARY_PACKAGE_NAME}.oem_id_prefs",
      Context.MODE_PRIVATE
    )
  )
}

private val Payments.extractor: IExtract by lazyInit {
  Extractor(
    ExtractorV1(),
    ExtractorV2()
  )
}
