package cm.aptoide.pt.feature_search.domain.repository

interface SearchStoreManager {

  fun shouldAddStore(): Boolean

  fun getStore(): String
}
