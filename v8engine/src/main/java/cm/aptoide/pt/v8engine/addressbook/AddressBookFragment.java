package cm.aptoide.pt.v8engine.addressbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepositoryImpl;
import cm.aptoide.pt.v8engine.addressbook.invitefriends.InviteFriendsFragment;
import cm.aptoide.pt.v8engine.fragment.UIComponentFragment;
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
import java.util.List;
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
  private RelativeLayout facebookSyncButton;
  private RelativeLayout twitterSyncButton;
  private TextView dismissV;
  private TextView addressbook_2nd_msg;
  private TextView about;
  private CallbackManager callbackManager;
  private ProgressDialog mGenericPleaseWaitDialog;

  public AddressBookFragment() {

  }

  public static AddressBookFragment newInstance() {
    AddressBookFragment addressBookFragment = new AddressBookFragment();
    Bundle extras = new Bundle();
    addressBookFragment.setArguments(extras);
    return addressBookFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActionsListener = new AddressBookPresenter(this, new ContactsRepositoryImpl(
        ((V8Engine) getContext().getApplicationContext()).getAccountManager()));
    callbackManager = CallbackManager.Factory.create();
    registerFacebookCallback();
    mGenericPleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(getContext());
  }

  @Override public void setupViews() {
    addressbook_2nd_msg.setText(
        getString(R.string.addressbook_2nd_msg, V8Engine.getConfiguration().getMarketName()));
    mActionsListener.getButtonsState();
    dismissV.setPaintFlags(dismissV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    about.setPaintFlags(about.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    RxView.clicks(addressBookSyncButton)
        .flatMap(click -> {
          PermissionManager permissionManager = new PermissionManager();
          final PermissionService permissionService = (PermissionService) getContext();
          return permissionManager.requestContactsAccess(permissionService);
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(permissionGranted -> mActionsListener.syncAddressBook());
    RxView.clicks(facebookSyncButton).subscribe(click -> facebookLoginCallback());
    RxView.clicks(twitterSyncButton).subscribe(click -> twitterLogin());
    RxView.clicks(dismissV).subscribe(click -> mActionsListener.finishViewClick());
    RxView.clicks(about).subscribe(click -> mActionsListener.aboutClick());
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
        ShowMessage.asLongSnack(getActivity(), "Twitter authorization error");
      }
    });
  }

  private TwitterModel createTwitterModel(Result<TwitterSession> result) {
    TwitterModel twitterModel = new TwitterModel();
    TwitterSession twitterSession = result.data;
    TwitterAuthToken twitterAuthToken = twitterSession.getAuthToken();
    twitterModel.setId(twitterSession.getUserId());
    twitterModel.setToken(twitterAuthToken.token);
    twitterModel.setSecret(twitterAuthToken.secret);
    return twitterModel;
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
    //changeSyncState(checked, addressBookSyncButton);
  }

  @Override public void changeTwitterState(boolean checked) {
    //changeSyncState(checked, twitterSyncButton);
  }

  @Override public void changeFacebookState(boolean checked) {
    //changeSyncState(checked, facebookSyncButton);
  }

  @Override public void showAboutFragment() {
    final String marketName = Application.getConfiguration().getMarketName();
    getNavigationManager().navigateTo(V8Engine.getFragmentProvider()
        .newDescriptionFragment("About Address Book",
            getString(R.string.addressbook_data_about, marketName), "default"));
  }

  @Override public void showSuccessFragment(List<Contact> contacts) {
    getNavigationManager().navigateTo(
        V8Engine.getFragmentProvider().newSyncSuccessFragment(contacts));
  }

  @Override public void showInviteFriendsFragment(
      @NonNull InviteFriendsFragment.InviteFriendsFragmentOpenMode openMode) {
    switch (openMode) {
      case ERROR:
        getNavigationManager().navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(InviteFriendsFragment.InviteFriendsFragmentOpenMode.ERROR));
        break;
      case NO_FRIENDS:
        getNavigationManager().navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(
                InviteFriendsFragment.InviteFriendsFragmentOpenMode.NO_FRIENDS));
        break;
      default:
        Logger.d(this.getClass().getSimpleName(), "Wrong openMode type.");
    }
  }

  @Override public void setGenericPleaseWaitDialog(boolean showProgress) {
    if (showProgress) {
      mGenericPleaseWaitDialog.show();
    } else {
      mGenericPleaseWaitDialog.dismiss();
    }
  }
/*
  private void changeSyncState(boolean checked, Button button) {
    if (checked) {
      button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
    } else {
      button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
  }*/

  @Override public int getContentViewId() {
    return R.layout.fragment_addressbook;
  }

  @Override public void bindViews(@Nullable View view) {
    addressBookSyncButton = (Button) view.findViewById(R.id.addressbook_sync_button);
    facebookSyncButton = (RelativeLayout) view.findViewById(R.id.facebook_sync_button);
    twitterSyncButton = (RelativeLayout) view.findViewById(R.id.twitter_sync_button);
    dismissV = (TextView) view.findViewById(R.id.addressbook_not_now);
    about = (TextView) view.findViewById(R.id.addressbook_about);
    addressbook_2nd_msg = (TextView) view.findViewById(R.id.addressbook_2nd_msg);
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