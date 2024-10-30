package cm.aptoide.pt.feature_apps.data

interface AppRepository {

  suspend fun getApp(packageName: String): App

  suspend fun getAppMeta(source: String): App
}
