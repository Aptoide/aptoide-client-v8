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
  private var controller: OutOfSpaceController = OutOfSpaceController()


  companion object {
    const val REQUIRED_SPACE = "required_space"
    const val OUT_OF_SPACE_REQUEST_CODE = 1994

    fun newInstance(requiredSpace: Long) = OutOfSpaceDialogFragment().apply {
      arguments = Bundle(1).apply {
        putLong(REQUIRED_SPACE, requiredSpace)
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val requiredSpace = arguments?.getLong(REQUIRED_SPACE)
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
    unninstall_apps_list.setController(controller)

    requiredSpace?.let {
      val requiredSpaceString: String = AptoideUtils.StringU.formatBytes(requiredSpace, false)
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
  }

  override fun showInstalledApps(installedApps: List<InstalledApp>) {
    controller.setData(installedApps)
  }

  override fun uninstallClick(): Observable<String> {
    return controller.uninstallEvent
  }

  override fun cancelButtonClick(): Observable<Void> {
    return RxView.clicks(cancel_button)
  }

}