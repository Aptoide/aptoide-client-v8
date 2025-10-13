package com.aptoide.android.aptoidegames.device_info

import cm.aptoide.pt.environment_info.DeviceIdProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AGDeviceIdProvider @Inject constructor(
  private val firebaseMessaging: FirebaseMessaging
) : DeviceIdProvider {

  var deviceId: String? = null

  override suspend fun getDeviceId(): String? {
    return deviceId ?: firebaseMessaging.token.await().also { deviceId = it }
  }
}
