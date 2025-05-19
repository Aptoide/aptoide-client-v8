package cm.aptoide.pt.feature_updates.data

interface VIPUpdatesProvider {
  suspend fun getVIPUpdatesList(): List<String>
}
