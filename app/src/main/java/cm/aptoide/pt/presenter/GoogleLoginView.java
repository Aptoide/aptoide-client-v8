/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.presenter;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import rx.Observable;

public interface GoogleLoginView extends View {

  void showGoogleLogin();

  void hideGoogleLogin();

  Observable<GoogleSignInResult> googleLoginClick();
}
