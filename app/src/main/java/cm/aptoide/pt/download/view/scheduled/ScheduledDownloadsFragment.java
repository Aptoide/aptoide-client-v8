package cm.aptoide.pt.download.view.scheduled;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.Install;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadEvent;
import cm.aptoide.pt.download.DownloadEventConverter;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.DownloadInstallBaseEvent;
import cm.aptoide.pt.download.InstallEvent;
import cm.aptoide.pt.download.InstallEventConverter;
import cm.aptoide.pt.download.ScheduledDownloadRepository;
import cm.aptoide.pt.install.InstallerFactory;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;

import static cm.aptoide.pt.DeepLinkIntentReceiver.SCHEDULE_DOWNLOADS;

public class ScheduledDownloadsFragment extends AptoideBaseFragment<BaseAdapter> {

  public static final String OPEN_SCHEDULE_DOWNLOADS_WITH_POPUP_URI =
      "aptoide://cm.aptoide.pt/" + SCHEDULE_DOWNLOADS + "?openMode=AskInstallAll";
  public static final String OPEN_MODE = "openMode";
  private static final String TAG = ScheduledDownloadsFragment.class.getSimpleName();
  private InstallManager installManager;
  private TextView emptyData;
  private ScheduledDownloadRepository scheduledDownloadRepository;
  private OpenMode openMode = OpenMode.normal;
  private DownloadEventConverter downloadConverter;
  private Analytics analytics;
  private InstallEventConverter installConverter;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private String marketName;

  public ScheduledDownloadsFragment() {
  }

  public static ScheduledDownloadsFragment newInstance() {
    return new ScheduledDownloadsFragment();
  }

