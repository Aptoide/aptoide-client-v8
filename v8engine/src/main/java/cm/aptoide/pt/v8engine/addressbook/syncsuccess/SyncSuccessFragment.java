package cm.aptoide.pt.v8engine.addressbook.syncsuccess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.fragment.SupportV4BaseFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class SyncSuccessFragment extends SupportV4BaseFragment implements SyncSuccessContract.View {

  public static final int NUMBER_OF_COLUMNS = 2;
  private SyncSuccessContract.UserActionsListener mActionsListener;
  ContactItemListener mItemListener = new ContactItemListener() {
    @Override public void onContactClick(Contact clickedContact) {
      mActionsListener.openFriend(clickedContact);
    }
  };
  private RecyclerView recyclerView;
  private SyncSuccessAdapter mListAdapter;
  private Button allowFind;
  private Button done;

  public static Fragment newInstance() {
    SyncSuccessFragment syncSuccessFragment = new SyncSuccessFragment();
    Bundle extras = new Bundle();
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
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActionsListener = new SyncSuccessPresenter(this);
    mListAdapter = new SyncSuccessAdapter(new ArrayList<>(0), mItemListener);
  }

  @Override public void setupViews() {
    recyclerView.setAdapter(mListAdapter);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS));
    RxView.clicks(allowFind).subscribe(click -> mActionsListener.allowFindClicked());
    RxView.clicks(done).subscribe(click -> mActionsListener.doneClicked());
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }

  @Override public void showStore() {

  }

  @Override public void showPhoneInputFragment() {

  }

  public interface ContactItemListener {

    void onContactClick(Contact clickedContact);
  }
}
