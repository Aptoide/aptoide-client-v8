package cm.aptoide.pt.osp_handler.handler

interface OemIdExtractor {
  fun extractOemId(packageName: String?): String?
}
