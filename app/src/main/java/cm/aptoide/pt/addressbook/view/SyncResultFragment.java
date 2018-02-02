package cm.aptoide.pt.addressbook.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.addressbook.data.Contact;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.presenter.SyncResultContract;
import cm.aptoide.pt.presenter.SyncResultPresenter;
import cm.aptoide.pt.view.fragment.UIComponentFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class SyncResultFragment extends UIComponentFragment implements SyncResultContract.View {

  public static final int SYNCED_LIST_NUMBER_OF_COLUMNS = 2;
  public static final String CONTACTS_JSON = "CONTACTS_JSON";
  private static final String TAG = "TAG";
  @Inject AnalyticsManager analyticsManager;
  @Inject NavigationTracker navigationTracker;
  private SyncResultContract.UserActionsListener mActionsListener;
  private List<Contact> contacts;
  private RecyclerView recyclerView;
  private SyncResultAdapter mListAdapter;
  private Button allowFind;
  private Button done;
  private TextView successMessage;
  private String entranceTag;
  private String marketName;

  public static Fragment newInstance(List<Contact> contacts, String tag) {
    SyncResultFragment syncSuccessFragment = new SyncResultFragment();
    Gson gson = new Gson();
    String contactsJson = gson.toJson(contacts);
    Bundle extras = new Bundle();
    extras.putString(CONTACTS_JSON, contactsJson);
    extras.putString(TAG, tag);
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

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    marketName = application.getMarketName();
    mActionsListener = new SyncResultPresenter(this,
        new AddressBookAnalytics(analyticsManager, navigationTracker),
        new AddressBookNavigationManager(getFragmentNavigator(), entranceTag,
            getString(R.string.addressbook_about),
            getString(R.string.addressbook_data_about, marketName)));
    mListAdapter = new SyncResultAdapter((ArrayList<Contact>) contacts, getContext());
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

    successMessage.setText(
        getString(R.string.addressbook_success_connected_friends, Integer.toString(contacts.size()),
            marketName));

    RxView.clicks(allowFind)
        .subscribe(click -> mActionsListener.allowFindClicked());
    RxView.clicks(done)
        .subscribe(click -> mActionsListener.doneClicked());
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }

  @Override public void showStore() {

  }

  @Override public void setProgressIndicator(boolean active) {
    if (getView() == null) {
      return;
    }
    // TODO: 14/02/2017 manipulate loader
  }
}
