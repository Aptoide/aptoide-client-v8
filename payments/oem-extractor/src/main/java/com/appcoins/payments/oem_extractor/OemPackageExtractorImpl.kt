package com.appcoins.payments.oem_extractor

import android.content.Context
import android.os.Build

class OemPackageExtractorImpl(private val context: Context) : OemPackageExtractor {

  override fun extractOemPackage(packageName: String?): String = packageName?.let {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      context.packageManager.getInstallSourceInfo(it).installingPackageName
    } else {
      @Suppress("DEPRECATION")
      context.packageManager.getInstallerPackageName(it)
    }
  } ?: ""
}
