package cm.aptoide.pt.download.view.outofspace

data class InstalledApp(val appName: String, val packageName: String, val icon: String,
                        val size: Long) {

  fun getIdentifier(): String {
    return packageName
  }
}
