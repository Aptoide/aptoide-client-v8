package cm.aptoide.pt.feature_apps.data.network.model

class BaseV7DataListResponse<T> : BaseV7Response() {
  val datalist: DataList<T>? = null
  val total: Int
    get() = if (hasData()) datalist!!.total else 0
  val nextSize: Int
    get() = if (hasData()) datalist!!.next else 0

  private fun hasData(): Boolean {
    return datalist?.list != null
  }
}