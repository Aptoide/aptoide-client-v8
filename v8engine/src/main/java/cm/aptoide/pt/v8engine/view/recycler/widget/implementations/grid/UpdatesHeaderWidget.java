/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.UpdateAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigation.TabNavigator;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
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

  public UpdatesHeaderWidget(View itemView) {
    super(itemView);
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
    title.setText(displayable.getLabel());
    more.setText(R.string.update_all);
    more.setVisibility(View.VISIBLE);

    more.setOnClickListener((view) -> {
      ((PermissionService) getContext()).requestAccessToExternalFileSystem(() -> {
        UpdateAccessor updateAccessor = AccessorFactory.getAccessorFor(Update.class);
        compositeSubscription.add(
            updateAccessor.getAll(false)
                .first()
                .observeOn(Schedulers.io())
                .map(updates -> {

                  ArrayList<Download> downloadList = new ArrayList<>(updates.size());
                  for (Update update : updates) {
                    downloadList.add(new DownloadFactory().create(update));
                  }
                  return downloadList;
                })
                .flatMap(downloads -> displayable.getInstallManager()
                    .startInstalls(downloads, getContext()))
                .subscribe(aVoid -> Logger.i(TAG, "Update task completed"),
                    throwable -> throwable.printStackTrace()));
      }, () -> {
      });

      tabNavigator.navigate(TabNavigator.DOWNLOADS);
      Analytics.Updates.updateAll();
    });
  }
}
