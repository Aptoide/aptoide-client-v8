package cm.aptoide.pt.view.entry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.view.ActivityView;

public class EntryActivity extends ActivityView {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startActivity(new Intent(this, ((V8Engine) getApplicationContext()).getEntryPointChooser()
        .getEntryPoint()));
    finish();
  }
}
