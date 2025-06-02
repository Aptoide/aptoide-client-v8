package com.aptoide.android.aptoidegames.firebase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.extensions.runPreviewable
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun rememberFirebaseToken(): String? = runPreviewable(
  preview = { getRandomString(10..20, "") },
  real = {
    var token: String? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
      FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
          token = task.result
        }
      }
    }
    token
  }
)
