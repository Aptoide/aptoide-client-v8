package cm.aptoide.pt.v8engine.presenter;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.AddressBookAnalytics;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class InviteFriendsPresenter implements InviteFriendsContract.UserActionsListener {

  private final InviteFriendsContract.View inviteFriendsView;
  private final AddressBookNavigation addressBookNavigationManager;
  private final String screen;
  private final AddressBookAnalytics analytics;

  public InviteFriendsPresenter(InviteFriendsContract.View inviteFriendsView,
      AddressBookNavigation addressBookNavigationManager,
      InviteFriendsContract.View.OpenMode openMode, AddressBookAnalytics analytics) {
    this.inviteFriendsView = inviteFriendsView;
    this.addressBookNavigationManager = addressBookNavigationManager;
    this.screen = getScreen(openMode);
    this.analytics = analytics;
  }

  @Override public void allowFindClicked() {
    addressBookNavigationManager.navigateToPhoneInputView();
  }

  @Override public void doneClicked() {
    analytics.sendNewConnectionsDoneEvent(screen);
    this.addressBookNavigationManager.leaveAddressBook();
  }

  @Override public void shareClicked(Context context) {
    analytics.sendNewConnectionsShareEvent(screen);
    AptoideAccountManager accountManager =
        ((V8Engine) context.getApplicationContext()).getAccountManager();

    String url = context.getString(R.string.store_url, accountManager.getAccount()
        .getStoreName());
    String shareText = context.getString(R.string.follow_my_store, V8Engine.getConfiguration()
        .getMarketName(), url);

    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Application.getConfiguration()
        .getMarketName());
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
    context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share)));
  }

  private String getScreen(InviteFriendsContract.View.OpenMode openMode) {
    switch (openMode) {
      case CONTACTS_PERMISSION_DENIAL:
        return "Not Able to Connect";
      case NO_FRIENDS:
        return "No New Connections";
      case ERROR:
        return "Error Getting Connections";
      default:
        throw new IllegalStateException("Mode not supported");
    }
  }
}