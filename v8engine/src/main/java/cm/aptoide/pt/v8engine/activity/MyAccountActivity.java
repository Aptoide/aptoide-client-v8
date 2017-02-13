/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.SimpleSubscriber;
import cm.aptoide.pt.v8engine.V8Engine;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by trinkes on 5/2/16.
 */
public class MyAccountActivity extends GooglePlayServicesActivity {

  private static final String TAG = MyAccountActivity.class.getSimpleName();

  private Button mLogout;
  private Toolbar mToolbar;
  private TextView mUsernameTextview;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private GoogleApiClient client;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(cm.aptoide.accountmanager.R.layout.my_account_activity);
    bindViews();
    setupToolbar();
    accountManager = ((V8Engine)getApplicationContext()).getAccountManager();
    accountNavigator = new AccountNavigator(this, accountManager);
    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();
    client = getClientBuilder()
        .addApi(GOOGLE_SIGN_IN_API, options)
        .build();
    mLogout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
          accountManager.logout(client);
          accountNavigator.navigateToAccountView();
          finish();
      }
    });
    mUsernameTextview.setText(accountManager.getUserEmail());
  }

  @Override protected GoogleApiClient getClient() {
    return client;
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(cm.aptoide.accountmanager.R.id.toolbar_login);
    mLogout = (Button) findViewById(cm.aptoide.accountmanager.R.id.button_logout);
    mUsernameTextview = (TextView) findViewById(cm.aptoide.accountmanager.R.id.username);
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle("My Account");
    }
  }
}