  public static Fragment newInstance(OpenMode openMode) {
    ScheduledDownloadsFragment scheduledDownloadsFragment = new ScheduledDownloadsFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(OPEN_MODE, openMode);
    scheduledDownloadsFragment.setArguments(bundle);
    return scheduledDownloadsFragment;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    marketName = application.getMarketName();
    final OkHttpClient httpClient = application.getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    bodyInterceptor = application.getAccountSettingsBodyInterceptorPoolV7();
    installManager = application.getInstallManager(InstallerFactory.ROLLBACK);
    final TokenInvalidator tokenInvalidator = application.getTokenInvalidator();
    downloadConverter =
        new DownloadEventConverter(bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            BuildConfig.APPLICATION_ID, application.getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE),
            application.getAptoideNavigationTracker());
    installConverter =
        new InstallEventConverter(bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
            BuildConfig.APPLICATION_ID, application.getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE),
            aptoideNavigationTracker);
    analytics = Analytics.getInstance();
    setHasOptionsMenu(true);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    openMode = (OpenMode) args.getSerializable(OPEN_MODE);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_with_toolbar;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    scheduledDownloadRepository =
        RepositoryFactory.getScheduledDownloadRepository(getContext().getApplicationContext());
    //		compositeSubscription = new CompositeSubscription();
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create) {
      switch (openMode) {
        case normal:
          break;
        case AskInstallAll:
          GenericDialogs.createGenericYesNoCancelMessage(getContext(),
              getString(R.string.all_title_schedule_download), getString(R.string.schDown_install))
              .subscribe(userResponse -> {
                switch (userResponse) {
                  case YES:
                    scheduledDownloadRepository.getAllScheduledDownloads()
                        .first()
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                        .subscribe(
                            scheduledList -> downloadAndInstallScheduledList(scheduledList, true),
                            err -> {
                              CrashReport.getInstance()
                                  .log(err);
                            });
                    break;
                  case NO:
                    break;
                  case CANCEL:
                    break;
                }
              });
          break;
      }
    }
    fetchScheduledDownloads();
  }

  private boolean downloadAndInstallScheduledList(List<Scheduled> installing,
      boolean isStartedAutomatic) {

    if (installing == null || installing.isEmpty()) {
      return false;
    }

    Context context = getContext();
    PermissionManager permissionManager = new PermissionManager();
    PermissionService permissionRequest = ((PermissionService) context);
    DownloadFactory downloadFactory = new DownloadFactory(marketName);

    permissionManager.requestExternalStoragePermission(permissionRequest)
        .flatMap(sucess -> scheduledDownloadRepository.setInstalling(installing))
        .flatMapIterable(scheduleds -> scheduleds)
        .map(scheduled -> downloadFactory.create(scheduled))
        .flatMap(downloadItem -> installManager.install(downloadItem)
            .toObservable()
            .flatMap(downloadProgress -> installManager.getInstall(downloadItem.getMd5(),
                downloadItem.getPackageName(), downloadItem.getVersionCode()))
            .doOnSubscribe(() -> setupEvents(downloadItem,
                isStartedAutomatic ? DownloadEvent.Action.AUTO : DownloadEvent.Action.CLICK))
            .filter(installationProgress -> installationProgress.getState()
                == Install.InstallationStatus.INSTALLED)
            .doOnNext(success -> scheduledDownloadRepository.deleteScheduledDownload(
                downloadItem.getMd5())))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(aVoid -> {
          Logger.i(TAG, "finished installing scheduled downloads");
        }, throwable -> {
          Logger.e(TAG, throwable.getMessage());
        });

    return true;
  }

  private void fetchScheduledDownloads() {
    scheduledDownloadRepository.getAllScheduledDownloads()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(scheduledDownloads -> {
          updateUi(scheduledDownloads);
        }, t -> {
          CrashReport.getInstance()
              .log(t);
          emptyData.setText(R.string.no_sch_downloads);
          emptyData.setVisibility(View.VISIBLE);
          clearDisplayables();
          finishLoading();
        });

    // keep installing data when downloading were stoped
    //scheduledDownloadRepository.getAllScheduledDownloads().first().subscribe(scheduleds -> {
    //  ArrayList<Scheduled> installing = new ArrayList<>();
    //  for (Scheduled s : scheduleds) {
    //    if (s.isDownloading()) installing.add(s);
    //  }
    //  downloadAndInstallScheduledList(installing);
    //});

    //compositeSubscription.add(subscription);
  }

  public void setupEvents(Download download, DownloadEvent.Action action) {
    DownloadEvent report =
        downloadConverter.create(download, action, DownloadEvent.AppContext.SCHEDULED);
    analytics.save(download.getPackageName() + download.getVersionCode(), report);

    InstallEvent installEvent =
        installConverter.create(download, DownloadInstallBaseEvent.Action.CLICK,
            DownloadInstallBaseEvent.AppContext.SCHEDULED);
    analytics.save(download.getPackageName() + download.getVersionCode(), installEvent);
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
        displayables.add(new ScheduledDownloadDisplayable(scheduledDownload, installManager));
      }
      clearDisplayables().addDisplayables(displayables, true);
    }
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(R.string.all_title_schedule_download);
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
        }
      }

      if (downloadAndInstallScheduledList(scheduledList, false)) {
        ShowMessage.asSnack(this.emptyData, R.string.installing_msg);
      } else {
        ShowMessage.asSnack(this.emptyData, R.string.schDown_nodownloadselect);
      }

      return true;
    }

    if (itemId == R.id.menu_remove) {
      BaseAdapter adapter = getAdapter();
      for (int i = 0; i < adapter.getItemCount(); ++i) {
        ScheduledDownloadDisplayable displayable =
            (ScheduledDownloadDisplayable) adapter.getDisplayable(i);
        if (displayable.isSelected()) {
          displayable.removeFromDatabase(((ScheduledAccessor) AccessorFactory.getAccessorFor(
              ((AptoideApplication) getContext().getApplicationContext()
                  .getApplicationContext()).getDatabase(), Scheduled.class)));
        }
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

  public enum OpenMode {
    normal, AskInstallAll
  }
}
