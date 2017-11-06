package cm.aptoide.pt.download.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.download.DownloadEventConverter;
import cm.aptoide.pt.download.InstallEventConverter;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.presenter.DownloadsPresenter;
import cm.aptoide.pt.presenter.DownloadsView;
import cm.aptoide.pt.store.view.StoreTabNavigator;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.custom.DividerItemDecoration;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class DownloadsFragment extends NavigationTrackFragment implements DownloadsView {

  private DownloadsAdapter adapter;
  private View noDownloadsView;
  private InstallEventConverter installConverter;
  private DownloadEventConverter downloadConverter;
  private InstallManager installManager;
  private Analytics analytics;
  private StoreTabNavigator storeTabNavigator;

  public static DownloadsFragment newInstance() {
    return new DownloadsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final OkHttpClient httpClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    final BodyInterceptor<BaseBody> baseBodyInterceptorV7 =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    final TokenInvalidator tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    installConverter =
        new InstallEventConverter(baseBodyInterceptorV7, httpClient, converterFactory,
            tokenInvalidator, BuildConfig.APPLICATION_ID,
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE),
            ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker());
    downloadConverter =
        new DownloadEventConverter(baseBodyInterceptorV7, httpClient, converterFactory,
            tokenInvalidator, BuildConfig.APPLICATION_ID,
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE),
            ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker());
    installManager =
        ((AptoideApplication) getContext().getApplicationContext()).getRollbackInstallManager();
    analytics = Analytics.getInstance();

    storeTabNavigator = new StoreTabNavigator(getFragmentNavigator());
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    attachPresenter(new DownloadsPresenter(this, installManager));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recycler_fragment_downloads, container, false);

    RecyclerView downloadsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    downloadsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    final int pixelDimen = AptoideUtils.ScreenU.getPixelsForDip(5, getContext().getResources());
    final DividerItemDecoration decor =
        new DividerItemDecoration(getContext(), pixelDimen, DividerItemDecoration.ALL);
    downloadsRecyclerView.addItemDecoration(decor);

    adapter = new DownloadsAdapter(installConverter, downloadConverter, installManager, analytics,
        getContext().getResources(), storeTabNavigator, navigationTracker);
    downloadsRecyclerView.setAdapter(adapter);
    noDownloadsView = view.findViewById(R.id.no_apps_downloaded);

    return view;
  }

  @UiThread @Override public void showActiveDownloads(List<Install> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setActiveDownloads(downloads);
  }

  @UiThread @Override public void showStandByDownloads(List<Install> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setStandByDownloads(downloads);
  }

  @UiThread @Override public void showCompletedDownloads(List<Install> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setCompletedDownloads(downloads);
  }

  @UiThread @Override public void showEmptyDownloadList() {
    setEmptyDownloadVisible(true);
    adapter.clearAll();
  }

  @UiThread private void setEmptyDownloadVisible(boolean visible) {
    if (noDownloadsView.getVisibility() == View.GONE && visible) {
      noDownloadsView.setVisibility(View.VISIBLE);
    }

    if (noDownloadsView.getVisibility() == View.VISIBLE && !visible) {
      noDownloadsView.setVisibility(View.GONE);
    }
  }
}
