package cm.aptoide.pt.v8engine.addressbook.data;

import android.support.annotation.NonNull;
import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;
import java.util.List;

/**
 * Created by jdandrade on 15/02/2017.
 */

public interface ContactsRepository {
  void getContacts(@NonNull LoadContactsCallback callback);

  void getTwitterContacts(@NonNull TwitterModel twitterModel,
      @NonNull LoadContactsCallback callback);

  void getFacebookContacts(@NonNull FacebookModel facebookModel,
      @NonNull LoadContactsCallback callback);

  void submitPhoneNumber(@NonNull SubmitContactCallback callback, String userPhone);

  interface LoadContactsCallback {

    void onContactsLoaded(List<Contact> contacts);
  }

  interface SubmitContactCallback {

    void onPhoneNumberSubmission(boolean success);
  }
}
