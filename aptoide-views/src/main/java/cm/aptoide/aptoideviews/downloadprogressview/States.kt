package cm.aptoide.aptoideviews.downloadprogressview

internal sealed class State {
  object Indeterminate: State()
  object InProgress: State()
  object Paused: State()
  object Installing: State()
}

internal sealed class Event {
  // User input driven events
  object ResumeClick: Event()
  object PauseClick: Event()
  object CancelClick: Event()

  // System driven events
  object DownloadStart: Event()
  object InstallStart: Event() // This also implies the end of the download
  object Reset: Event()
}