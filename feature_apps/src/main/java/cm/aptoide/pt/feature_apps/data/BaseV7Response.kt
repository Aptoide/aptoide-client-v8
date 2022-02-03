package cm.aptoide.pt.feature_apps.data

open class BaseV7Response {
  var info: Info? = null
  var errors: List<Error>? = null
  val error: Error?
    get() = if (errors != null && errors!!.isNotEmpty()) {
      errors!![0]
    } else {
      null
    }
  val isOk: Boolean
    get() = info != null && info!!.status == Info.Status.OK

  class Info {
    var status: Status? = null
    var time: Time? = null

    constructor() {}
    constructor(status: Status?, time: Time?) {
      this.status = status
      this.time = time
    }

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