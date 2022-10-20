package cm.aptoide.pt.aptoide_network.data.network.base_response

import androidx.annotation.Keep

@Keep
open class BaseV7Response {
  var info: Info? = null
  var errors: List<Error>? = null
  var error: Error? = null
    get() = if (errors != null && errors!!.isNotEmpty()) {
      errors!![0]
    } else {
      null
    }
  var isOk: Boolean = false
    get() = info != null && info!!.status == Info.Status.OK

  @Keep
  class Info(var status: Status?, var time: Time?) {

    @Keep
    enum class Status {
      OK, QUEUED, FAIL, Processing
    }

    @Keep
    class Time {
      var seconds = 0.0
      var human: String? = null
    }
  }

  @Keep
  class Error {
    var code: String? = null
    var description: String? = null
  }
}