/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cm.aptoide.accountmanager.*;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by trinkes on 4/29/16.
 */
public class SignUpActivity extends AccountBaseActivity implements AptoideAccountManager.IRegisterUser {

  private Button signUpButton;
  private Toolbar mToolbar;
  private EditText password_box;
  private EditText emailBox;
  private Button hidePasswordButton;
  private View content;

  private String SIGNUP = "signup";
  private AptoideAccountManager accountManager;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    accountManager = ((V8Engine)getApplicationContext()).getAccountManager();
    bindViews();
    setupToolbar();
    setupListeners();
  }

  @Override public String getActivityTitle() {
    return getString(R.string.register);
  }

  @Override public int getLayoutId() {
    return R.layout.sign_up_activity_layout;
  }

  private void bindViews() {
    content = findViewById(android.R.id.content);
    signUpButton = (Button) findViewById(R.id.submitCreateUser);
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    emailBox = (EditText) findViewById(R.id.username);
    password_box = (EditText) findViewById(R.id.password);
    hidePasswordButton = (Button) findViewById(R.id.btn_show_hide_pass);
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle(getActivityTitle());
    }
  }

  private void setupListeners() {
    setupShowHidePassButton();
    accountManager.setupRegisterUser(this, signUpButton, this);
  }

  private void setupShowHidePassButton() {
    hidePasswordButton.setOnClickListener(v -> {
      final int cursorPosition = password_box.getSelectionStart();
      final boolean passwordShown = password_box.getTransformationMethod() == null;
      v.setBackgroundResource(
          passwordShown ? R.drawable.icon_closed_eye : R.drawable.icon_open_eye);
      password_box.setTransformationMethod(
          passwordShown ? new PasswordTransformationMethod() : null);
      password_box.setSelection(cursorPosition);
    });
  }

  @Override public void onRegisterSuccess(Bundle data) {
    ShowMessage.asSnack(content, R.string.user_created);
    data.putString(AptoideLoginUtils.APTOIDE_LOGIN_FROM, SIGNUP);
    setResult(RESULT_OK, new Intent().putExtras(data));
    Analytics analytics = accountManager.getAnalytics();
    if (analytics != null) {
      analytics.signUp();
    }
    accountManager.sendLoginBroadcast();
    finish();
  }

  @Override public void onRegisterFail(@StringRes int reason) {
    ShowMessage.asSnack(content, reason);
  }

  @Override public String getUserPassword() {
    return password_box == null ? "" : password_box.getText().toString();
  }

  @Override public String getUserEmail() {
    return emailBox == null ? "" : emailBox.getText().toString().toLowerCase();
  }
}
