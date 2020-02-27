package cm.aptoide.pt.themes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cm.aptoide.pt.R
import cm.aptoide.pt.view.fragment.BaseBottomSheetDialogView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.dark_theme_dialog.*
import rx.Observable
import javax.inject.Inject

class DarkThemeDialogFragment : BaseBottomSheetDialogView(), DarkThemeDialogView {

  @Inject
  lateinit var presenter: DarkThemeDialogPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.dark_theme_dialog, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    attachPresenter(presenter)
  }

  override fun clickDismiss(): Observable<Void> {
    return RxView.clicks(dismissButton)
  }

  override fun clickTurnItOn(): Observable<Void> {
    return RxView.clicks(turnItOnButton)
  }

  override fun dismissView() {
    this.dismiss()
  }
}