package cm.aptoide.pt.home.apps.model

import cm.aptoide.pt.home.apps.App

data class DownloadApp(val name: String,
                       override val md5: String,
                       val icon: String,
                       override val packageName: String,
                       override val progress: Int,
                       val version: String,
                       override val versionCode: Int,
                       override var status: StateApp.Status? = null,
                       val appId: Long) : StateApp {

  override fun getIdentifier(): String {
    return md5
  }

  override fun getType(): App.Type {
    return App.Type.DOWNLOAD
  }

}