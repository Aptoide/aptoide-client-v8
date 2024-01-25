package com.appcoins.oem_extractor

interface OemPackageExtractor {
  fun extractOemPackage(packageName: String?): String
}
