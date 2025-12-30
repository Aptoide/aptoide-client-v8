package cm.aptoide.pt.feature_updates.data

interface StoreNameProvider {
  suspend fun getStoreName(): String?
}
