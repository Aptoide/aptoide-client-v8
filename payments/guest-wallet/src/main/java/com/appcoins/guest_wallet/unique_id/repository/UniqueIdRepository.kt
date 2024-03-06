package com.appcoins.guest_wallet.unique_id.repository

import android.content.SharedPreferences
import com.appcoins.guest_wallet.BuildConfig
import com.appcoins.guest_wallet.di.UniqueIdSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UniqueIdRepositoryImpl @Inject constructor(
  @UniqueIdSharedPreferences private val sharedPreferences: SharedPreferences,
) : UniqueIdRepository {

  private companion object {
    private const val UNIQUE_ID = "${BuildConfig.LIBRARY_PACKAGE_NAME}.unique_id"
  }

  override suspend fun getUniqueId(): String? =
    sharedPreferences.getString(UNIQUE_ID, null)

  override suspend fun storeUniqueId(id: String) {
    sharedPreferences.edit().also { it.putString(UNIQUE_ID, id) }.apply()
  }
}

interface UniqueIdRepository {

  suspend fun getUniqueId(): String?
  suspend fun storeUniqueId(id: String)
}
