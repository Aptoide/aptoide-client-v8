package cm.aptoide.pt.download.view.outofspace

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import cm.aptoide.pt.R
import cm.aptoide.pt.utils.AptoideUtils
import cm.aptoide.pt.view.fragment.BaseDialogView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.out_of_space_dialog_fragment.*
import rx.Observable
import javax.inject.Inject

class OutOfSpaceDialogFragment : BaseDialogView(), OutOfSpaceDialogView {

  @Inject
  lateinit var presenter: OutOfSpaceDialogPresenter
  private lateinit var controller: OutOfSpaceController
  private var requiredSpace: Long = 0

  companion object {
    const val APP_SIZE = "app_size"
    const val APP_PACKAGE_NAME = "package_name"
    const val OUT_OF_SPACE_REQUEST_CODE = 1994

    fun newInstance(requiredSpace: Long, packageName: String) = OutOfSpaceDialogFragment().apply {
      arguments = Bundle(2).apply {
        putLong(APP_SIZE, requiredSpace)
        putString(APP_PACKAGE_NAME, packageName)
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    arguments?.let {
      requiredSpace = arguments!!.getLong(APP_SIZE)
    }
    setupViews(requiredSpace)
    attachPresenter(presenter)
  }

  override fun getDialogStyle(): Int {
    return R.attr.roundedDialogsTheme
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    dialog?.window?.setBackgroundDrawableResource(R.drawable.transparent)
    super.onCreateView(inflater, container, savedInstanceState)
    return inflater.inflate(R.layout.out_of_space_dialog_fragment, container, false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun setupViews(requiredSpace: Long?) {
    controller = OutOfSpaceController()
    unninstall_apps_list.setController(controller)

    requiredSpace?.let {
      val requiredSpaceString: String = AptoideUtils.StringU.formatBytes(requiredSpace, false)
      setOutOfSpaceMessage(requiredSpaceString)
    }
  }

  override fun showInstalledApps(installedApps: List<InstalledApp>) {
    apps_list_group.visibility = View.VISIBLE
    out_of_space_progress_bar.visibility = View.GONE
    controller.setData(installedApps)
  }

  override fun uninstallClick(): Observable<String> {
    return controller.uninstallEvent
  }

  override fun dismissDialogClick(): Observable<Void> {
    return Observable.merge(RxView.clicks(cancel_button), RxView.clicks(ok_button))
  }

  private fun setOutOfSpaceMessage(requiredSpaceString: String) {
    val outOfSpaceMessage: String = getString(R.string.out_of_space_body,
        requiredSpaceString)
    val spannable = SpannableString(outOfSpaceMessage)
    spannable.setSpan(ForegroundColorSpan(
        resources.getColor(R.color.default_orange_gradient_end)),
        outOfSpaceMessage.indexOf(requiredSpaceString),
        outOfSpaceMessage.indexOf(requiredSpaceString) + requiredSpaceString.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    out_of_space_description.text = spannable
  }

  override fun requiredSpaceToInstall(requiredAppSpace: Long) {
    val requiredSpaceString: String =
        AptoideUtils.StringU.formatBytes(requiredAppSpace, false)
    setOutOfSpaceMessage(requiredSpaceString)
  }

  override fun showGeneralOutOfSpaceError() {
    general_message_out_of_space_group.visibility = View.VISIBLE
    out_of_space_progress_bar.visibility = View.GONE
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    controller.onSaveInstanceState(outState)
  }

  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    controller.onRestoreInstanceState(savedInstanceState)
  }
}