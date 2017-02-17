package cm.aptoide.pt.v8engine.addressbook.syncsuccess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.fragment.SupportV4BaseFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class SyncSuccessFragment extends SupportV4BaseFragment implements SyncSuccessContract.View {

  public static final int SYNCED_LIST_NUMBER_OF_COLUMNS = 2;
  public static final String CONTACTS_JSON = "CONTACTS_JSON";
  private SyncSuccessContract.UserActionsListener mActionsListener;
  ContactItemListener mItemListener = new ContactItemListener() {
    @Override public void onContactClick(Contact clickedContact) {
      mActionsListener.openFriend(clickedContact);
    }
  };
  private List<Contact> contacts;
  private RecyclerView recyclerView;
  private SyncSuccessAdapter mListAdapter;
  private Button allowFind;
  private Button done;
  private TextView successMessage;

  public static Fragment newInstance(List<Contact> contacts) {
    SyncSuccessFragment syncSuccessFragment = new SyncSuccessFragment();
    Gson gson = new Gson();
    String contactsJson = gson.toJson(contacts);
    Bundle extras = new Bundle();
    extras.putString(CONTACTS_JSON, contactsJson);
    syncSuccessFragment.setArguments(extras);
    return syncSuccessFragment;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_sync_success;
  }

  @Override public void bindViews(@Nullable View view) {
    recyclerView = (RecyclerView) view.findViewById(R.id.addressbook_contacts_list);
    allowFind = (Button) view.findViewById(R.id.addressbook_allow_find);
    done = (Button) view.findViewById(R.id.addressbook_done);
    successMessage = (TextView) view.findViewById(R.id.addressbook_successful_message);
  }

  @Override public void onResume() {
    super.onResume();
    this.mActionsListener.loadFriends();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActionsListener = new SyncSuccessPresenter(this);
    mListAdapter = new SyncSuccessAdapter((ArrayList<Contact>) contacts, mItemListener);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    final String contactsJson = (String) args.get(CONTACTS_JSON);
    Gson gson = new Gson();
    contacts = gson.fromJson(contactsJson, new TypeToken<ArrayList<Contact>>() {
    }.getType());
  }

  @Override public void setupViews() {
    recyclerView.setAdapter(mListAdapter);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(
        new GridLayoutManager(getContext(), SYNCED_LIST_NUMBER_OF_COLUMNS));

    successMessage.setText(getString(R.string.addressbook_success_connected_friends, "X",
        Application.getConfiguration().getMarketName()));

    RxView.clicks(allowFind).subscribe(click -> mActionsListener.allowFindClicked());
    RxView.clicks(done).subscribe(click -> mActionsListener.doneClicked());
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }

  @Override public void showStore() {

  }

  @Override public void showPhoneInputFragment() {
    ((FragmentShower) getContext()).pushFragmentV4(
        V8Engine.getFragmentProvider().newPhoneInputFragment());
  }

  @Override public void setProgressIndicator(boolean active) {
    if (getView() == null) {
      return;
    }
    // TODO: 14/02/2017 manipulate loader
  }

  public interface ContactItemListener {

    void onContactClick(Contact clickedContact);
  }
}
