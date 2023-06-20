package cm.aptoide.pt.settings.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import cm.aptoide.pt.settings.domain.Feedback
import timber.log.Timber

fun Context.sendMail(feedback: Feedback) {
  try {
    val intent = Intent(Intent.ACTION_SEND)
      .apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(feedback.destinationEmail))
        putExtra(Intent.EXTRA_SUBJECT, feedback.subject)
        putExtra(Intent.EXTRA_TEXT, feedback.body)
        putExtra(Intent.EXTRA_STREAM, feedback.logs)
      }
    startActivity(Intent.createChooser(intent, "Sending email..."))
  } catch (t: Throwable) {
    Timber.e(t)
  }
}
