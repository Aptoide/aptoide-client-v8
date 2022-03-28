package cm.aptoide.pt.feature_apps.data.network.model

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

  class Info(var status: Status?, var time: Time?) {

    enum class Status {
      OK, QUEUED, FAIL, Processing
    }

    class Time {
      var seconds = 0.0
      var human: String? = null
    }
  }

  class Error {
    var code: String? = null
    var description: String? = null
  }
}