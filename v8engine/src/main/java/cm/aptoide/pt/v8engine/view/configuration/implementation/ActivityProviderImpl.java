package cm.aptoide.pt.v8engine.view.configuration.implementation;

import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.v8engine.view.configuration.ActivityProvider;
import cm.aptoide.pt.v8engine.view.MainActivity;

/**
 * Created by neuro on 17-10-2016.
 */

public class ActivityProviderImpl implements ActivityProvider {
  @Override public Class<? extends AppCompatActivity> getMainActivityFragmentClass() {
    return MainActivity.class;
  }
}
