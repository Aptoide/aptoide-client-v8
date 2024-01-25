package com.appcoins.oem_extractor

interface OemIdExtractor {
  fun extractOemId(packageName: String?): String?
}
