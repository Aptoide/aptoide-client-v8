package cm.aptoide.pt.autoupdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import cm.aptoide.pt.R
import cm.aptoide.pt.view.fragment.BaseDialogView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.auto_update_dialog_fragment.*
import rx.Observable
import javax.inject.Inject

class AutoUpdateDialogFragment : BaseDialogView(), AutoUpdateDialogView {

  @Inject
  lateinit var presenter: AutoUpdateDialogPresenter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    attachPresenter(presenter)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.window.setBackgroundDrawableResource(R.drawable.transparent)

    return inflater.inflate(R.layout.auto_update_dialog_fragment, container, false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun updateClicked(): Observable<Void> {
    return RxView.clicks(update_button)
  }

  override fun notNowClicked(): Observable<Void> {
    return RxView.clicks(not_now_button)
  }

  override fun dismissDialog() {
    dismiss()
  }
}