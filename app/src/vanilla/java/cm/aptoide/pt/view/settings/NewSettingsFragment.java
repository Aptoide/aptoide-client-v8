package cm.aptoide.pt.view.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import cm.aptoide.pt.account.AdultContentAnalytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.file.FileManager;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.notification.NotificationSyncScheduler;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.view.dialog.EditableTextDialog;
import cm.aptoide.pt.view.fragment.FragmentView;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by franciscocalado on 12/03/18.
 */

public class NewSettingsFragment extends FragmentView
    implements SharedPreferences.OnSharedPreferenceChangeListener, NewSettingsView {

  private static final float STROKE_SIZE = 0.04f;

  protected Toolbar toolbar;
  @Inject NavigationTracker navigationTracker;
  @Inject NewSettingsNavigator newSettingsNavigator;
  private SharedPreferences sharedPreferences;
  private UpdateRepository repository;
  private String defaultThemeName;
  private AdultContentAnalytics adultContentAnalytics;
  private Context context;
  private CompositeSubscription subscriptions;
  private FileManager fileManager;
  private AptoideAccountManager accountManager;

  private RxAlertDialog adultContentConfirmationDialog;
  private EditableTextDialog enableAdultContentPinDialog;
  private EditableTextDialog setPinDialog;
  private EditableTextDialog removePinDialog;

  private boolean trackAnalytics;
  private NotificationSyncScheduler notificationSyncScheduler;
  private String marketName;
  private Database database;
  private PublishSubject<Void> populateAccountSubject;

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
  private TextView myAccountTitle;
  private TextView myStoreName;
  private TextView myStoreTitle;
  private Button loginButton;
  private Button logoutButton;
  private Button findFriendsButton;
  private Button createStoreButton;
  private Button editStoreButton;
  private Button editProfileButton;

  //Settings views
  private View compatibleApps;
  private View downloadOverWifi;

  //TODO: Add string resources to the settings XML and fragment (ALL STRINGS HARDCODED!!!)

  public static Fragment newInstance() {
    return new NewSettingsFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);

    populateAccountSubject = PublishSubject.create();

    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    adultContentAnalytics = application.getAdultContentAnalytics();
    defaultThemeName = application.getDefaultThemeName();
    marketName = application.getMarketName();
    trackAnalytics = true;
    database = ((AptoideApplication) getContext().getApplicationContext()).getDatabase();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    fileManager = ((AptoideApplication) getContext().getApplicationContext()).getFileManager();
    subscriptions = new CompositeSubscription();

    bodyInterceptor = application.getAccountSettingsBodyInterceptorPoolV7();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();


    adultContentConfirmationDialog =
        new RxAlertDialog.Builder(getContext()).setMessage(R.string.are_you_adult)
            .setPositiveButton(R.string.yes)
            .setNegativeButton(R.string.no)
            .build();
    enableAdultContentPinDialog =
        new PinDialog.Builder(getContext()).setMessage(R.string.request_adult_pin)
            .setPositiveButton(R.string.all_button_ok)
            .setNegativeButton(R.string.cancel)
            .setView(R.layout.dialog_requestpin)
            .setEditText(R.id.pininput)
            .build();
    removePinDialog = new PinDialog.Builder(getContext()).setMessage(R.string.request_adult_pin)
        .setPositiveButton(R.string.all_button_ok)
        .setNegativeButton(R.string.cancel)
        .setView(R.layout.dialog_requestpin)
        .setEditText(R.id.pininput)
        .build();
    setPinDialog = new PinDialog.Builder(getContext()).setMessage(R.string.asksetadultpinmessage)
        .setPositiveButton(R.string.all_button_ok)
        .setNegativeButton(R.string.cancel)
        .setView(R.layout.dialog_requestpin)
        .setEditText(R.id.pininput)
        .build();

    notificationSyncScheduler =
        ((AptoideApplication) getContext().getApplicationContext()).getNotificationSyncScheduler();
    repository = RepositoryFactory.getUpdateRepository(getContext(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
    //navigationTracker.registerScreen(ScreenTagHistory.Builder.build(this.getClass()
    //    .getSimpleName()));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setAccountViews(view);

    AptoideApplication application = (AptoideApplication) getContext().getApplicationContext();
    attachPresenter(new NewSettingsPresenter(this, accountManager, CrashReport.getInstance(),
        ((ActivityResultNavigator) getContext()).getMyAccountNavigator(),
        application.getDefaultSharedPreferences(), application.getNavigationTracker(),
        AndroidSchedulers.mainThread(), newSettingsNavigator));
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.new_settings_layout, container, false);
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

  }

  @Override public void showAccount(Account account) {
    if (account.getNickname()
        .isEmpty() || account.getNickname()
        .equals(null) || account == null) {
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
    myStoreName.setText(store.
        getName());
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

    myStoreTitle = (TextView) myStoreView.findViewById(R.id.name);
    myStoreTitle.setText("My store");

    myAccountTitle = (TextView) myProfileView.findViewById(R.id.name);
    myAccountTitle.setText("My account");

    loginButton = (Button) view.findViewById(R.id.login_button);
    logoutButton = (Button) view.findViewById(R.id.logout_button);
    createStoreMessage = (TextView) view.findViewById(R.id.create_store_message);
    findFriendsButton = (Button) view.findViewById(R.id.find_friends_button);
    createStoreButton = (Button) view.findViewById(R.id.create_store_button);
    editStoreButton = (Button) myStoreView.findViewById(R.id.edit_button);
    editProfileButton = (Button) myProfileView.findViewById(R.id.edit_button);
  }

  private void setSettingsViews(View view) {
    downloadOverWifi = view.findViewById(R.id.download_wifi);
  }

}