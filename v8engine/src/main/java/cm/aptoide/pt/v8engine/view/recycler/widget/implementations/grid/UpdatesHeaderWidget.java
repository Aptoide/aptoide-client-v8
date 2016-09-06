/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.UpdatesAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderWidget extends Widget<UpdatesHeaderDisplayable> {

  private static final String TAG = UpdatesHeaderWidget.class.getSimpleName();
  private TextView title;
  private Button more;

  public UpdatesHeaderWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    title = (TextView) itemView.findViewById(R.id.title);
    more = (Button) itemView.findViewById(R.id.more);
  }

  @Override public void bindView(UpdatesHeaderDisplayable displayable) {
    title.setText(displayable.getLabel());
    more.setText(R.string.update_all);
    more.setVisibility(View.VISIBLE);
    more.setOnClickListener((view) -> {
      DownloadServiceHelper downloadManager =
          new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());
      final DownloadAccessor accessor = AccessorFactory.getAccessorFor(Download.class);
      UpdatesAccessor updatesAccessor = AccessorFactory.getAccessorFor(Update.class);
      updatesAccessor.getUpdates()
          .first()
          .observeOn(Schedulers.io())
          .map(updates -> {
            List<Download> downloadList = new ArrayList<>(updates.size());
            for (Update update : updates) {
              downloadList.add(new DownloadFactory().create(update));
            }
            return downloadList;
          })
          .flatMapIterable(downloads -> downloads)
          .map(download -> downloadManager.startDownload(accessor, (PermissionRequest) getContext(),
              download))
          .toList()
          .flatMap(observables -> Observable.merge(observables))
          .filter(downloading -> downloading.getOverallDownloadStatus() == Download.COMPLETED)
          .flatMap(downloading -> displayable.getInstallManager()
              .install(UpdatesHeaderWidget.this.getContext(),
                  (PermissionRequest) UpdatesHeaderWidget.this.getContext(),
                  downloading.getAppId()))
          .subscribe(aVoid -> Logger.d(TAG, "Update task completed"), Throwable::printStackTrace);

      Intent intent = new Intent();
      intent.setAction(HomeFragment.ChangeTabReceiver.SET_TAB_EVENT);
      intent.putExtra(HomeFragment.ChangeTabReceiver.SET_TAB_EVENT, Event.Name.myDownloads);
      getContext().sendBroadcast(intent);
      Analytics.Updates.updateAll();
    });
  }

  @Override public void onViewAttached() {

  }

  @Override public void onViewDetached() {

  }
}
