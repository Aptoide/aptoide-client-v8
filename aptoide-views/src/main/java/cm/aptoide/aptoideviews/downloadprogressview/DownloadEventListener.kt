package cm.aptoide.aptoideviews.downloadprogressview

interface DownloadEventListener {
  fun onActionClick(action: Action)

  data class Action(val type: Type, val payload: Any?) {
    enum class Type {
      CANCEL, RESUME, PAUSE
    }
  }

}