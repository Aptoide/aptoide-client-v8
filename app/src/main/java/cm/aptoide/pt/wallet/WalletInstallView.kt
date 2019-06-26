package cm.aptoide.pt.wallet

import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.presenter.View
import cm.aptoide.pt.promotions.WalletApp
import rx.Observable

interface WalletInstallView : View {

  fun showWalletInstallationView(appIcon: String?,
                                 walletApp: WalletApp)

  fun showInstallationSuccessView()

  fun closeButtonClicked(): Observable<Void>

  fun dismissDialog()

  fun showRootInstallWarningPopup(): Observable<Boolean>?

  fun showIndeterminateDownload()

  fun cancelDownloadButtonClicked(): Observable<Void>

  fun showDownloadState(downloadModel: DownloadModel)
}