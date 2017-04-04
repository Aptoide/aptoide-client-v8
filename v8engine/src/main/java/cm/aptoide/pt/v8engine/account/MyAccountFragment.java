/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.account;

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
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.view.store.home.HomeFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import cm.aptoide.pt.v8engine.presenter.MyAccountPresenter;
import cm.aptoide.pt.v8engine.presenter.MyAccountView;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

/**
 * Created by trinkes on 5/2/16.
 */
public class MyAccountFragment extends FragmentView implements MyAccountView {

  private AptoideAccountManager accountManager;

  private Button logoutButton;
  private TextView usernameTextView;

  public static Fragment newInstance() {
    return new MyAccountFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    accountManager = ((V8Engine) getActivity().getApplicationContext()).getAccountManager();
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

  @Override public void navigateToHome() {
    final FragmentNavigator navManager = getFragmentNavigator();
    Fragment home =
        HomeFragment.newInstance(V8Engine.getConfiguration().getDefaultStore(), StoreContext.home,
            V8Engine.getConfiguration().getDefaultTheme());
    navManager.cleanBackStack();
    navManager.navigateToWithoutBackSave(home);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    usernameTextView.setText(accountManager.getAccountEmail());

    setupToolbar(view, getString(R.string.my_account));

    attachPresenter(new MyAccountPresenter(this, accountManager, CrashReport.getInstance()),
        savedInstanceState);
  }

  private Toolbar setupToolbar(View view, String title) {
    setHasOptionsMenu(true);
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.logo_toolbar);

    toolbar.setTitle(title);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    return toolbar;
  }
}
