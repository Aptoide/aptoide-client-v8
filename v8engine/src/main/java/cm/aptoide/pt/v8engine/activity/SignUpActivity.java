/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cm.aptoide.accountmanager.*;
import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.ErrorsMapper;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 4/29/16.
 */
public class SignUpActivity extends BaseActivity {

  private Button signUpButton;
  private Toolbar toolbar;
  private EditText passwordEditText;
  private EditText emailEditText;
  private Button hideShowPasswordButton;
  private View content;
  private ProgressDialog progressDialog;

  private AptoideAccountManager accountManager;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sign_up_activity_layout);
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this);
    accountManager = ((V8Engine) getApplicationContext()).getAccountManager();
    bindViews();
    setupToolbar();
    setupListeners();
    RxView.clicks(signUpButton)
        .doOnNext(click -> progressDialog.show())
        .flatMap(click -> accountManager.createAccount(getUsername(), getPassword())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> showAccountCreatedMesssageAndNavigateToCreateUser())
            .doOnError(throwable -> showError(throwable))
            .doOnUnsubscribe(() -> progressDialog.dismiss())
            .toObservable())
        .retry()
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();
    if (i == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void bindViews() {
    content = findViewById(android.R.id.content);
    signUpButton = (Button) findViewById(R.id.submitCreateUser);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    emailEditText = (EditText) findViewById(R.id.username);
    passwordEditText = (EditText) findViewById(R.id.password);
    hideShowPasswordButton = (Button) findViewById(R.id.btn_show_hide_pass);
  }

  private void setupToolbar() {
    if (toolbar != null) {
      setSupportActionBar(toolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle(R.string.register);
    }
  }

  private void setupListeners() {
    setupShowHidePassButton();
  }

  private void setupShowHidePassButton() {
    hideShowPasswordButton.setOnClickListener(v -> {
      final int cursorPosition = passwordEditText.getSelectionStart();
      final boolean passwordShown = passwordEditText.getTransformationMethod() == null;
      v.setBackgroundResource(
          passwordShown ? R.drawable.icon_closed_eye : R.drawable.icon_open_eye);
      passwordEditText.setTransformationMethod(
          passwordShown ? new PasswordTransformationMethod() : null);
      passwordEditText.setSelection(cursorPosition);
    });
  }

  private void showAccountCreatedMesssageAndNavigateToCreateUser() {
    ShowMessage.asSnack(content, R.string.user_created);
    startActivity(new Intent(this, CreateUserActivity.class));
    finish();
  }

  private void showError(Throwable throwable) {
    int errorString = R.string.unknown_error;
    if (throwable instanceof AccountValidationException) {
      switch (((AccountValidationException) throwable).getCode()) {
        case AccountValidationException.EMPTY_EMAIL:
          errorString = R.string.no_email_error_message;
          break;
        case AccountValidationException.EMPTY_EMAIL_AND_PASSWORD:
          errorString = R.string.no_email_and_pass_error_message;
          break;
        case AccountValidationException.EMPTY_PASSWORD:
          errorString = R.string.no_pass_error_message;
          break;
        case AccountValidationException.INVALID_PASSWORD:
          errorString = R.string.password_validation_text;
          break;
      }
    } else if (throwable instanceof OAuthException) {
      final OAuth oAuth = ((OAuthException) throwable).getoAuth();
      errorString =
          ErrorsMapper.getWebServiceErrorMessageFromCode(oAuth.getErrors().get(0).getCode());
    } ShowMessage.asSnack(content, errorString);
  }

  private String getPassword() {
    return passwordEditText.getText().toString();
  }

  private String getUsername() {
    return emailEditText.getText().toString();
  }
}
