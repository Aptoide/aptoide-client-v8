package cm.aptoide.pt

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cm.aptoide.pt.FirebaseConstants.LAUNCH_SOURCE
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class AptoideFirebaseNotificationService : FirebaseMessagingService() {

  @Inject
  lateinit var firebaseNotificationAnalytics: FirebaseNotificationAnalytics

  companion object {
    const val FCM_NOTIFICATION_CHANNEL_ID = "fcm_notification_channel"
    const val FCM_NOTIFICATION_CHANNEL_NAME = "Remote Notification Channel"
  }

  override fun onCreate() {
    super.onCreate()
    (applicationContext as AptoideApplication).applicationComponent.inject(this)
  }

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    if (message.notification != null) {
      setupNotificationChannel(applicationContext)
      showNotification(applicationContext, message)
    }
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Log.d("firebase", "onNewToken: $token")
  }

  override fun handleIntent(intent: Intent?) {
    try {
      intent!!.putExtra(LAUNCH_SOURCE, "notification")
      val remoteMessage = RemoteMessage(intent.extras)
      onMessageReceived(remoteMessage)

      intent.extras?.let {
        val messageId = it.getString(FirebaseConstants.FIREBASE_MESSAGE_ID)
        val messageName = it.getString(FirebaseConstants.FIREBASE_MESSAGE_NAME)
        if (messageId != null && messageName != null) {
          val messageDeviceTime = System.currentTimeMillis()
          val label = it.getString(FirebaseConstants.FIREBASE_MESSAGE_LABEL)
          val hasNotificationsPermission = NotificationManagerCompat.from(applicationContext)
            .areNotificationsEnabled()
          firebaseNotificationAnalytics.sendFirebaseNotificationReceived(
            messageId = messageId,
            messageName = messageName,
            messageDeviceTime = messageDeviceTime,
            label = label,
            hasNotificationPermissions = hasNotificationsPermission
          )
        }
      }
      //super.handleIntent(intent)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun setupNotificationChannel(context: Context) {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      if (notificationManager.getNotificationChannel(FCM_NOTIFICATION_CHANNEL_ID) == null) {
        NotificationChannel(
          FCM_NOTIFICATION_CHANNEL_ID,
          FCM_NOTIFICATION_CHANNEL_NAME,
          NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
          setSound(null, null)
        }.let {
          notificationManager.createNotificationChannel(it)
        }
      }
    }
  }

  private fun showNotification(context: Context, message: RemoteMessage) {
    message.notification?.let {
      val notificationId = message.messageId.hashCode()

      val clickIntent = PendingIntent.getActivity(
        context,
        notificationId,
        Intent(context, MainActivity::class.java)
          .putExtras(message.toIntent())
          .putExtra(FirebaseConstants.FIREBASE_MESSAGE_ID, message.messageId)
          .putExtra(FirebaseConstants.FIREBASE_ANALYTICS_DATA, message.toIntent().extras),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
      )

      val notification =
        NotificationCompat.Builder(applicationContext, FCM_NOTIFICATION_CHANNEL_ID)
          .setContentIntent(clickIntent)
          .setOngoing(false)
          .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
          .setLargeIcon(
            ImageLoader.with(applicationContext)
              .loadBitmap(it.imageUrl.toString())
          )
          .setContentTitle(it.title)
          .setContentText(it.body)
          .setPriority(NotificationCompat.PRIORITY_DEFAULT)
          .setAutoCancel(true)
          .setShowWhen(true)
          .build()

      with(NotificationManagerCompat.from(applicationContext)) {
        notify(notificationId, notification)
      }
    }
  }
}

public object FirebaseConstants {
  const val FIREBASE_MESSAGE_ID = "google.message_id"
  const val FIREBASE_MESSAGE_NAME = "google.c.a.c_l"
  const val FIREBASE_MESSAGE_LABEL = "google.c.a.m_l"
  const val FIREBASE_ANALYTICS_DATA = "gcm.n.analytics_data"
  const val LAUNCH_SOURCE = "launchSource"
}
