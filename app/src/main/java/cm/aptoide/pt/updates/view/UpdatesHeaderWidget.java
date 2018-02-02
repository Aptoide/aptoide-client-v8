/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.updates.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.SimpleTabNavigation;
import cm.aptoide.pt.navigator.TabNavigation;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.updates.UpdatesAnalytics;
import cm.aptoide.pt.view.recycler.widget.Widget;
import java.util.ArrayList;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderWidget extends Widget<UpdatesHeaderDisplayable> {

  private static final String TAG = UpdatesHeaderWidget.class.getSimpleName();
  private TextView title;
  private Button more;
  private TabNavigator tabNavigator;
  private UpdatesAnalytics updatesAnalytics;

  public UpdatesHeaderWidget(View itemView) {
    super(itemView);
    final AnalyticsManager analyticsManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAnalyticsManager();
    final NavigationTracker navigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    updatesAnalytics = new UpdatesAnalytics(analyticsManager, navigationTracker);
  }

  @Override protected void assignViews(View itemView) {

    if (itemView.getContext() instanceof TabNavigator) {
      tabNavigator = (TabNavigator) itemView.getContext();
    } else {
      throw new IllegalStateException(
          "Context must implement " + TabNavigator.class.getSimpleName());
    }
    title = (TextView) itemView.findViewById(R.id.title);
    more = (Button) itemView.findViewById(R.id.more);
  }

  @Override public void bindView(UpdatesHeaderDisplayable displayable) {
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    final String marketName = application.getMarketName();
    title.setText(displayable.getLabel());
    more.setText(R.string.updatetab_button_update_all);
    more.setVisibility(View.VISIBLE);

    more.setOnClickListener((view) -> {
      updatesAnalytics.updates("Update All");
      ((PermissionService) getContext()).requestAccessToExternalFileSystem(() -> {
        UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(
            ((AptoideApplication) getContext().getApplicationContext()
                .getApplicationContext()).getDatabase(), Update.class);
        compositeSubscription.add(updateAccessor.getAll(false)
            .first()
            .observeOn(Schedulers.io())
            .map(updates -> {

              ArrayList<Download> downloadList = new ArrayList<>(updates.size());
              for (Update update : updates) {
                Download download = new DownloadFactory(marketName).create(update);
                displayable.setupDownloadEvent(download);
                downloadList.add(download);
              }
              return downloadList;
            })
            .flatMap(downloads -> displayable.getInstallManager()
                .startInstalls(downloads))
            .subscribe(aVoid -> Logger.i(TAG, "Update task completed"),
                throwable -> throwable.printStackTrace()));
      }, () -> {
      });

      tabNavigator.navigate(new SimpleTabNavigation(TabNavigation.DOWNLOADS));
    });
  }
}
