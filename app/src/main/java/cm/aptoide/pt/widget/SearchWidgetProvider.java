package cm.aptoide.pt.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.R;

/**
 * Created by franciscocalado on 10/16/17.
 */

public class SearchWidgetProvider extends AppWidgetProvider {

  public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      Intent intent = new Intent(context, DeepLinkIntentReceiver.class);
      intent.setData(Uri.parse("aptoide://cm.aptoide.pt/deeplink?name=search"));

      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search_layout);
      views.setOnClickPendingIntent(R.id.widget_search_bar, pendingIntent);

      manager.updateAppWidget(appWidgetId, views);
    }
  }
}
