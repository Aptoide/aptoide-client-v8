package com.appcoins.payments.oem_extractor

interface OemIdExtractor {
  fun extractOemId(packageName: String?): String?
}