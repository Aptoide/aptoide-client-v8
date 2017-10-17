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
import cm.aptoide.pt.search.view.SearchActivity;
import cm.aptoide.pt.view.MainActivity;
import cm.aptoide.pt.view.entry.EntryActivity;

/**
 * Created by franciscocalado on 10/16/17.
 */

public class SearchWidgetProvider extends AppWidgetProvider {

  public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds){
    final int N = appWidgetIds.length;

    for(int i=0; i<N; i++){
      int appWidgetId = appWidgetIds[i];

      Intent intent  = new Intent(context, DeepLinkIntentReceiver.class);
      intent.setData(Uri.parse("aptoidesearch://fbbdfn"));

      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.search_widget);
      views.setOnClickPendingIntent(R.id.widget_search_bar, pendingIntent);

      manager.updateAppWidget(appWidgetId,views);
    }
  }
}
