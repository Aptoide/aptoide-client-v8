package cm.aptoide.pt.v8engine.presenter;

import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.presenter.InviteFriendsContract;
import java.util.List;

/**
 * Created by jdandrade on 02/03/2017.
 */

public interface AddressBookNavigation {

  void leaveAddressBook();

  void navigateToPhoneInputView();

  void navigateToInviteFriendsView(@NonNull InviteFriendsContract.View.OpenMode openMode);

  void showAboutFragment();

  void showSuccessFragment(List<Contact> contacts);

  void navigateToThankYouConnectingFragment();
}
