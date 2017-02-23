package cm.aptoide.pt.v8engine.addressbook.invitefriends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.SupportV4BaseFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class InviteFriendsFragment extends SupportV4BaseFragment
    implements InviteFriendsContract.View {
  private InviteFriendsContract.UserActionsListener mActionsListener;
  private Button share;
  private Button allowFind;
  private Button done;

  public static Fragment newInstance() {
    InviteFriendsFragment inviteFriendsFragment = new InviteFriendsFragment();
    Bundle extras = new Bundle();
    inviteFriendsFragment.setArguments(extras);
    return inviteFriendsFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActionsListener = new InviteFriendsPresenter(this);
  }

  @Override public void setupViews() {
    RxView.clicks(allowFind).subscribe(click -> mActionsListener.allowFindClicked());
    RxView.clicks(done).subscribe(click -> mActionsListener.doneClicked());
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_invitefriends;
  }

  @Override public void bindViews(@Nullable View view) {
    share = (Button) view.findViewById(R.id.addressbook_share_social);
    allowFind = (Button) view.findViewById(R.id.addressbook_allow_find);
    done = (Button) view.findViewById(R.id.addressbook_done);
  }

  @Override public void showPhoneInputFragment() {
    ((FragmentShower) getContext()).pushFragmentV4(
        V8Engine.getFragmentProvider().newPhoneInputFragment());
  }

  @Override public void finishView() {
    getActivity().onBackPressed();
  }
}
