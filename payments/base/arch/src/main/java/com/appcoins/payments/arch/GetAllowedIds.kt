package com.appcoins.payments.arch

interface GetAllowedIds {
  suspend operator fun invoke(): Set<String>
}

class StaticGetAllowedIds(private vararg val ids: Set<String>) : GetAllowedIds {
  override suspend fun invoke(): Set<String> = ids.toList().flatten().toSet()
}
