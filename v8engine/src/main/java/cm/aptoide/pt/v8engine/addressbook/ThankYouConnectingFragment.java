package cm.aptoide.pt.v8engine.addressbook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.UIComponentFragment;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 01/03/2017.
 */
public class ThankYouConnectingFragment extends UIComponentFragment {
  private Button done;

  public static ThankYouConnectingFragment newInstance() {
    ThankYouConnectingFragment thankYouConnectingFragment = new ThankYouConnectingFragment();
    Bundle extras = new Bundle();
    thankYouConnectingFragment.setArguments(extras);
    return thankYouConnectingFragment;
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_thankyouconnecting;
  }

  @Override public void bindViews(@Nullable View view) {
    done = (Button) view.findViewById(R.id.addressbook_done);
  }

  @Override public void setupViews() {
    RxView.clicks(done)
        .subscribe(clicks -> getNavigationManager().navigateTo(
            V8Engine.getFragmentProvider().newAddressBookFragment()),
            throwable -> throwable.printStackTrace());
  }
}

