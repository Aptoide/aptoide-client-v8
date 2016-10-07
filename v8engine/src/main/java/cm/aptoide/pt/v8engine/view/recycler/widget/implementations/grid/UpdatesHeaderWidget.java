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
import cm.aptoide.pt.database.accessors.UpdatesAccessor;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderWidget extends Widget<UpdatesHeaderDisplayable> {

  private static final String TAG = UpdatesHeaderWidget.class.getSimpleName();
  private TextView title;
  private Button more;
  private Subscription subscription;

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
      PermissionManager permissionManager = new PermissionManager();
      UpdatesAccessor updatesAccessor = AccessorFactory.getAccessorFor(Update.class);

      subscription =
          permissionManager.requestExternalStoragePermission((PermissionRequest) getContext())
              .flatMap(success -> permissionManager.requestDownloadAccess(
                  (PermissionRequest) getContext()))
              .flatMap(success -> updatesAccessor.getUpdates())
              .first()
              .observeOn(Schedulers.io())
              .flatMapIterable(updates -> updates)
              .map(update -> new DownloadFactory().create(update))
              .flatMap(downloading -> displayable.install(UpdatesHeaderWidget.this.getContext(),
                  downloading))
              .subscribe(aVoid -> Logger.i(TAG, "Update task completed"),
                  throwable -> throwable.printStackTrace());

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
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }
}
