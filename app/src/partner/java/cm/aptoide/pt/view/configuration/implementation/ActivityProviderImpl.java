package cm.aptoide.pt.view.configuration.implementation;

import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.view.PartnersLaunchView;
import cm.aptoide.pt.view.ActivityProvider;

/**
 * Created by diogoloureiro on 11/08/2017.
 *
 * activity provider implementation
 */

public class ActivityProviderImpl implements ActivityProvider {

  /**
   * @return partners default main activity
   */
  @Override public Class<? extends AppCompatActivity> getMainActivityFragmentClass() {
    return PartnersLaunchView.class;
  }
}
