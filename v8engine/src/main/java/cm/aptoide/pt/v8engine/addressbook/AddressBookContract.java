package cm.aptoide.pt.v8engine.addressbook;

import android.support.annotation.NonNull;
import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.addressbook.invitefriends.InviteFriendsFragment;
import java.util.List;

/**
 * Created by jdandrade on 10/02/2017.
 */
public interface AddressBookContract {
  interface View {

    void finishView();

    void changeAddressBookState(boolean checked);

    void changeTwitterState(boolean checked);

    void changeFacebookState(boolean checked);

    void showAboutFragment();

    void showSuccessFragment(List<Contact> contacts);

    void showInviteFriendsFragment(
        @NonNull InviteFriendsFragment.InviteFriendsFragmentOpenMode openMode);

    void setGenericPleaseWaitDialog(boolean showProgress);

    void showPhoneInputFragment();
  }

  interface UserActionsListener {

    void syncAddressBook();

    void syncTwitter(TwitterModel twitterModel);

    void syncFacebook(FacebookModel facebookModel);

    void getButtonsState();

    void finishViewClick();

    void aboutClick();

    void allowFindClick();
  }
}
