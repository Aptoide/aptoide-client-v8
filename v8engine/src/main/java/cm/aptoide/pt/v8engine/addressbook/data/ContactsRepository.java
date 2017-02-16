package cm.aptoide.pt.v8engine.addressbook.data;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Created by jdandrade on 15/02/2017.
 */

public interface ContactsRepository {
  void getContacts(@NonNull LoadContactsCallback callback);

  interface LoadContactsCallback {

    void onContactsLoaded(List<Contact> contacts);
  }
}
