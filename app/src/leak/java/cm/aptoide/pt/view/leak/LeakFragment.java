package cm.aptoide.pt.view.leak;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.leak.LeakTool;
import com.trello.rxlifecycle.components.support.RxFragment;

public class LeakFragment extends RxFragment {

  private LeakTool leakTool;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    leakTool = ((AptoideApplication) getContext().getApplicationContext()).getLeakTool();
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