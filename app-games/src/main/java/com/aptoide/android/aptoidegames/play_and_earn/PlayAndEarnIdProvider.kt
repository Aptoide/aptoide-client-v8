package com.aptoide.android.aptoidegames.play_and_earn

import com.aptoide.android.aptoidegames.LocalIdsRepository
import com.aptoide.android.aptoidegames.attribution.domain.AttributionManager
import com.aptoide.android.aptoidegames.firebase.FirebaseInfoProvider
import javax.inject.Inject
import javax.inject.Singleton

/** The id that identifies the user to Play & Earn (sent as `guest_id`). */
fun interface PaEGuestIdSource {
  suspend fun getId(): String
}

/**
 * Resolves the id used to identify the user in Play & Earn: the guest id when available,
 * falling back to the Firebase token when the guest id is missing.
 */
@Singleton
class PlayAndEarnIdProvider @Inject constructor(
  private val localIdsRepository: LocalIdsRepository,
  private val firebaseInfoProvider: FirebaseInfoProvider,
) : PaEGuestIdSource {

  override suspend fun getId(): String {
    val guestId = localIdsRepository.getId(AttributionManager.GUEST_UID_KEY)
    return guestId.ifBlank { firebaseInfoProvider.getFirebaseToken().orEmpty() }
  }
}
