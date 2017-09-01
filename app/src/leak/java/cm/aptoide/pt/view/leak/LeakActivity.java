package cm.aptoide.pt.view.leak;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.leak.LeakTool;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public class LeakActivity extends RxAppCompatActivity {

  private LeakTool leakTool;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    leakTool = ((AptoideApplication) getApplicationContext()).getLeakTool();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    leakTool.watch(this);
  }
}
