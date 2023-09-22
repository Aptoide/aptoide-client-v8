package cm.aptoide.pt.guest_wallet.unique_id

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UniqueIDProviderImpl @Inject constructor() : UniqueIDProvider {
  override fun getUniqueId(): String {
    TODO("Not yet implemented")
  }

}
internal interface UniqueIDProvider {
  fun getUniqueId(): String
}