package cm.aptoide.pt.guest_wallet.unique_id

import cm.aptoide.pt.guest_wallet.unique_id.generator.IDGenerator
import cm.aptoide.pt.guest_wallet.unique_id.repository.UniqueIdRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UniqueIDProviderImpl @Inject constructor(
  private val generator: IDGenerator,
  private val uniqueIdRepository: UniqueIdRepository,
) : UniqueIDProvider {
  override suspend fun getUniqueId(): String {
    val uniqueId = uniqueIdRepository.getUniqueId()
    return uniqueId ?: run {
      generator.generateUniqueID()
        .also { uniqueIdRepository.storeUniqueId(it) }
    }
  }
}

interface UniqueIDProvider {
  suspend fun getUniqueId(): String
}
