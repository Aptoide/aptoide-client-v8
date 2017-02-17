/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.pt.navigation.AccountNavigator;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GooglePlayServicesFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by trinkes on 5/2/16.
 */
public class MyAccountFragment extends GooglePlayServicesFragment {

  private Button mLogout;
  private TextView usernameTextView;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private GoogleApiClient client;

  private View inflate(LayoutInflater layoutInflater) {
    return layoutInflater.inflate(R.layout.my_account_activity, null);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflate(inflater);

    mLogout = (Button) view.findViewById(R.id.button_logout);
    usernameTextView = (TextView) view.findViewById(R.id.username);

    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final FragmentActivity parentActivity = getActivity();
    mLogout.setOnClickListener(v -> {
      accountManager.logout(client);
      accountNavigator.navigateToAccountView();
      parentActivity.finish();
    });
    accountManager = ((V8Engine) parentActivity.getApplicationContext()).getAccountManager();

    accountNavigator = new AccountNavigator(getContext(), accountManager);
    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();
    client = getClientBuilder().addApi(GOOGLE_SIGN_IN_API, options).build();
    usernameTextView.setText(accountManager.getUserEmail());
  }

  @Override protected void connect() {
    client.connect();
  }
}
