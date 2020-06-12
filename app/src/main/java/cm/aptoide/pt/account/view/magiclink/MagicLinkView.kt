package cm.aptoide.pt.account.view.magiclink

import cm.aptoide.pt.presenter.View
import rx.Observable


interface MagicLinkView : View {

  fun getMagicLinkClick(): Observable<String>

  fun setInitialState()

  fun removeTextFieldError()

  fun setEmailInvalidError()

  fun setLoadingScreen()

  fun removeLoadingScreen()

  fun getEmailTextChangeEvent(): Observable<String>

  fun showUnknownError()

  fun getSecureLoginTextClick(): Observable<Void>

}