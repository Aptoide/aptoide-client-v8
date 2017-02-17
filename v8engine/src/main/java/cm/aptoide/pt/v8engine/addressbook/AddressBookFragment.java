package cm.aptoide.pt.v8engine.addressbook;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepositoryImpl;
import cm.aptoide.pt.v8engine.addressbook.data.SyncAddressBookRequest;
import cm.aptoide.pt.v8engine.fragment.SupportV4BaseFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;

/**
 * Created by jdandrade on 07/02/2017.
 */

public class AddressBookFragment extends SupportV4BaseFragment implements AddressBookContract.View {

  private AddressBookContract.UserActionsListener mActionsListener;
  private Button addressBookSyncButton;
  private Button facebookSyncButton;
  private Button twitterSyncButton;
  private TextView dismissV;
  private TextView about;

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
    mActionsListener = new AddressBookPresenter(this,
        new ContactsRepositoryImpl(new SyncAddressBookRequest(null, null)));
  }

  @Override public void setupViews() {
    mActionsListener.getButtonsState();
    dismissV.setPaintFlags(dismissV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    about.setPaintFlags(about.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    RxView.clicks(addressBookSyncButton).subscribe(click -> mActionsListener.syncAddressBook());
    RxView.clicks(facebookSyncButton).subscribe(click -> mActionsListener.syncFacebook());
    RxView.clicks(twitterSyncButton).subscribe(click -> mActionsListener.syncTwitter());
    RxView.clicks(dismissV).subscribe(click -> mActionsListener.finishViewClick());
    RxView.clicks(about).subscribe(click -> mActionsListener.aboutClick());
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
            getString(R.string.addressbook_data_about, marketName, marketName, marketName,
                marketName), "default"));
  }

  @Override public void showSuccessFragment(List<Contact> contacts) {
    ((FragmentShower) getContext()).pushFragmentV4(
        V8Engine.getFragmentProvider().newSyncSuccessFragment(contacts));
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
  }
}
