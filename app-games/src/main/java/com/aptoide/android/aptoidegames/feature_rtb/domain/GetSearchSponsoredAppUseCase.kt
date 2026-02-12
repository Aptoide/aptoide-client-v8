package com.aptoide.android.aptoidegames.feature_rtb.domain

import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import com.aptoide.android.aptoidegames.feature_rtb.repository.RTBRepository
import retrofit2.HttpException
import java.io.IOException

class GetSearchSponsoredAppUseCase(private val repository: RTBRepository) {
  suspend fun invoke(): RTBApp? = try {
    repository.getRTBApps("search").firstOrNull()
  } catch (e: Exception) {
    when (e) {
      is IOException, is HttpException -> null
      else -> throw e
    }
  }
}
