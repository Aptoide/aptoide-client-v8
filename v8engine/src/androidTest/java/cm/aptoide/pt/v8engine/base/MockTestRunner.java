package cm.aptoide.pt.v8engine.base;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;
import cm.aptoide.pt.v8engine.MultiDexTestApplication;

/**
 * Created by sithengineer on 14/10/2016.
 */

public class MockTestRunner extends AndroidJUnitRunner {
  @Override public Application newApplication(ClassLoader cl, String className, Context context)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return super.newApplication(cl, MultiDexTestApplication.class.getName(), context);
  }
}
