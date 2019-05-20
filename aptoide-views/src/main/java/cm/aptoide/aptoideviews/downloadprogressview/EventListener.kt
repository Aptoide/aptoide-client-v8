package cm.aptoide.aptoideviews.downloadprogressview

interface EventListener {
  fun onActionClick(action: Action)

  enum class Action {
    CANCEL, RESUME, PAUSE
  }
}