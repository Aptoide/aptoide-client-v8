package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.debugTools.LeakTool;
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
    leakTool.watch(this);
  }
}