package cm.aptoide.pt.account.view.magiclink

import cm.aptoide.pt.navigator.FragmentNavigator

class SendMagicLinkNavigator(private val fragmentNavigator: FragmentNavigator) {

  fun navigateToCheckYourEmail(email: String) {
    fragmentNavigator.navigateTo(CheckYourEmailFragment.newInstance(email), true)
  }
}