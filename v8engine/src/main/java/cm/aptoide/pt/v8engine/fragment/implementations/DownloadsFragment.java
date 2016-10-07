/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import com.trello.rxlifecycle.FragmentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 7/15/16.
 */
public class DownloadsFragment extends GridRecyclerFragmentWithDecorator {

  private static final String TAG = DownloadsFragment.class.getSimpleName();
  private List<Displayable> activeDisplayablesList = new LinkedList<>();
  private List<Displayable> completedDisplayablesList = new LinkedList<>();
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private List<Progress<Download>> oldDownloadsList;

  public static DownloadsFragment newInstance() {
    return new DownloadsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK),
        AccessorFactory.getAccessorFor(Download.class),
        AccessorFactory.getAccessorFor(Installed.class));

    oldDownloadsList = new ArrayList<>();
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    subscriptions = new CompositeSubscription();
    installManager.getInstallationsAsList()
        .observeOn(Schedulers.computation())
        .first()
        .map(downloads -> sortDownloads(downloads))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(downloads -> updateUi(downloads), Throwable::printStackTrace);

    installManager.getInstallationsAsList()
        .sample(250, TimeUnit.MILLISECONDS)
        .filter(downloads -> shouldUpdateList(downloads, oldDownloadsList))
        .map(downloads -> oldDownloadsList = downloads)
        .map(downloads -> sortDownloads(downloads))
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(downloads -> updateUi(downloads));
  }

  private List<Progress<Download>> sortDownloads(List<Progress<Download>> progressList) {
    Collections.sort(progressList, (lhs, rhs) -> Long.valueOf(lhs.getRequest().getTimeStamp())
        .compareTo(rhs.getRequest().getTimeStamp()) * -1);
    return progressList;
  }

  @Override public void onDestroyView() {
    if (subscriptions != null && !subscriptions.isUnsubscribed()) {
      subscriptions.clear();
    }
    super.onDestroyView();
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_fragment_downloads;
  }

  private void updateUi(List<Progress<Download>> progressList) {
    fillDisplayableList(progressList);
    View v = getView();
    if (v != null) {
      if (progressList.size() == 0) {
        getView().findViewById(R.id.no_apps_downloaded).setVisibility(View.VISIBLE);
      } else {
        getView().findViewById(R.id.no_apps_downloaded).setVisibility(View.GONE);
      }
    }
    setDisplayables();
  }

  private void fillDisplayableList(List<Progress<Download>> progressList) {
    activeDisplayablesList.clear();
    completedDisplayablesList.clear();
    for (final Progress<Download> progress : progressList) {
      if (isDownloading(progress)) {
        activeDisplayablesList.add(new ActiveDownloadDisplayable(progress, installManager));
      } else {
        completedDisplayablesList.add(new CompletedDownloadDisplayable(progress, installManager));
      }
    }
    Collections.reverse(activeDisplayablesList);
    if (completedDisplayablesList.size() > 0) {
      completedDisplayablesList.add(0, new StoreGridHeaderDisplayable(
          new GetStoreWidgets.WSWidget().setTitle(
              AptoideUtils.StringU.getResString(R.string.completed))));
    }
    if (activeDisplayablesList.size() > 0) {
      activeDisplayablesList.add(0,
          new ActiveDownloadsHeaderDisplayable(AptoideUtils.StringU.getResString(R.string.active),
              installManager));
    }
  }

  private boolean isDownloading(Progress<Download> progress) {
    return progress.getRequest().getOverallDownloadStatus() == Download.PROGRESS
        || progress.getRequest().getOverallDownloadStatus() == Download.PENDING
        || progress.getRequest().getOverallDownloadStatus() == Download.IN_QUEUE;
  }

  /**
   * this method checks if the downloads from the 2 lists are equivalents (same {@link
   * Download#getAppId()} and {@link Download#getOverallDownloadStatus()}
   *
   * @param progresses list of the most recent downloads list
   * @param oldDownloadsList list of the old downloads list
   * @return true if the lists have different downloads or the download state has change, false
   * otherwise
   */
  private Boolean shouldUpdateList(@NonNull List<Progress<Download>> progresses,
      @NonNull List<Progress<Download>> oldDownloadsList) {
    if (progresses.size() != oldDownloadsList.size()) {
      return true;
    }
    for (int i = 0; i < oldDownloadsList.size(); i++) {
      int oldIndex = getDownloadFromListById(progresses.get(i), oldDownloadsList);
      int newIndex = getDownloadFromListById(oldDownloadsList.get(i), progresses);
      if (oldIndex < 0
          || newIndex < 0
          || progresses.get(i).getRequest().getOverallDownloadStatus() != oldDownloadsList.get(
          oldIndex).getRequest().getOverallDownloadStatus()) {

        return true;
      }
    }
    return false;
  }

  private int getDownloadFromListById(Progress<Download> downloadProgress,
      List<Progress<Download>> oldDownloadsList) {

    for (int i = 0; i < oldDownloadsList.size(); i++) {
      if ((oldDownloadsList.get(i).getRequest().getAppId() == downloadProgress.getRequest()
          .getAppId())) {
        return i;
      }
    }
    return -1;
  }

  public void setDisplayables() {
    LinkedList<Displayable> displayables = new LinkedList<>();
    displayables.addAll(activeDisplayablesList);
    displayables.addAll(completedDisplayablesList);
    setDisplayables(displayables);
  }
}
