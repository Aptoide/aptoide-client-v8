package cm.aptoide.pt.home.apps.model

import cm.aptoide.pt.home.apps.App

interface StateApp : App {
  val md5: String
  val status: Status?
  val progress: Int
  val packageName: String
  val versionCode: Int

  enum class Status {
    ACTIVE, INSTALLING, PAUSE, ERROR, STANDBY, IN_QUEUE
  }
}