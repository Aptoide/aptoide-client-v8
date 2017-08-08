package cm.aptoide.pt.view.configuration.implementation;

import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.view.PartnersLaunchActivity;
import cm.aptoide.pt.view.configuration.ActivityProvider;

/**
 * Created by neuro on 17-10-2016.
 */

public class ActivityProviderImpl implements ActivityProvider {
  @Override public Class<? extends AppCompatActivity> getMainActivityFragmentClass() {
    return PartnersLaunchActivity.class;
  }
}
