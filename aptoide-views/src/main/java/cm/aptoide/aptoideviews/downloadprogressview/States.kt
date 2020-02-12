package cm.aptoide.aptoideviews.downloadprogressview

internal sealed class State {
  object Queue : State()
  object InProgress : State()
  object Paused : State()
  object InitialPaused : State()
  object Installing : State()
  object Canceled : State()
}

internal sealed class Event {
  // User input driven events
  object ResumeClick : Event()

  object PauseClick : Event()
  object CancelClick : Event()

  // System driven events
  object DownloadStart : Event()

  object InstallStart : Event() // This also implies the end of the download
  object PauseStart : Event()
  object Reset : Event()
}