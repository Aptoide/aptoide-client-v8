package cm.aptoide.pt.v8engine.timeline.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.ActivityView;

public class PostActivity extends ActivityView implements PostFragment.PostUrlProvider {

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    if (savedInstanceState == null) {
      final PostFragment fragment = PostFragment.newInstance();
      getFragmentNavigator().navigateToWithoutBackSave(fragment);
    }
  }

  @LayoutRes private int getLayoutId() {
    return R.layout.empty_frame;
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
  }

  @Override public String getUrlToShare() {
    Intent intent = getIntent();
    String toShare = null;
    if (intent != null && intent.getType() != null && intent.getType()
        .equals("text/plain")) {
      toShare = intent.getStringExtra(Intent.EXTRA_TEXT);
    }
    return toShare;
  }
}
