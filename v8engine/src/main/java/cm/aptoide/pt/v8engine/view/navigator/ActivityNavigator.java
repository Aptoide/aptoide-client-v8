package cm.aptoide.pt.v8engine.view.navigator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcelobenites on 27/03/17.
 */

public class ActivityNavigator {

  private final Context context;

  public ActivityNavigator(Context context) {
    this.context = context;
  }

  public void navigateTo(Class<? extends AppCompatActivity> activityClass) {
    final Intent intent = new Intent();
    intent.setComponent(new ComponentName(context, activityClass));
    context.startActivity(intent);
  }
}
