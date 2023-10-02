package cm.aptoide.pt.osp_handler.handler

interface OemIdExtractorService {
  suspend fun extractOemId(packageName: String?): String?
}
