package cm.aptoide.pt.v8engine.addressbook.invitefriends;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class InviteFriendsFragment extends UIComponentFragment
    implements InviteFriendsContract.View {
  public static final String OPEN_MODE = "OPEN_MODE";
  private InviteFriendsContract.UserActionsListener mActionsListener;
  private InviteFriendsFragmentOpenMode openMode;

  private Button share;
  private Button allowFind;
  private Button done;
  private TextView message;

  public static Fragment newInstance(InviteFriendsFragmentOpenMode openMode) {
    InviteFriendsFragment inviteFriendsFragment = new InviteFriendsFragment();
    Bundle extras = new Bundle();
    extras.putSerializable(OPEN_MODE, openMode);
    inviteFriendsFragment.setArguments(extras);
    return inviteFriendsFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActionsListener = new InviteFriendsPresenter(this);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    openMode = (InviteFriendsFragmentOpenMode) args.get(OPEN_MODE);
  }

  @Override public void setupViews() {
    RxView.clicks(allowFind).subscribe(click -> mActionsListener.allowFindClicked());
    RxView.clicks(done).subscribe(click -> mActionsListener.doneClicked());
    RxView.clicks(share).subscribe(click -> mActionsListener.shareClicked(getContext()));
    setupMessage(openMode);
  }

  public void setupMessage(@NonNull InviteFriendsFragmentOpenMode openMode) {
    switch (openMode) {
      case ERROR:
        message.setText(getString(R.string.addressbook_insuccess_connection));
        break;
      case NO_FRIENDS:
        message.setText(getString(R.string.we_didn_t_find_any_contacts_that_are_using_aptoide));
        break;
      default:
        Logger.d(this.getClass().getSimpleName(), "Wrong openMode type.");
    }
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_invitefriends;
  }

  @Override public void bindViews(@Nullable View view) {
    share = (Button) view.findViewById(R.id.addressbook_share_social);
    allowFind = (Button) view.findViewById(R.id.addressbook_allow_find);
    done = (Button) view.findViewById(R.id.addressbook_done);
    message = (TextView) view.findViewById(R.id.addressbook_friends_message);
  }

  @Override public void showPhoneInputFragment() {
    getNavigationManager().navigateTo(V8Engine.getFragmentProvider().newPhoneInputFragment());
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }

  public enum InviteFriendsFragmentOpenMode {
    ERROR, NO_FRIENDS
  }
}
