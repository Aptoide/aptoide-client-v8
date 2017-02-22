/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.pt.navigation.AccountNavigator;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GooglePlayServicesFragment;
import cm.aptoide.pt.v8engine.presenter.MyAccountPresenter;
import cm.aptoide.pt.v8engine.view.MyAccountView;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by trinkes on 5/2/16.
 */
public class MyAccountFragment extends GooglePlayServicesFragment implements MyAccountView {

  private Button logoutButton;
  private TextView usernameTextView;
  private GoogleApiClient client;

  public static Fragment newInstance() {
    return new MyAccountFragment();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflate(inflater);

    logoutButton = (Button) view.findViewById(R.id.button_logout);
    usernameTextView = (TextView) view.findViewById(R.id.username);

    return view;
  }

  private View inflate(LayoutInflater layoutInflater) {
    return layoutInflater.inflate(R.layout.my_account_activity, null);
  }

  public Observable<Void> signOutClick() {
    return RxView.clicks(logoutButton);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    AptoideAccountManager accountManager =
        ((V8Engine) getActivity().getApplicationContext()).getAccountManager();

    AccountNavigator accountNavigator =
        new AccountNavigator(NavigationManagerV4.Builder.buildWith(getActivity()), accountManager);

    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();

    client = getClientBuilder().addApi(GOOGLE_SIGN_IN_API, options).build();
    usernameTextView.setText(accountManager.getUserEmail());

    attachPresenter(new MyAccountPresenter(this, accountManager, accountNavigator, client),
        savedInstanceState);
  }

  @Override protected void connect() {
    client.connect();
  }
}
