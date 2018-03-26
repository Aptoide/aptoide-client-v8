package cm.aptoide.pt.addressbook.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.addressbook.data.ContactsRepository;
import cm.aptoide.pt.addressbook.utils.ContactUtils;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.FacebookModel;
import cm.aptoide.pt.dataprovider.model.v7.TwitterModel;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.AddressBookContract;
import cm.aptoide.pt.presenter.AddressBookPresenter;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.fragment.UIComponentFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.jakewharton.rxbinding.view.RxView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import java.util.Arrays;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;

public class AddressBookFragment extends UIComponentFragment
    implements AddressBookContract.View, NotBottomNavigationView {

  public static final int TWITTER_REQUEST_CODE = 140;
  public static final int FACEBOOK_REQUEST_CODE = 64206;
  @Inject TwitterAuthClient mTwitterAuthClient;
  @Inject AnalyticsManager analyticsManager;
  @Inject NavigationTracker navigationTracker;
  private AddressBookContract.UserActionsListener mActionsListener;
  private Button addressBookSyncButton;
  private Button allowFriendsFindButton;
  private Button facebookSyncButton;
  private Button twitterSyncButton;
  private Button dismissV;
  private TextView addressbook_2nd_msg;
  private TextView about;
  private ImageView checkOrReloadAddressBook;
  private ImageView checkOrReloadTwitter;
  private ImageView checkOrReloadFacebook;
  private CallbackManager callbackManager;
  private ProgressDialog mGenericPleaseWaitDialog;
  private TwitterSession twitterSession;
  private AddressBookAnalytics analytics;
  private String marketName;

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
    marketName = application.getMarketName();
    analytics = new AddressBookAnalytics(analyticsManager, navigationTracker);
    final BodyInterceptor<BaseBody> baseBodyBodyInterceptor =
        application.getAccountSettingsBodyInterceptorPoolV7();
    final OkHttpClient httpClient = application.getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    mActionsListener = new AddressBookPresenter(this,
        new ContactsRepository(baseBodyBodyInterceptor, httpClient, converterFactory,
            application.getIdsRepository(), new ContactUtils(
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE),
            getContext().getContentResolver()), application.getTokenInvalidator(),
            application.getDefaultSharedPreferences()), analytics,
        new AddressBookNavigationManager(getFragmentNavigator(), getTag(),
            getString(R.string.addressbook_about),
            getString(R.string.addressbook_data_about, marketName)),
        application.getDefaultSharedPreferences());
    callbackManager = CallbackManager.Factory.create();
    registerFacebookCallback();
    mGenericPleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
  }

  // This method is being overriden here because the views are binded in it and changeFacebookState
  // needs the views binded or the app will crash
  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    if (accessToken != null) {
      if (!accessToken.isExpired()) {
        changeFacebookState(true);
      }
    }
  }

  @Override public void setupViews() {
    addressbook_2nd_msg.setText(getString(R.string.addressbook_2nd_msg, marketName));
    mActionsListener.getButtonsState();
    //dismissV.setPaintFlags(dismissV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    about.setPaintFlags(about.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    RxView.clicks(addressBookSyncButton)
        .flatMap(click -> {
          analytics.sendSyncAddressBookEvent();
          PermissionManager permissionManager = new PermissionManager();
          final PermissionService permissionService = (PermissionService) getContext();
          return permissionManager.requestContactsAccess(permissionService);
        })
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(permissionGranted -> {
          if (permissionGranted) {
            analytics.sendAllowAptoideAccessToContactsEvent();
            mActionsListener.syncAddressBook();
          } else {
            analytics.sendDenyAptoideAccessToContactsEvent();
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
    LoginManager.getInstance()
        .logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
  }

  private void twitterLogin() {
    mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
      @Override public void success(Result<TwitterSession> result) {
        TwitterModel twitterModel = createTwitterModel(result);
        mActionsListener.syncTwitter(twitterModel);
      }

      @Override public void failure(TwitterException exception) {
        ShowMessage.asLongSnack(getActivity(), getString(R.string.address_book_twitter_error));
      }
    });
  }

  private TwitterModel createTwitterModel(Result<TwitterSession> result) {
    TwitterModel twitterModel = new TwitterModel();
    twitterSession = result.data;
    TwitterAuthToken twitterAuthToken = twitterSession.getAuthToken();
    twitterModel.setId(twitterSession.getUserId());
    twitterModel.setToken(twitterAuthToken.token);
    twitterModel.setSecret(twitterAuthToken.secret);
    return twitterModel;
  }

  private void changeSyncState(boolean checked, ImageView imageView) {
    if (checked) {
      imageView.setImageResource(R.drawable.check);
    } else {
      imageView.setImageResource(R.drawable.reload);
    }
  }

  private void registerFacebookCallback() {
    LoginManager.getInstance()
        .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override public void onSuccess(LoginResult loginResult) {
            FacebookModel facebookModel = createFacebookModel(loginResult);
            mActionsListener.syncFacebook(facebookModel);
          }

          @Override public void onCancel() {
          }

          @Override public void onError(FacebookException error) {
            Logger.e(this.getClass()
                .getName(), error.getMessage());
          }
        });
  }

  private FacebookModel createFacebookModel(LoginResult loginResult) {
    FacebookModel facebookModel = new FacebookModel();
    facebookModel.setId(Long.valueOf(loginResult.getAccessToken()
        .getUserId()));
    facebookModel.setAccessToken(loginResult.getAccessToken()
        .getToken());
    return facebookModel;
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
    if (showProgress) {
      mGenericPleaseWaitDialog.show();
    } else {
      mGenericPleaseWaitDialog.dismiss();
    }
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
    addressbook_2nd_msg = (TextView) view.findViewById(R.id.addressbook_2nd_msg);
    checkOrReloadFacebook = (ImageView) view.findViewById(R.id.check_or_reload_facebook);
    checkOrReloadTwitter = (ImageView) view.findViewById(R.id.check_or_reload_twitter);
    checkOrReloadAddressBook = (ImageView) view.findViewById(R.id.check_or_reload_addressbook);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == TWITTER_REQUEST_CODE) {
      mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    } else if (requestCode == FACEBOOK_REQUEST_CODE) {
      callbackManager.onActivityResult(requestCode, resultCode, data);
    }
  }
}
