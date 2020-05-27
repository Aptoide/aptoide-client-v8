package cm.aptoide.pt.account.view.magiclink

import cm.aptoide.pt.presenter.View
import rx.Observable


interface MagicLinkView : View {

  fun getMagicLinkClick(): Observable<String>

  fun setInitialState()

  fun setEmailInvalidError()

  fun setExpiredMagicLinkError()

  fun setLoadingScreen()

  fun getEmailTextChangeEvent(): Observable<String>

}