package cm.aptoide.pt.v8engine.debugTools;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by trinkes on 27/03/2017.
 */

public class LeakTool {

  private RefWatcher refWatcher;

  public void setup(Application application) {
    if (LeakCanary.isInAnalyzerProcess(application)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    refWatcher = LeakCanary.install(application);
  }

  private RefWatcher getRefWatcher() {
    if (refWatcher == null) {
      throw new IllegalStateException("Call setup first!");
    }
    return refWatcher;
  }

  public void watch(Object fragment) {
    getRefWatcher().watch(fragment);
  }
}
