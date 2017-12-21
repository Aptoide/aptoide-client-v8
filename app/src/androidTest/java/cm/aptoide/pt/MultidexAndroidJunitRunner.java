package cm.aptoide.pt;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;

public class MultidexAndroidJunitRunner extends AndroidJUnitRunner {

  @Override public void onCreate(Bundle arguments) {
    MultiDex.install(getTargetContext());
    super.onCreate(arguments);
  }

  @Override public Application newApplication(ClassLoader cl, String className, Context context)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return super.newApplication(cl, MockAptoideApplication.class.getName(), context);
  }
}
