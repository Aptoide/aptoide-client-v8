package cm.aptoide.pt.v8engine.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.R;

public class OpenGLES20Activity extends ActivityView {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_open_gl);
  }

  @Override protected void onPause() {
    super.onPause();
    overridePendingTransition(0, 0);
  }
}
