package cm.aptoide.pt.emptyapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import cm.aptoide.pt.shareappsandroid.HighwayActivity;

/**
 * Created by neuro on 17-02-2017.
 */
public class MainActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.System.canWrite(this)) {
        startActivity(new Intent(this, HighwayActivity.class));
        finish();
      } else {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
      }
    }

    startActivity(new Intent(this, HighwayActivity.class));
    finish();
  }
}
