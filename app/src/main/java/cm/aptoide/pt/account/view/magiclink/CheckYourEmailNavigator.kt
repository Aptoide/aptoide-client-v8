package cm.aptoide.pt.account.view.magiclink

import android.content.Intent
import cm.aptoide.pt.navigator.ActivityNavigator

class CheckYourEmailNavigator(private val activityNavigator: ActivityNavigator?) {

  fun navigateToEmailApp() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_APP_EMAIL)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    activityNavigator?.navigateWithIntent(intent)
  }
}