package cm.aptoide.pt.v8engine.view.addressbook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 01/03/2017.
 */
public class ThankYouConnectingFragment extends UIComponentFragment {
  public static final String TAG = "TAG";
  private Button done;
  private String entranceTag;

  public static ThankYouConnectingFragment newInstance(String tag) {
    ThankYouConnectingFragment thankYouConnectingFragment = new ThankYouConnectingFragment();
    Bundle extras = new Bundle();
    extras.putString(TAG, tag);
    thankYouConnectingFragment.setArguments(extras);
    return thankYouConnectingFragment;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_thankyouconnecting;
  }

  @Override public void bindViews(@Nullable View view) {
    done = (Button) view.findViewById(R.id.addressbook_done);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    entranceTag = (String) args.get(TAG);
  }

  @Override public void setupViews() {
    AddressBookNavigationManager addressBookNavigationManager =
        new AddressBookNavigationManager(getFragmentNavigator(), entranceTag,
            getString(R.string.addressbook_about), getString(R.string.addressbook_data_about));
    RxView.clicks(done)
        .subscribe(clicks -> addressBookNavigationManager.leaveAddressBook(),
            throwable -> throwable.printStackTrace());
  }
}

