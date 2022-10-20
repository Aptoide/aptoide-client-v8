package cm.aptoide.pt.aptoide_network.data.network.base_response

import androidx.annotation.Keep

@Keep
class BaseV7DataListResponse<T> : BaseV7Response() {
  var datalist: DataList<T>? = null
  var total: Int = 0
    get() = if (hasData()) datalist!!.total else 0
  var nextSize: Int = 0
    get() = if (hasData()) datalist!!.next else 0

  private fun hasData(): Boolean {
    return datalist?.list != null
  }
}