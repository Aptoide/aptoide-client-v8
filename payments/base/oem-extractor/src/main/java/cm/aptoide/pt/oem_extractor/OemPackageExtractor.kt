package cm.aptoide.pt.oem_extractor

interface OemPackageExtractor {
  fun extractOemPackage(packageName: String?): String
}
