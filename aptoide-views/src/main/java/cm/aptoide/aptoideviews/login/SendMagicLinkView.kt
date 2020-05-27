package cm.aptoide.aptoideviews.login

import android.content.Context
import android.text.SpannedString
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import cm.aptoide.aptoideviews.R
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.send_magic_link_layout.view.*
import rx.Observable

class SendMagicLinkView : FrameLayout {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    inflate(context, R.layout.send_magic_link_layout, this)
    setState(State.Initial)
    setupViews()
    isSaveEnabled = true
  }

  private fun setupViews() {
    val string: SpannedString = buildSpannedString {
      bold {
        append(context.getText(R.string.login_safe_body_1))
      }
      append(" - ")
      append(context.getText(R.string.login_safe_body_2))
    }
    login_benefits_textview.text = string
  }

  fun setState(state: State) = when (state) {
    State.Initial ->
      setInitialState()
    is State.Error ->
      setErrorState(state.message, state.isTextFieldError)
  }

  fun getMagicLinkSubmit(): Observable<String> {
    return RxView.clicks(send_magic_link_button)
        .map { email.text.toString() }
  }

  fun getEmailChangeEvent(): Observable<String> {
    return RxTextView.textChangeEvents(email)
        .map { email.text.toString() }
  }

  private fun setInitialState() {
    tip.visibility = View.VISIBLE
    tip_error.visibility = View.GONE

    email.setTextAppearance(context, R.style.Aptoide_TextView_Regular_S_Primary)
    val typedValue = TypedValue()
    context.theme.resolveAttribute(R.attr.loginInputBackground, typedValue, true)
    email.setBackgroundResource(typedValue.resourceId)
  }

  private fun setErrorState(message: String, textFieldError: Boolean) {
    tip.visibility = View.GONE
    tip_error.visibility = View.VISIBLE
    tip_error.text = message

    if (textFieldError) {
      email.setTextAppearance(context, R.style.Aptoide_TextView_Regular_S_OrangeGradientEnd)
      val typedValue = TypedValue()
      context.theme.resolveAttribute(R.attr.loginInputErrorBackground, typedValue, true)
      email.setBackgroundResource(typedValue.resourceId)
    }
  }

  sealed class State {
    object Initial : State()
    data class Error(val message: String, val isTextFieldError: Boolean) : State()
  }
}