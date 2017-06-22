package cm.aptoide.pt.v8engine.timeline.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.ActivityView;

public class PostActivity extends ActivityView {
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    if (savedInstanceState == null) {
      Intent intent = getIntent();
      String toShare = "";
      if (intent != null && intent.getType() != null && intent.getType()
          .equals("text/plain")) {
        toShare = intent.getStringExtra(Intent.EXTRA_TEXT);
      }

      final PostFragment fragment = PostFragment.newInstance(toShare);
      getFragmentNavigator().navigateToWithoutBackSave(fragment);
    }
  }

  @LayoutRes private int getLayoutId() {
    return R.layout.empty_frame;
  }
}
