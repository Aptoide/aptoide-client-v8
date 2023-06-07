package cm.aptoide.pt.settings.domain

import android.net.Uri

data class Feedback(
  val destinationEmail: String,
  val subject: String,
  val body: String = "",
  val logs: Uri? = null,
)
