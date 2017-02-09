/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.AptoideWsV3Exception;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.accountmanager.ws.responses.GenericResponseV3;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.presenter.GoogleLoginPresenter;
import cm.aptoide.pt.v8engine.view.LoginView;
import com.google.android.gms.common.SignInButton;

/**
 * Created by marcelobenites on 06/02/17.
 */
public class LoginActivity extends GoogleLoginActivity implements LoginView {

  private ProgressDialog progressDialog;
  private View content;
  private SignInButton googleLoginButton;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity_layout);

    googleLoginButton =
        (SignInButton) findViewById(cm.aptoide.accountmanager.R.id.g_sign_in_button);
    content = findViewById(android.R.id.content);
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this);

    final AptoideAccountManager accountManager = AptoideAccountManager.getInstance(
        this,
        Application.getConfiguration(),
        new SecureCoderDecoder.Builder(this).create(),
        AccountManager.get(this),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), this));

    attachPresenter(
        new GoogleLoginPresenter(this, accountManager),
        savedInstanceState);
  }

  @Override protected SignInButton getGoogleButton() {
    return googleLoginButton;
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(Throwable throwable) {
    final String message;
    if (throwable instanceof AptoideWsV3Exception) {
      final GenericResponseV3 oAuth = ((AptoideWsV3Exception) throwable).getBaseResponse();
      message = getString(ErrorsMapper.getWebServiceErrorMessageFromCode(oAuth.getError()));
    } else {
      message = getString(cm.aptoide.accountmanager.R.string.unknown_error);
    }
    ShowMessage.asSnack(content, message);
  }
}