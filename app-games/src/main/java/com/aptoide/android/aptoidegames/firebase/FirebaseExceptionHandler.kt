package com.aptoide.android.aptoidegames.firebase

import cm.aptoide.pt.exception_handler.ExceptionHandler
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Firebase-based implementation of ExceptionHandler that records exceptions to Firebase Crashlytics.
 * Does not record CancellationExceptions as they are part of normal coroutine flow.
 */
class FirebaseExceptionHandler @Inject constructor() : ExceptionHandler {
  override fun recordException(exception: Throwable) {
    // Don't record cancellation exceptions as they are expected behavior
    if (exception !is CancellationException) {
      Firebase.crashlytics.recordException(exception)
    }
  }
}
