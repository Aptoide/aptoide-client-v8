package com.aptoide.android.aptoidegames.play_and_earn.data

import com.aptoide.android.aptoidegames.play_and_earn.domain.UserInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserInfoRepository @Inject constructor(
  private val userAccountPreferencesRepository: UserAccountPreferencesRepository
) : UserInfoRepository {

  override fun observeUserInfo(): Flow<UserInfo?> {
    return userAccountPreferencesRepository.getUserInfo()
  }
}
