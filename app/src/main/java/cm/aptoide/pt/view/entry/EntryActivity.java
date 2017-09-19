package cm.aptoide.pt.view.entry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.view.ActivityView;

public class EntryActivity extends ActivityView {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startActivity(new Intent(this,
        ((AptoideApplication) getApplicationContext()).getEntryPointChooser()
            .getEntryPoint()));
    finish();
  }
}
