package cm.aptoide.pt.v8engine.view.configuration;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by neuro on 17-10-2016.
 */

public interface ActivityProvider {
  Class<? extends AppCompatActivity> getMainActivityFragmentClass();
}
