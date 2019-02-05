package cm.aptoide.pt.addressbook.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jdandrade on 01/03/2017.
 */
public class ThankYouConnectingFragment extends UIComponentFragment
    implements NotBottomNavigationView {
  public static final String TAG = "TAG";
  @Inject @Named("aptoide-theme") String theme;
  @Inject @Named("marketName") String marketName;
  private Button done;
  private String entranceTag;

  public static ThankYouConnectingFragment newInstance(String tag) {
    ThankYouConnectingFragment thankYouConnectingFragment = new ThankYouConnectingFragment();
    Bundle extras = new Bundle();
    extras.putString(TAG, tag);
    thankYouConnectingFragment.setArguments(extras);
    return thankYouConnectingFragment;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    entranceTag = (String) args.get(TAG);
  }

  @Override public void setupViews() {
    AddressBookNavigationManager addressBookNavigationManager =
        new AddressBookNavigationManager(getFragmentNavigator(), entranceTag,
            getString(R.string.addressbook_about),
            getString(R.string.addressbook_data_about, marketName),
            theme);
    RxView.clicks(done)
        .subscribe(clicks -> addressBookNavigationManager.leaveAddressBook(),
            throwable -> throwable.printStackTrace());
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_thankyouconnecting;
  }

  @Override public void bindViews(@Nullable View view) {
    done = (Button) view.findViewById(R.id.addressbook_done);
  }
}

