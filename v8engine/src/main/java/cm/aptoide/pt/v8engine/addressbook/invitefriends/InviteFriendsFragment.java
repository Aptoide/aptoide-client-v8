package cm.aptoide.pt.v8engine.addressbook.invitefriends;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.v8engine.addressbook.navigation.AddressBookNavigationManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.UIComponentFragment;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class InviteFriendsFragment extends UIComponentFragment
    implements InviteFriendsContract.View {
  public static final String OPEN_MODE = "OPEN_MODE";
  public static final String TAG = "TAG";
  private InviteFriendsContract.UserActionsListener mActionsListener;
  private OpenMode openMode;
  private String entranceTag;

  private Button share;
  private Button allowFind;
  private Button done;
  private TextView message;

  public static Fragment newInstance(OpenMode openMode, String tag) {
    InviteFriendsFragment inviteFriendsFragment = new InviteFriendsFragment();
    Bundle extras = new Bundle();
    extras.putSerializable(OPEN_MODE, openMode);
    extras.putString(TAG, tag);
    inviteFriendsFragment.setArguments(extras);
    return inviteFriendsFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActionsListener = new InviteFriendsPresenter(this,
        new AddressBookNavigationManager(getFragmentNavigator(), entranceTag,
            getString(R.string.addressbook_about), getString(R.string.addressbook_data_about,
            Application.getConfiguration().getMarketName())), openMode,
        new AddressBookAnalytics(Analytics.getInstance(),
            AppEventsLogger.newLogger(getContext().getApplicationContext())));
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    openMode = (OpenMode) args.get(OPEN_MODE);
    entranceTag = (String) args.get(TAG);
  }

  @Override public void setupViews() {
    RxView.clicks(allowFind).subscribe(click -> mActionsListener.allowFindClicked());
    RxView.clicks(done).subscribe(click -> mActionsListener.doneClicked());
    RxView.clicks(share).subscribe(click -> mActionsListener.shareClicked(getContext()));
    setupMessage(openMode);
  }

  public void setupMessage(@NonNull OpenMode openMode) {
    switch (openMode) {
      case ERROR:
        message.setText(getString(R.string.addressbook_insuccess_connection));
        break;
      case NO_FRIENDS:
        message.setText(getString(R.string.we_didn_t_find_any_contacts_that_are_using_aptoide,
            Application.getConfiguration().getMarketName()));
        break;
      case CONTACTS_PERMISSION_DENIAL:
        message.setText(R.string.addressbook_we_werent_able_to_connect_you);
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
}
