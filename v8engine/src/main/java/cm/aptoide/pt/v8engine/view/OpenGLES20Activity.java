package cm.aptoide.pt.v8engine.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by jdandrade on 14/11/2016.
 */
public class OpenGLES20Activity extends AppCompatActivity {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_open_gl);
  }

  @Override protected void onPause() {
    super.onPause();
    overridePendingTransition(0, 0);
  }
}
