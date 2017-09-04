package cm.aptoide.pt.presenter;

import android.support.annotation.NonNull;
import cm.aptoide.pt.addressbook.data.Contact;
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
