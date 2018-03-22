package cm.aptoide.pt.view.settings;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.BackButtonFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by franciscocalado on 12/03/18.
 */

public class NewAccountFragment extends BackButtonFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener, NewAccountView {

  private static final float STROKE_SIZE = 0.04f;

  protected Toolbar toolbar;
  @Inject NewAccountNavigator newAccountNavigator;
  private AptoideAccountManager accountManager;

  private Converter.Factory converterFactory;
  private OkHttpClient httpClient;
  private BodyInterceptor<BaseBody> bodyInterceptor;

  //Account views
  private View myProfileView;
  private View myStoreView;
  private View loginView;
  private View accountView;
  private TextView createStoreMessage;
  private ImageView myAccountAvatar;
  private ImageView myStoreAvatar;
  private TextView myAccountName;
  private TextView myStoreName;
  private Button loginButton;
  private Button logoutButton;
  private Button findFriendsButton;
  private Button createStoreButton;
  private Button editStoreButton;
  private Button editProfileButton;

  //Navigation buttons
  private View notificationHistory;
  private View settings;

  public static Fragment newInstance() {
    return new NewAccountFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);

    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();

    bodyInterceptor = application.getAccountSettingsBodyInterceptorPoolV7();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    notificationHistory = view.findViewById(R.id.notifications_history);
    settings = view.findViewById(R.id.settings);

    setAccountViews(view);
    setupToolbar();

    AptoideApplication application = (AptoideApplication) getContext().getApplicationContext();
    attachPresenter(new NewAccountPresenter(this, accountManager, CrashReport.getInstance(),
        application.getDefaultSharedPreferences(), AndroidSchedulers.mainThread(),
        newAccountNavigator));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_my_account, container, false);
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

  }

  @Override public void showAccount(Account account) {
    if (TextUtils.isEmpty(account.getNickname())) {
      showLoginAccountDisplayable();
    } else if (account.getStore()
        .getName()
        .isEmpty()) {
      showAccountNoStoreDisplayable();
      setUserProfile(account);
    } else {
      showAccountAndStoreDisplayable();
      setUserProfile(account);
      setUserStore(account.getStore()
          .getName(), account.getStore()
          .getAvatar());
    }
  }

  @Override public Observable<Void> loginClick() {
    return RxView.clicks(loginButton);
  }

  @Override public Observable<Void> signOutClick() {
    return RxView.clicks(logoutButton);
  }

  @Override public Observable<Void> findFriendsClick() {
    return RxView.clicks(findFriendsButton);
  }

  @Override public Observable<Void> storeClick() {
    return RxView.clicks(myStoreView);
  }

  @Override public Observable<Void> userClick() {
    return RxView.clicks(myProfileView);
  }

  @Override public Observable<Void> editStoreClick() {
    return RxView.clicks(editStoreButton);
  }

  @Override public Observable<Void> editUserProfileClick() {
    return RxView.clicks(editProfileButton);
  }

  @Override public Observable<Void> settingsClicked() {
    return RxView.clicks(settings);
  }

  @Override public Observable<Void> notificationsClicked() {
    return RxView.clicks(notificationHistory);
  }

  @Override public Observable<GetStore> getStore() {
    return accountManager.accountStatus()
        .first()
        .flatMap(account -> GetStoreRequest.of(new BaseRequestWithStore.StoreCredentials(
                account.getStore()
                    .getName(), null, null), StoreContext.meta, bodyInterceptor, httpClient,
            converterFactory,
            ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
            .observe());
  }

  @Override public void refreshUI(Store store) {
    myStoreName.setText(store.getName());
    setUserStore(store.getName(), store.getAvatar());
  }

  @Override public void showLoginAccountDisplayable() {
    loginView.setVisibility(View.VISIBLE);
    accountView.setVisibility(View.GONE);
  }

  @Override public Observable<Void> createStoreClick() {
    return RxView.clicks(createStoreButton);
  }

  private void showAccountNoStoreDisplayable() {
    loginView.setVisibility(View.GONE);
    accountView.setVisibility(View.VISIBLE);

    myProfileView.setVisibility(View.VISIBLE);
    myStoreView.setVisibility(View.GONE);
    createStoreButton.setVisibility(View.VISIBLE);
    createStoreMessage.setVisibility(View.VISIBLE);
  }

  private void showAccountAndStoreDisplayable() {
    loginView.setVisibility(View.GONE);
    accountView.setVisibility(View.VISIBLE);

    myProfileView.setVisibility(View.VISIBLE);
    myStoreView.setVisibility(View.VISIBLE);
    createStoreButton.setVisibility(View.GONE);
    createStoreMessage.setVisibility(View.GONE);
  }

  private void setUserProfile(Account account) {
    if (!TextUtils.isEmpty(account.getNickname())) {
      myAccountName.setText(account.getNickname());
    } else {
      myAccountName.setText(account.getEmail());
    }
    if (!TextUtils.isEmpty(account.getAvatar())) {
      String userAvatarUrl = account.getAvatar();
      userAvatarUrl = userAvatarUrl.replace("50", "150");
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, myAccountAvatar, STROKE_SIZE,
              R.drawable.my_account_placeholder);
    }
  }

  private void setUserStore(String storeName, String storeAvatar) {
    if (!TextUtils.isEmpty(storeName)) {
      myStoreName.setText(storeName);
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransformWithPlaceholder(storeAvatar, this.myStoreAvatar,
              STROKE_SIZE, R.drawable.my_account_placeholder);
    }
  }

  private void setAccountViews(View view) {
    myProfileView = view.findViewById(R.id.my_profile);
    myStoreView = view.findViewById(R.id.my_store);
    accountView = view.findViewById(R.id.account_displayables);
    loginView = view.findViewById(R.id.login_register_container);

    myAccountAvatar = (ImageView) myProfileView.findViewById(R.id.user_icon);
    myAccountName = (TextView) myProfileView.findViewById(R.id.description);
    myStoreAvatar = (ImageView) myStoreView.findViewById(R.id.user_icon);
    myStoreName = (TextView) myStoreView.findViewById(R.id.description);

    TextView myStoreTitle = (TextView) myStoreView.findViewById(R.id.name);
    myStoreTitle.setText(R.string.newaccount_my_store);

    TextView myAccountTitle = (TextView) myProfileView.findViewById(R.id.name);
    myAccountTitle.setText(R.string.newaccount_my_profile);

    loginButton = (Button) view.findViewById(R.id.login_button);
    logoutButton = (Button) view.findViewById(R.id.logout_button);
    createStoreMessage = (TextView) view.findViewById(R.id.create_store_message);
    findFriendsButton = (Button) view.findViewById(R.id.find_friends_button);
    createStoreButton = (Button) view.findViewById(R.id.create_store_button);
    editStoreButton = (Button) myStoreView.findViewById(R.id.edit_button);
    editProfileButton = (Button) myProfileView.findViewById(R.id.edit_button);
  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.my_account_title_my_account);

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(toolbar.getTitle());
    }
    toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
  }
}