/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.MyAccountNavigator;
import cm.aptoide.pt.v8engine.presenter.MyAccountPresenter;
import cm.aptoide.pt.v8engine.presenter.MyAccountView;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

/**
 * Created by trinkes on 5/2/16.
 */
public class MyAccountFragment extends FragmentView implements MyAccountView {

  private AptoideAccountManager accountManager;

  private Button logoutButton;
  private TextView usernameTextView;
  private TextView storeNameTextView;
  private ImageView userAvatar;
  private ImageView storeAvatar;
  private Button userProfileEditButton;
  private Button userStoreEditButton;
  private View separator;
  private RelativeLayout storeLayout;

  private float strokeSize = 0.04f;
  private String userAvatarUrl = null;
  private Button moreNotificationsButton;

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
    bindViews(view);
    return view;
  }

  private void bindViews(View view) {
    logoutButton = (Button) view.findViewById(R.id.button_logout);
    usernameTextView = (TextView) view.findViewById(R.id.my_account_username);
    storeNameTextView = (TextView) view.findViewById(R.id.my_account_store_name);
    userProfileEditButton = (Button) view.findViewById(R.id.my_account_edit_user_profile);
    userStoreEditButton = (Button) view.findViewById(R.id.my_account_edit_user_store);
    storeLayout = (RelativeLayout) view.findViewById(R.id.my_account_store);
    userAvatar = (ImageView) view.findViewById(R.id.my_account_user_avatar);
    storeAvatar = (ImageView) view.findViewById(R.id.my_account_store_avatar);
    separator = (View) view.findViewById(R.id.my_account_separator);
    moreNotificationsButton = (Button) view.findViewById(R.id.my_account_more_notifications_button);
  }

  public int getLayoutId() {
    return R.layout.my_account_activity;
  }

  public Observable<Void> signOutClick() {
    return RxView.clicks(logoutButton);
  }

  @Override public Observable<Void> moreNotificationsClick() {
    return RxView.clicks(moreNotificationsButton);
  }

  @Override public void navigateToHome() {
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setupAccountLayout();

    setupToolbar(view, getString(R.string.my_account));

    attachPresenter(new MyAccountPresenter(this, accountManager, CrashReport.getInstance(),
            new MyAccountNavigator(getFragmentNavigator())),
        savedInstanceState);
  }

  private void setupAccountLayout() {

    if (!TextUtils.isEmpty(accountManager.getAccount()
        .getNickname())) {
      usernameTextView.setText(accountManager.getAccount()
          .getNickname());
    } else {
      usernameTextView.setText(accountManager.getAccountEmail());
    }

    if (!TextUtils.isEmpty(accountManager.getAccount()
        .getAvatar())) {
      userAvatarUrl = accountManager.getAccount()
          .getAvatar();
      userAvatarUrl = userAvatarUrl.replace("50", "150");
    }
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, userAvatar,
            R.drawable.user_account_white, strokeSize);

    if (!TextUtils.isEmpty(accountManager.getAccount()
        .getStoreName())) {
      storeNameTextView.setText(accountManager.getAccount()
          .getStoreName());
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransform(accountManager.getAccount()
              .getStoreAvatar(), storeAvatar);
    } else {
      separator.setVisibility(View.GONE);
      storeLayout.setVisibility(View.GONE);
    }
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
