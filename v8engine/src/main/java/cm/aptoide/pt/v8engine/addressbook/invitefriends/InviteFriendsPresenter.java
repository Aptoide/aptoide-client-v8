package cm.aptoide.pt.v8engine.addressbook.invitefriends;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.navigation.AddressBookNavigation;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class InviteFriendsPresenter implements InviteFriendsContract.UserActionsListener {

  private final InviteFriendsContract.View inviteFriendsView;
  private final AddressBookNavigation addressBookNavigationManager;

  public InviteFriendsPresenter(InviteFriendsContract.View inviteFriendsView,
      AddressBookNavigation addressBookNavigationManager) {
    this.inviteFriendsView = inviteFriendsView;
    this.addressBookNavigationManager = addressBookNavigationManager;
  }

  @Override public void allowFindClicked() {
    this.inviteFriendsView.showPhoneInputFragment();
  }

  @Override public void doneClicked() {
    this.addressBookNavigationManager.exitAddressBook();
  }

  @Override public void shareClicked(Context context) {
    AptoideAccountManager accountManager =
        ((V8Engine) context.getApplicationContext()).getAccountManager();

    String shareText =
        context.getString(R.string.follow_my_store, V8Engine.getConfiguration().getMarketName())
            + " http://"
            + accountManager.getAccount().getStore()
            + ".store.aptoide.com";
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
        Application.getConfiguration().getMarketName());
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
    context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share)));
  }
}
