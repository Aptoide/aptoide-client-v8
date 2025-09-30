package com.aptoide.android.aptoidegames.play_and_earn.data

import com.aptoide.android.aptoidegames.play_and_earn.domain.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {

  fun observeUserInfo(): Flow<UserInfo?>
}
