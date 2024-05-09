package com.aptoide.android.aptoidegames.firebase

import cm.aptoide.pt.feature_categories.analytics.AptoideFirebaseInfoProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseInfoProvider @Inject constructor(
  private val firebaseMessaging: FirebaseMessaging
) :
  AptoideFirebaseInfoProvider {

  override suspend fun getFirebaseToken(): String? {
    return firebaseMessaging.token.await()
  }
}
