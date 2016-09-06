/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.ScheduledDownloadRepository;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduledDownloadDisplayable;
import com.trello.rxlifecycle.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sithengineer on 19/07/16.
 */
public class ScheduledDownloadsFragment extends GridRecyclerFragment {

  private static final String TAG = ScheduledDownloadsFragment.class.getSimpleName();

  private TextView emptyData;
  private ScheduledDownloadRepository scheduledDownloadRepository;

  //	private CompositeSubscription compositeSubscription;

  public ScheduledDownloadsFragment() {
  }

  public static ScheduledDownloadsFragment newInstance() {
    return new ScheduledDownloadsFragment();
  }

  @Override public void load(boolean refresh, Bundle savedInstanceState) {
    Logger.d(TAG, "refresh excluded updates? " + (refresh ? "yes" : "no"));
    fetchScheduledDownloads();
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_with_toolbar;
  }

  //	@Override
  //	public void onDestroyView() {
  //		super.onDestroyView();
  //		Observable.empty().observeOn(RealmSchedulers.getScheduler()).concatWith(Observable.fromCallable(() -> {
  //			if (compositeSubscription != null && compositeSubscription.hasSubscriptions()) {
  //				compositeSubscription.unsubscribe();
  //			}
  //			return null;
  //		})).subscribe();
  //	}

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    scheduledDownloadRepository = RepositoryFactory.getRepositoryFor(Scheduled.class);
    //		compositeSubscription = new CompositeSubscription();
    setHasOptionsMenu(true);
  }

  @Override public void setupToolbar() {
    super.setupToolbar();
    if (toolbar != null) {
      ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      bar.setDisplayHomeAsUpEnabled(true);
      bar.setTitle(R.string.setting_schdwntitle);
    }
  }

  private void fetchScheduledDownloads() {
    scheduledDownloadRepository.getAllScheduledDownloads()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(scheduledDownloads -> {
          updateUi(scheduledDownloads);
        }, t -> {
          Logger.e(TAG, t);
          emptyData.setText(R.string.no_sch_downloads);
          emptyData.setVisibility(View.VISIBLE);
          clearDisplayables();
          finishLoading();
        });

    // keep installing data when downloading were stoped
    scheduledDownloadRepository.getAllScheduledDownloads().first().subscribe(scheduleds -> {
      ArrayList<Scheduled> installing = new ArrayList<>();
      for (Scheduled s : scheduleds) {
        if (s.isDownloading()) installing.add(s);
      }
      downloadAndInstallScheduledList(installing);
    });

    //compositeSubscription.add(subscription);
  }

  @UiThread private void updateUi(List<Scheduled> scheduledDownloadList) {
    if (scheduledDownloadList == null || scheduledDownloadList.isEmpty()) {
      emptyData.setText(R.string.no_sch_downloads);
      emptyData.setVisibility(View.VISIBLE);
      clearDisplayables();
      finishLoading();
    } else {
      emptyData.setVisibility(View.GONE);
      ArrayList<ScheduledDownloadDisplayable> displayables =
          new ArrayList<>(scheduledDownloadList.size());
      for (final Scheduled scheduledDownload : scheduledDownloadList) {
        displayables.add(new ScheduledDownloadDisplayable(scheduledDownload));
      }
      setDisplayables(displayables);
    }
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_scheduled_downloads_fragment, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }

    if (itemId == R.id.menu_install_selected) {
      BaseAdapter adapter = getAdapter();
      ArrayList<Scheduled> scheduledList = new ArrayList<>();
      for (int i = 0; i < adapter.getItemCount(); ++i) {
        ScheduledDownloadDisplayable displayable =
            ((ScheduledDownloadDisplayable) adapter.getDisplayable(i));
        if (displayable.isSelected()) {
          scheduledList.add(displayable.getPojo());
          displayable.updateUi(true);
        }
      }

      if (downloadAndInstallScheduledList(scheduledList)) {
        ShowMessage.asSnack(this.emptyData, R.string.installing_msg);
      } else {
        ShowMessage.asSnack(this.emptyData, R.string.schDown_nodownloadselect);
      }

      return true;
    }

    if (itemId == R.id.menu_select_all) {
      BaseAdapter adapter = getAdapter();
      for (int i = 0; i < adapter.getItemCount(); ++i) {
        ((ScheduledDownloadDisplayable) adapter.getDisplayable(i)).setSelected(true);
        adapter.notifyDataSetChanged();
      }
      return true;
    }

    if (itemId == R.id.menu_select_none) {
      BaseAdapter adapter = getAdapter();
      for (int i = 0; i < adapter.getItemCount(); ++i) {
        ((ScheduledDownloadDisplayable) adapter.getDisplayable(i)).setSelected(false);
        adapter.notifyDataSetChanged();
      }
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private boolean downloadAndInstallScheduledList(ArrayList<Scheduled> installing) {

    if (installing == null || installing.isEmpty()) return false;

    DownloadFactory factory = new DownloadFactory();
    scheduledDownloadRepository.setInstalling(installing)
        .flatMapIterable(scheduleds -> scheduleds)
        .map(scheduled -> factory.create(scheduled))
        .toList()
        .flatMap(downloads -> downloadAndInstallDownloadList(downloads))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(aVoid -> {
          Logger.i(TAG, "finished installing scheduled downloads");
        });

    return true;
  }

  public Observable<List<Download>> downloadAndInstallDownloadList(List<Download> downloadList) {

    PermissionManager permissionManager = new PermissionManager();

    DownloadServiceHelper downloadManager =
        new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);

    InstallManager installManager =
        new InstallManager(permissionManager, getContext().getPackageManager(),
            new DownloadInstallationProvider(downloadManager));

    PermissionRequest permissionRequest = ((PermissionRequest) getContext());
    DownloadServiceHelper downloadServiceHelper =
        new DownloadServiceHelper(AptoideDownloadManager.getInstance(), new PermissionManager());

    Context ctx = getContext();

    return Observable.from(downloadList)
        .flatMap(downloadItem -> downloadAndInstall(downloadItem, permissionRequest,
            downloadServiceHelper, installManager, ctx))
        .buffer(downloadList.size()) // buffer all downloads in one event
        .first(); // return the whole list in one (first) event
  }

  private Observable<Download> downloadAndInstall(Download download,
      PermissionRequest permissionRequest, DownloadServiceHelper downloadServiceHelper,
      InstallManager installManager, Context context) {
    Logger.v(TAG, "downloading app with id " + download.getAppId());
    return downloadServiceHelper.startDownload(permissionRequest, download)
        .map(downloadItem -> { // for logging purposes only
          Logger.d(TAG,
              String.format("scheduled download progress = %d and status = %d for app id %d",
                  downloadItem.getOverallProgress(), downloadItem.getOverallDownloadStatus(),
                  downloadItem.getAppId()));
          return downloadItem;
        })
        .filter(downloadItem -> downloadItem.getOverallDownloadStatus() == Download.COMPLETED)
        .flatMap(downloadItem -> installAndRemoveFromList(installManager, context,
            downloadItem.getAppId()).map(aVoid -> downloadItem));
  }

  private Observable<Void> installAndRemoveFromList(InstallManager installManager, Context context,
      long appId) {
    Logger.v(TAG, "installing app with id " + appId);
    return installManager.install(context, (PermissionRequest) context, appId)
        .doOnError(err -> Logger.e(TAG, err))
        .doOnNext(aVoid -> scheduledDownloadRepository.deleteScheduledDownload(appId))
        .doOnUnsubscribe(() -> Logger.d(TAG,
            "Scheduled Downloads do on unsubscribed called for install manager"));
  }
}
