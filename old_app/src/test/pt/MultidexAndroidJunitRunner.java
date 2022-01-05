package cm.aptoide.pt;

import android.os.Bundle;
import androidx.multidex.MultiDex;
import androidx.test.runner.AndroidJUnitRunner;

public class MultidexAndroidJunitRunner extends AndroidJUnitRunner {

  @Override public void onCreate(Bundle arguments) {
    MultiDex.install(getTargetContext());
    super.onCreate(arguments);
  }
}
