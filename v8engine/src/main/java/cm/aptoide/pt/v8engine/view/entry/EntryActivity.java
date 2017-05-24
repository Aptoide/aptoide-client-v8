package cm.aptoide.pt.v8engine.view.entry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by neuro on 12-05-2017.
 */

public class EntryActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startActivity(new Intent(this,
        ((V8Engine) getApplicationContext()).getEntryPointChooser().getEntryPoint()));
    finish();
  }
}
