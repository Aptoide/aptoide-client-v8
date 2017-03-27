package cm.aptoide.pt.v8engine.addressbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepositoryImpl;
import cm.aptoide.pt.v8engine.addressbook.navigation.AddressBookNavigationManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.UIComponentFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
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
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 07/02/2017.
 */

public class AddressBookFragment extends UIComponentFragment implements AddressBookContract.View {

  public static final int TWITTER_REQUEST_CODE = 140;
  public static final int FACEBOOK_REQUEST_CODE = 64206;
  TwitterAuthClient mTwitterAuthClient;
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

  public static AddressBookFragment newInstance() {
    AddressBookFragment addressBookFragment = new AddressBookFragment();
    Bundle extras = new Bundle();
    addressBookFragment.setArguments(extras);
    return addressBookFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    analytics = new AddressBookAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
    final BodyInterceptor<BaseBody> baseBodyBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();
    mActionsListener =
        new AddressBookPresenter(this, new ContactsRepositoryImpl(baseBodyBodyInterceptor),
            analytics,
            new AddressBookNavigationManager(NavigationManagerV4.Builder.buildWith(getActivity()),
                getTag(), getString(R.string.addressbook_about),
                getString(R.string.addressbook_data_about,
                    Application.getConfiguration().getMarketName())));
    callbackManager = CallbackManager.Factory.create();
    registerFacebookCallback();
    mGenericPleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
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
    addressbook_2nd_msg.setText(
        getString(R.string.addressbook_2nd_msg, V8Engine.getConfiguration().getMarketName()));
    mActionsListener.getButtonsState();
    //dismissV.setPaintFlags(dismissV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    about.setPaintFlags(about.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    RxView.clicks(addressBookSyncButton).flatMap(click -> {
      analytics.sendSyncAddressBookEvent();
      PermissionManager permissionManager = new PermissionManager();
      final PermissionService permissionService = (PermissionService) getContext();
      return permissionManager.requestContactsAccess(permissionService);
    }).observeOn(AndroidSchedulers.mainThread()).subscribe(permissionGranted -> {
      if (permissionGranted) {
        analytics.sendAllowAptoideAccessToContactsEvent();
        mActionsListener.syncAddressBook();
      } else {
        analytics.sendDenyAptoideAccessToContactsEvent();
        mActionsListener.contactsPermissionDenied();
      }
    });
    RxView.clicks(facebookSyncButton).subscribe(click -> facebookLoginCallback());
    RxView.clicks(twitterSyncButton).subscribe(click -> twitterLogin());
    RxView.clicks(dismissV).subscribe(click -> mActionsListener.finishViewClick());
    RxView.clicks(about).subscribe(click -> mActionsListener.aboutClick());
    RxView.clicks(allowFriendsFindButton).subscribe(click -> mActionsListener.allowFindClick());
  }

  private void facebookLoginCallback() {
    LoginManager.getInstance()
        .logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
  }

  private void twitterLogin() {
    mTwitterAuthClient = new TwitterAuthClient();
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
            Logger.e(this.getClass().getName(), error.getMessage());
          }
        });
  }

  private FacebookModel createFacebookModel(LoginResult loginResult) {
    FacebookModel facebookModel = new FacebookModel();
    facebookModel.setId(Long.valueOf(loginResult.getAccessToken().getUserId()));
    facebookModel.setAccessToken(loginResult.getAccessToken().getToken());
    return facebookModel;
  }

  @Override public void onResume() {
    super.onResume();
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

  private void changeSyncState(boolean checked, Button button) {
    if (checked) {
      button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
    } else {
      button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.reload, 0);
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