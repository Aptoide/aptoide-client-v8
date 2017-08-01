package cm.aptoide.pt.view.leak;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.leak.LeakTool;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.leak.LeakTool;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by trinkes on 28/03/2017.
 */

public class LeakActivity extends RxAppCompatActivity {

  private LeakTool leakTool;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    leakTool = ((V8Engine) getApplicationContext()).getLeakTool();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    leakTool.watch(this);
  }
}
