package cm.aptoide.pt.oem_extractor.cache

interface OemIdPreferences {
  fun putOemId(key: String?, value: String?)
  fun getOemId(key: String?): String?
}
