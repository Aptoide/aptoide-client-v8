package cm.aptoide.pt.aptoide_network.data.network.base_response

import androidx.annotation.Keep

@Keep
class BaseV7ListResponse<T> : BaseV7Response() {
  var list: List<T>? = null
  var total: Int = 0
    get() = if (hasData()) list!!.size else 0

  private fun hasData(): Boolean {
    return list != null
  }
}