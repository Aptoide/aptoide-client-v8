package cm.aptoide.pt.oem_extractor

interface OemIdExtractor {
  fun extractOemId(packageName: String?): String?
}
