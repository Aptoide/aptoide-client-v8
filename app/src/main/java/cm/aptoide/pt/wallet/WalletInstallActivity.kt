package cm.aptoide.pt.wallet

import android.os.Bundle
import cm.aptoide.pt.R
import cm.aptoide.pt.view.ActivityView
import javax.inject.Inject


class WalletInstallActivity : ActivityView(), WalletInstallView {

  @Inject
  lateinit var presenter: WalletInstallPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent.inject(this)

    setContentView(R.layout.wallet_install_activity)
    attachPresenter(presenter)
  }

}