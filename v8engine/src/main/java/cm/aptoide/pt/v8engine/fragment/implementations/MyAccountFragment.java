/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
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
  public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(layoutInflater, container, savedInstanceState);
    View view = layoutInflater.inflate(getLayoutId(), null);
    logoutButton = (Button) view.findViewById(R.id.button_logout);
    usernameTextView = (TextView) view.findViewById(R.id.username);
    return view;
  }

  public int getLayoutId() {
    return R.layout.my_account_activity;
  }

  public Observable<Void> signOutClick() {
    return RxView.clicks(logoutButton);
  }

  @Override public void navigateToLoginAfterLogout() {
    getNavigationManager().cleanBackStack();
    //getNavigationManager().navigateTo(LoginSignUpFragment.newInstance(false));
    Fragment home =
        HomeFragment.newInstance(V8Engine.getConfiguration().getDefaultStore(), StoreContext.home,
            V8Engine.getConfiguration().getDefaultTheme());
    getNavigationManager().navigateTo(home);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    AptoideAccountManager accountManager =
        ((V8Engine) getActivity().getApplicationContext()).getAccountManager();

    final GoogleSignInOptions options =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
            .requestScopes(new Scope(Scopes.PROFILE))
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();

    client = getClientBuilder().addApi(GOOGLE_SIGN_IN_API, options).build();
    usernameTextView.setText(accountManager.getUserEmail());

    setupToolbar(view);

    attachPresenter(new MyAccountPresenter(this, accountManager, client,
        getActivity().getSupportFragmentManager()), savedInstanceState);
  }

  private void setupToolbar(View view) {
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
    toolbar.setTitle(getString(R.string.my_account));
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override protected void connect() {
    client.connect();
  }
}
