/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import cm.aptoide.pt.v8engine.notification.view.InboxAdapter;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 5/2/16.
 */
public class MyAccountFragment extends BaseToolbarFragment implements MyAccountView {

  private static final float STROKE_SIZE = 0.04f;
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
  private String userAvatarUrl = null;
  private RelativeLayout header;
  private TextView headerText;
  private Button moreNotificationsButton;
  private View userLayout;

  private PublishSubject<AptoideNotification> notificationSubject;
  private InboxAdapter adapter;
  private RecyclerView list;
  private Converter.Factory converterFactory;
  private OkHttpClient httpClient;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private CrashReport crashReport;

  public static Fragment newInstance() {
    return new MyAccountFragment();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    logoutButton = null;
    usernameTextView = null;
    storeNameTextView = null;
    userProfileEditButton = null;
    userStoreEditButton = null;
    storeLayout = null;
    userAvatar = null;
    storeAvatar = null;
    separator = null;
    header = null;
    headerText = null;
    moreNotificationsButton = null;
    userLayout = null;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    accountManager = ((V8Engine) getActivity().getApplicationContext()).getAccountManager();
    notificationSubject = PublishSubject.create();
    adapter = new InboxAdapter(Collections.emptyList(), notificationSubject);
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    crashReport = CrashReport.getInstance();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    list = (RecyclerView) view.findViewById(R.id.fragment_my_account_notification_list);
    list.setAdapter(adapter);
    list.setLayoutManager(new LinearLayoutManager(getContext()));

    logoutButton = (Button) view.findViewById(R.id.button_logout);
    logoutButton.setAllCaps(true);

    usernameTextView = (TextView) view.findViewById(R.id.my_account_username);
    storeNameTextView = (TextView) view.findViewById(R.id.my_account_store_name);
    userLayout = view.findViewById(R.id.my_account_user);
    userProfileEditButton = (Button) view.findViewById(R.id.my_account_edit_user_profile);
    userStoreEditButton = (Button) view.findViewById(R.id.my_account_edit_user_store);
    storeLayout = (RelativeLayout) view.findViewById(R.id.my_account_store);
    userAvatar = (ImageView) view.findViewById(R.id.my_account_user_avatar);
    storeAvatar = (ImageView) view.findViewById(R.id.my_account_store_avatar);
    separator = view.findViewById(R.id.my_account_separator);
    header = (RelativeLayout) view.findViewById(R.id.my_account_notifications_header);
    headerText = (TextView) view.findViewById(R.id.my_account_notifications_header)
        .findViewById(R.id.title);
    headerText.setText(getString(R.string.myaccount_header_title));

    moreNotificationsButton = (Button) view.findViewById(R.id.my_account_notifications_header)
        .findViewById(R.id.more);

    attachPresenter(new MyAccountPresenter(this, accountManager, crashReport,
            new MyAccountNavigator(getFragmentNavigator()),
            ((V8Engine) getContext().getApplicationContext()).getNotificationCenter(),
            new LinksHandlerFactory(getContext()),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences()),
        savedInstanceState);
  }

  @Override public int getContentViewId() {
    return R.layout.my_account_activity;
  }

  @Override public void showAccount(Account account) {

    if (!TextUtils.isEmpty(account.getNickname())) {
      usernameTextView.setText(account.getNickname());
    } else {
      usernameTextView.setText(account.getEmail());
    }

    setUserAvatar(account);

    setOrHideUserStore(account.getStore()
        .getName(), account.getStore()
        .getAvatar());
  }

  @Override public Observable<Void> signOutClick() {
    return RxView.clicks(logoutButton);
  }

  @Override public Observable<Void> moreNotificationsClick() {
    return RxView.clicks(moreNotificationsButton);
  }

  @Override public Observable<Void> storeClick() {
    return RxView.clicks(storeLayout);
  }

  @Override public Observable<Void> userClick() {
    return RxView.clicks(userLayout);
  }

  @Override public Observable<AptoideNotification> notificationSelection() {
    return notificationSubject;
  }

  @Override public void showNotifications(List<AptoideNotification> notifications) {
    adapter.updateNotifications(notifications);
  }

  @Override public Observable<Void> editStoreClick() {
    return RxView.clicks(userStoreEditButton);
  }

  @Override public Observable<GetStore> getStore() {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> GetStoreRequest.of(new BaseRequestWithStore.StoreCredentials(
                account.getStore()
                    .getName(), null, null), StoreContext.meta, bodyInterceptor, httpClient,
            converterFactory,
            ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
            .observe());
  }

  @Override public Observable<Void> editUserProfileClick() {
    return RxView.clicks(userProfileEditButton);
  }

  @Override public void navigateToHome() {
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }

  @Override public void showHeader() {
    header.setVisibility(View.VISIBLE);
  }

  @Override public void hideHeader() {
    header.setVisibility(View.INVISIBLE);
  }

  @Override public void refreshUI(Store store) {
    storeNameTextView.setText(store.
        getName());
    setOrHideUserStore(store.getName(), store.getAvatar());
  }

  private void setOrHideUserStore(String storeName, String storeAvatar) {
    if (!TextUtils.isEmpty(storeName)) {
      storeNameTextView.setText(storeName);
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransformWithPlaceholder(storeAvatar, this.storeAvatar, STROKE_SIZE,
              R.drawable.my_account_placeholder);
    } else {
      separator.setVisibility(View.GONE);
      storeLayout.setVisibility(View.GONE);
    }
  }

  private void setUserAvatar(Account account) {
    if (!TextUtils.isEmpty(account.getAvatar())) {
      userAvatarUrl = account.getAvatar();
      userAvatarUrl = userAvatarUrl.replace("50", "150");
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, userAvatar, STROKE_SIZE,
              R.drawable.my_account_placeholder);
    }
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(getString(R.string.my_account_title_my_account));
  }
}
