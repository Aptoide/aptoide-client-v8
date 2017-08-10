package cm.aptoide.pt.view.leak;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.leak.LeakTool;
import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Created by trinkes on 27/03/2017.
 */

public class LeakFragment extends RxFragment {

  private LeakTool leakTool;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    leakTool = ((V8Engine) getContext().getApplicationContext()).getLeakTool();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    final View view = getView();
    if (view != null) {
      leakTool.watch(view);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    leakTool.watch(this);
  }
}