package cm.aptoide.pt.osp_handler.handler

interface OemIdExtractor {
  suspend fun extractOemId(packageName: String?): String?
}
