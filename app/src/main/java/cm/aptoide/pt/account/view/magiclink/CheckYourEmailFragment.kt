package cm.aptoide.pt.account.view.magiclink

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory
import cm.aptoide.pt.R
import cm.aptoide.pt.view.NotBottomNavigationView
import cm.aptoide.pt.view.fragment.BaseToolbarFragment
import com.jakewharton.rxbinding.view.RxView
import rx.Observable
import javax.inject.Inject


class CheckYourEmailFragment : BaseToolbarFragment(), CheckYourEmailView, NotBottomNavigationView {

  @Inject
  lateinit var presenter: CheckYourEmailPresenter

  lateinit var openEmailAppButton: Button
  lateinit var openEmailBody: TextView
  private var email: String? = null

  companion object {
    private const val EMAIL = "email"

    fun newInstance(email: String) = CheckYourEmailFragment().apply {
      arguments = Bundle().apply {
        putString(EMAIL, email)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFragmentComponent(savedInstanceState).inject(this)
    setHasOptionsMenu(true)
  }

  override fun loadExtras(args: Bundle?) {
    super.loadExtras(args)
    email = args?.getString(EMAIL)
  }

  override fun setupViews() {
    super.setupViews()
    attachPresenter(presenter)
  }

  override fun bindViews(view: View?) {
    super.bindViews(view)

    view?.let { v ->
      openEmailAppButton = v.findViewById(R.id.open_email_app_button)
      openEmailBody = v.findViewById(R.id.check_your_email_body_text)
      openEmailBody.text = getString(R.string.login_check_email_body, email)
    }
  }

  override fun getHistoryTracker(): ScreenTagHistory? {
    return ScreenTagHistory.Builder.build(this.javaClass
        .simpleName)
  }

  override fun getContentViewId(): Int {
    return R.layout.fragment_check_your_email
  }

  override fun hasToolbar(): Boolean {
    return true
  }

  override fun displayHomeUpAsEnabled(): Boolean {
    return true
  }

  override fun setupToolbarDetails(toolbar: Toolbar?) {
    toolbar?.title = ""
  }

  override fun getCheckYourEmailClick(): Observable<Void> {
    return RxView.clicks(openEmailAppButton)
  }
}