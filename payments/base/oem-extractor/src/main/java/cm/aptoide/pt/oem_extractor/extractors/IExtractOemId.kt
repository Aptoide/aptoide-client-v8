package cm.aptoide.pt.oem_extractor.extractors

interface IExtractOemId {
  suspend fun extractOemId(packageName: String): String?
}
