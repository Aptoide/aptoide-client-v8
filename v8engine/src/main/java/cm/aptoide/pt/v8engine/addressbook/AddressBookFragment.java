package cm.aptoide.pt.v8engine.addressbook;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.BaseLoaderToolbarFragment;

/**
 * Created by jdandrade on 07/02/2017.
 */

public class AddressBookFragment extends BaseLoaderToolbarFragment {

  public static AddressBookFragment newInstance() {
    AddressBookFragment addressBookFragment = new AddressBookFragment();
    Bundle extras = new Bundle();
    //extra arguments if needed
    addressBookFragment.setArguments(extras);
    return addressBookFragment;
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.addressbook_fragment_id;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {

  }

  @Override public int getContentViewId() {
    return R.layout.fragment_addressbook;
  }
}
