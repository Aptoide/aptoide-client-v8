package com.appcoins.payments.guest_wallet.unique_id

import com.appcoins.payments.guest_wallet.unique_id.generator.IDGenerator
import com.appcoins.payments.guest_wallet.unique_id.repository.UniqueIdRepository

internal class UniqueIDProviderImpl(
  private val generator: IDGenerator,
  private val uniqueIdRepository: UniqueIdRepository,
) : UniqueIDProvider {
  override suspend fun createUniqueId(): String = generator.generateUniqueID()
    .also { uniqueIdRepository.storeUniqueId(it) }

  override suspend fun getUniqueId(): String? = uniqueIdRepository.getUniqueId()
}

interface UniqueIDProvider {
  suspend fun createUniqueId(): String

  suspend fun getUniqueId(): String?
}
