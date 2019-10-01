package cm.aptoide.pt.home.more.appcoins

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import cm.aptoide.pt.R
import cm.aptoide.pt.app.DownloadModel
import cm.aptoide.pt.home.bundles.apps.RewardApp
import cm.aptoide.pt.home.more.base.ListAppsFragment
import cm.aptoide.pt.promotions.WalletApp
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment
import cm.aptoide.pt.utils.GenericDialogs
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.earn_appcoins_wallet_install_layout.*
import kotlinx.android.synthetic.main.wallet_install_cardview.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.exceptions.OnErrorNotImplementedException
import java.text.DecimalFormat
import javax.inject.Inject

class EarnAppcListFragment : ListAppsFragment<RewardApp, EarnAppcListViewHolder>(),
    EarnAppcListView {

  @Inject
  lateinit var presenter: EarnAppcListPresenter

  private var errorMessageSubscription: Subscription? = null

  private val oneDecimalFormatter = DecimalFormat("0.0")

  companion object {
    fun newInstance(title: String, tag: String): EarnAppcListFragment {
      val fragment = EarnAppcListFragment()

      val config = Bundle()
      config.putString(StoreTabGridRecyclerFragment.BundleCons.TITLE, title)
      config.putString(StoreTabGridRecyclerFragment.BundleCons.TAG, tag)

      fragment.arguments = config
      return fragment
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val listAppsLayout = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
    inflater.inflate(R.layout.earn_appcoins_wallet_install_layout, listAppsLayout)
    return listAppsLayout
  }

  override fun onDestroy() {
    errorMessageSubscription?.also { errorSubscription ->
      if (!errorSubscription.isUnsubscribed) {
        errorSubscription.unsubscribe()
      }
    }
    super.onDestroy()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.present()
  }

  override fun setupWallet(walletApp: WalletApp) {
    if (!walletApp.isInstalled) {
      app_cardview_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
      app_cardview_layout.visibility = View.VISIBLE
      wallet_app_title_textview.text = walletApp.appName
      rating_label.text = oneDecimalFormatter.format(walletApp.rating)
    }
  }

  override fun hideWalletArea() {
    val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
    animation.fillAfter = true
    app_cardview_layout.startAnimation(animation)
  }

  override fun updateState(walletApp: WalletApp) {
    walletApp.downloadModel?.also { downloadModel ->
      if (downloadModel.isDownloadingOrInstalling) {
        appview_transfer_info.visibility = View.VISIBLE
        install_group.visibility = View.GONE
        setDownloadState(downloadModel.progress, downloadModel.downloadState)
      } else if (!walletApp.isInstalled) {
        appview_transfer_info.visibility = View.GONE
        install_group.visibility = View.VISIBLE
        if (downloadModel.hasError()) {
          handleDownloadError(downloadModel.downloadState)
        }
      }
    }
  }

  private fun handleDownloadError(downloadState: DownloadModel.DownloadState?) {
    when (downloadState) {
      DownloadModel.DownloadState.ERROR -> showErrorDialog("",
          requireContext().getString(R.string.error_occured))
      DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR -> showErrorDialog(
          requireContext().getString(R.string.out_of_space_dialog_title),
          requireContext().getString(R.string.out_of_space_dialog_message))
      else -> throw IllegalStateException("Invalid Download State $downloadState")
    }
  }

  private fun showErrorDialog(title: String, message: String) {
    errorMessageSubscription = GenericDialogs.createGenericOkMessage(context, title, message)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe({ }, { error -> OnErrorNotImplementedException(error) })
  }

  private fun setDownloadState(progress: Int, downloadState: DownloadModel.DownloadState?) {
    val pauseShowing = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT, 4f)
    val pauseHidden = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT, 2f)
    when (downloadState) {
      DownloadModel.DownloadState.ACTIVE -> {
        appview_download_progress_bar.isIndeterminate = false
        appview_download_progress_bar.progress = progress
        appview_download_progress_number.text = "$progress%"
        appview_download_pause_download.visibility = View.VISIBLE
        appview_download_cancel_button.visibility = View.GONE
        appview_download_resume_download.visibility = View.GONE
        install_controls_layout.layoutParams = pauseShowing
        appview_download_download_state.text = getString(R.string.appview_short_downloading)
      }
      DownloadModel.DownloadState.INDETERMINATE -> {
        appview_download_progress_bar.isIndeterminate = true
        appview_download_pause_download.visibility = View.VISIBLE
        appview_download_cancel_button.visibility = View.GONE
        appview_download_resume_download.visibility = View.GONE
        install_controls_layout.layoutParams = pauseShowing
        appview_download_download_state.text = getString(R.string.appview_short_downloading)
      }
      DownloadModel.DownloadState.PAUSE -> {
        appview_download_progress_bar.isIndeterminate = false
        appview_download_progress_bar.progress = progress
        appview_download_progress_number.text = "$progress%"
        appview_download_pause_download.visibility = View.GONE
        appview_download_cancel_button.visibility = View.VISIBLE
        appview_download_resume_download.visibility = View.VISIBLE
        install_controls_layout.layoutParams = pauseHidden
        appview_download_download_state.text = getString(R.string.appview_short_downloading)
      }
      DownloadModel.DownloadState.COMPLETE -> {
        appview_download_progress_bar.isIndeterminate = true
        appview_download_pause_download.visibility = View.VISIBLE
        appview_download_cancel_button.visibility = View.GONE
        appview_download_resume_download.visibility = View.GONE
        install_controls_layout.layoutParams = pauseShowing
        appview_download_download_state.text = getString(R.string.appview_short_downloading)
      }
      DownloadModel.DownloadState.INSTALLING -> {
        appview_download_progress_bar.isIndeterminate = true
        appview_download_pause_download.visibility = View.GONE
        appview_download_cancel_button.visibility = View.GONE
        appview_download_resume_download.visibility = View.GONE
        install_controls_layout.layoutParams = pauseHidden
        appview_download_download_state.text = getString(R.string.appview_short_installing)
      }
      DownloadModel.DownloadState.ERROR -> showErrorDialog("",
          requireContext().getString(R.string.error_occured))
      DownloadModel.DownloadState.NOT_ENOUGH_STORAGE_ERROR -> showErrorDialog(
          requireContext().getString(R.string.out_of_space_dialog_title),
          requireContext().getString(R.string.out_of_space_dialog_message))
    }
  }

  override fun resumeDownload(): Observable<Void> {
    return RxView.clicks(appview_download_resume_download)
  }

  override fun pauseDownload(): Observable<Void> {
    return RxView.clicks(appview_download_pause_download)
  }

  override fun cancelDownload(): Observable<Void> {
    return RxView.clicks(appview_download_cancel_button)
  }

  override fun showRootInstallWarningPopup(): Observable<Boolean> {
    return GenericDialogs.createGenericYesNoCancelMessage(requireContext(), null,
        resources.getString(R.string.root_access_dialog))
        .map { response -> response.equals(GenericDialogs.EResponse.YES) }
  }

  override fun onWalletInstallClick(): Observable<Void> {
    return RxView.clicks(wallet_install_button)
  }

  override fun getItemSizeWidth(): Int {
    return 168
  }

  override fun getItemSizeHeight(): Int {
    return 158
  }

  override fun getContainerPaddingDp(): Rect {
    return Rect(8, 8, 8, 118)
  }

  override fun createViewHolder(): (ViewGroup, Int) -> EarnAppcListViewHolder {
    return { parent, viewType ->
      EarnAppcListViewHolder(
          LayoutInflater.from(parent.context).inflate(R.layout.earn_appcoins_item, parent,
              false), DecimalFormat("0.00"))
    }
  }

}