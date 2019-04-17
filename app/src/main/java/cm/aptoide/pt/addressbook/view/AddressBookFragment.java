package cm.aptoide.pt.addressbook.view;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.addressbook.data.ContactsRepository;
import cm.aptoide.pt.addressbook.utils.ContactUtils;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.presenter.AddressBookContract;
import cm.aptoide.pt.presenter.AddressBookPresenter;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.fragment.UIComponentFragment;
import com.facebook.AccessToken;
import com.jakewharton.rxbinding.view.RxView;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;

public class AddressBookFragment extends UIComponentFragment
    implements AddressBookContract.View, NotBottomNavigationView {

  private AddressBookContract.UserActionsListener mActionsListener;
  private Button addressBookSyncButton;
  private Button allowFriendsFindButton;
  private Button facebookSyncButton;
  private Button twitterSyncButton;
  private Button dismissV;
  private TextView about;
  private ImageView checkOrReloadAddressBook;
  private ImageView checkOrReloadTwitter;
  private ImageView checkOrReloadFacebook;

  public static AddressBookFragment newInstance() {
    AddressBookFragment addressBookFragment = new AddressBookFragment();
    Bundle extras = new Bundle();
    addressBookFragment.setArguments(extras);
    return addressBookFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    final BodyInterceptor<BaseBody> baseBodyBodyInterceptor =
        application.getAccountSettingsBodyInterceptorPoolV7();
    final OkHttpClient httpClient = application.getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    mActionsListener = new AddressBookPresenter(this,
        new ContactsRepository(baseBodyBodyInterceptor, httpClient, converterFactory,
            application.getIdsRepository(), new ContactUtils(
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE),
            getContext().getContentResolver()), application.getTokenInvalidator(),
            application.getDefaultSharedPreferences()), null,
        new AddressBookNavigationManager(getFragmentNavigator(), getTag(),
            getString(R.string.addressbook_about), "", ""),
        application.getDefaultSharedPreferences());
    registerFacebookCallback();
  }

  // This method is being overriden here because the views are binded in it and changeFacebookState
  // needs the views binded or the app will crash
  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    if (accessToken != null) {
      if (!accessToken.isExpired()) {
        changeFacebookState(true);
      }
    }
  }

  @Override public void setupViews() {
    mActionsListener.getButtonsState();
    about.setPaintFlags(about.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    RxView.clicks(addressBookSyncButton)
        .flatMap(click -> {
          PermissionManager permissionManager = new PermissionManager();
          final PermissionService permissionService = (PermissionService) getContext();
          return permissionManager.requestContactsAccess(permissionService);
        })
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(permissionGranted -> {
          if (permissionGranted) {
            mActionsListener.syncAddressBook();
          } else {
            mActionsListener.contactsPermissionDenied();
          }
        });

    RxView.clicks(facebookSyncButton)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> facebookLoginCallback());

    RxView.clicks(twitterSyncButton)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> twitterLogin());

    RxView.clicks(dismissV)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> mActionsListener.finishViewClick());

    RxView.clicks(about)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> mActionsListener.aboutClick());

    RxView.clicks(allowFriendsFindButton)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> mActionsListener.allowFindClick());
  }

  private void facebookLoginCallback() {

  }

  private void twitterLogin() {

  }

  private void changeSyncState(boolean checked, ImageView imageView) {
    if (checked) {
      imageView.setImageResource(R.drawable.check);
    } else {
      imageView.setImageResource(R.drawable.ic_refresh);
    }
  }

  private void registerFacebookCallback() {
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }

  @Override public void changeAddressBookState(boolean checked) {
    changeSyncState(checked, checkOrReloadAddressBook);
  }

  @Override public void changeTwitterState(boolean checked) {
    changeSyncState(checked, checkOrReloadTwitter);
  }

  @Override public void changeFacebookState(boolean checked) {
    changeSyncState(checked, checkOrReloadFacebook);
  }

  @Override public void setGenericPleaseWaitDialog(boolean showProgress) {
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_addressbook;
  }

  @Override public void bindViews(@Nullable View view) {
    addressBookSyncButton = (Button) view.findViewById(R.id.addressbook_text);
    facebookSyncButton = (Button) view.findViewById(R.id.facebook_text);
    twitterSyncButton = (Button) view.findViewById(R.id.twitter_text);
    allowFriendsFindButton = (Button) view.findViewById(R.id.addressbook_allow_find);
    dismissV = (Button) view.findViewById(R.id.addressbook_done);
    about = (TextView) view.findViewById(R.id.addressbook_about);
    checkOrReloadFacebook = (ImageView) view.findViewById(R.id.check_or_reload_facebook);
    checkOrReloadTwitter = (ImageView) view.findViewById(R.id.check_or_reload_twitter);
    checkOrReloadAddressBook = (ImageView) view.findViewById(R.id.check_or_reload_addressbook);
  }
}
