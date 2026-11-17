package com.aptoide.android.aptoidegames.gamegenie.purchase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.DEEPLINK_KEY
import com.aptoide.android.aptoidegames.firebase.FirebaseNotificationBuilder
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import java.util.UUID

/**
 * HACKATHON, DEBUG BUILDS ONLY: posts the perk push notification locally, through the same
 * FirebaseNotificationBuilder the FCM service uses for a foreground notification message. The
 * message carries the same fields a real push would: title/body plus `dti.link` = the perk link.
 *
 * adb shell am broadcast -f 0x20 \
 *   -n <applicationId>/com.aptoide.android.aptoidegames.gamegenie.purchase.DebugPerkPushReceiver \
 *   --es package com.aptoide.diceroll.sdk.dev --es sku attempts \
 *   --es perk "20% bonus on Extra attempts" --es title "Diceroll" --es body "..."
 */
class DebugPerkPushReceiver : BroadcastReceiver() {

  @EntryPoint
  @InstallIn(SingletonComponent::class)
  interface Dependencies {
    fun firebaseNotificationBuilder(): FirebaseNotificationBuilder
  }

  override fun onReceive(context: Context, intent: Intent) {
    val item = GeniePurchaseCatalog.diceroll
    val packageName = intent.getStringExtra("package") ?: item.packageCandidates.first()
    val sku = intent.getStringExtra("sku") ?: item.sku
    val perk = intent.getStringExtra("perk") ?: "20% bonus on ${item.title}"
    val title = intent.getStringExtra("title") ?: "${item.gameName} has a perk for you"
    val body = intent.getStringExtra("body") ?: "$perk today. Tap and Genie gets it for you."
    val link = GeniePerkLink.build(BuildConfig.DEEP_LINK_SCHEMA, packageName, sku, perk)

    // The keys FCM itself uses for a notification message with a data payload.
    val message = RemoteMessage(
      Bundle().apply {
        putString("google.message_id", "debug-perk-${UUID.randomUUID()}")
        putString("gcm.n.e", "1")
        putString("gcm.n.title", title)
        putString("gcm.n.body", body)
        putString(DEEPLINK_KEY, link)
      }
    )
    Timber.d("Debug perk push: $link")
    EntryPointAccessors.fromApplication(context.applicationContext, Dependencies::class.java)
      .firebaseNotificationBuilder()
      .showFirebaseNotification(message)
  }
}
