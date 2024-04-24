package com.appcoins.payments.oem_extractor

interface OemPackageExtractor {
  fun extractOemPackage(packageName: String?): String
}
