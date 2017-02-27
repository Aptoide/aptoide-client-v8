package cm.aptoide.pt.v8engine.addressbook;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepositoryImpl;
import cm.aptoide.pt.v8engine.fragment.SupportV4BaseFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
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

public class AddressBookFragment extends SupportV4BaseFragment implements AddressBookContract.View {

  public static final int TWITTER_REQUEST_CODE = 140;
  public static final int FACEBOOK_REQUEST_CODE = 64206;
  TwitterAuthClient mTwitterAuthClient;
  private AddressBookContract.UserActionsListener mActionsListener;
  private Button addressBookSyncButton;
  private Button facebookSyncButton;
  private Button twitterSyncButton;
  private TextView dismissV;
  private TextView addressbook_2nd_msg;
  private TextView about;
  private ProgressBar addressBookSyncProgress;
  private CallbackManager callbackManager;

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
    mActionsListener = new AddressBookPresenter(this, new ContactsRepositoryImpl());
    callbackManager = CallbackManager.Factory.create();
    registerFacebookCallback();
  }

  @Override public void setupViews() {
    addressbook_2nd_msg.setText(
        getString(R.string.addressbook_2nd_msg, V8Engine.getConfiguration().getMarketName()));
    mActionsListener.getButtonsState();
    dismissV.setPaintFlags(dismissV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    about.setPaintFlags(about.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    addressBookSyncProgress.setIndeterminate(true);
    RxView.clicks(addressBookSyncButton).subscribe(click -> {
      if (!ManagerPreferences.getAddressBookSyncState()) {
        PermissionManager permissionManager = new PermissionManager();
        final PermissionRequest permissionRequest = (PermissionRequest) getContext();
        permissionManager.requestContactsAccess(permissionRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(success -> {
              mActionsListener.syncAddressBook();
            }, throwable -> CrashReport.getInstance().log(throwable));
      }
    });
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
    changeSyncState(checked, addressBookSyncButton);
  }

  @Override public void changeTwitterState(boolean checked) {
    changeSyncState(checked, twitterSyncButton);
  }

  @Override public void changeFacebookState(boolean checked) {
    changeSyncState(checked, facebookSyncButton);
  }

  @Override public void showAboutFragment() {
    final String marketName = Application.getConfiguration().getMarketName();
    ((FragmentShower) getContext()).pushFragmentV4(V8Engine.getFragmentProvider()
        .newDescriptionFragment("About Address Book",
            getString(R.string.addressbook_data_about, marketName), "default"));
  }

  @Override public void showSuccessFragment(List<Contact> contacts) {
    ((FragmentShower) getContext()).pushFragmentV4(
        V8Engine.getFragmentProvider().newSyncSuccessFragment(contacts));
  }

  @Override public void showInviteFriendsFragment() {
    ((FragmentShower) getContext()).pushFragmentV4(
        V8Engine.getFragmentProvider().newInviteFriendsFragment());
  }

  @Override public void setAddressBookProgressIndicator(boolean showProgress) {
    if (showProgress) {
      addressBookSyncProgress.setVisibility(View.VISIBLE);
    } else {
      addressBookSyncButton.setVisibility(View.INVISIBLE);
    }
  }

  private void changeSyncState(boolean checked, Button button) {
    if (checked) {
      button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
    } else {
      button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_addressbook;
  }

  @Override public void bindViews(@Nullable View view) {
    addressBookSyncButton = (Button) view.findViewById(R.id.addressbook_sync_button);
    facebookSyncButton = (Button) view.findViewById(R.id.facebook_sync_button);
    twitterSyncButton = (Button) view.findViewById(R.id.twitter_sync_button);
    dismissV = (TextView) view.findViewById(R.id.addressbook_not_now);
    about = (TextView) view.findViewById(R.id.addressbook_about);
    addressBookSyncProgress =
        (ProgressBar) view.findViewById(R.id.addressbook_addressbook_sync_progress);
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