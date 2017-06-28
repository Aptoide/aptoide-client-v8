package cm.aptoide.pt.v8engine.view.downloads;

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
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.InstallationProgress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.download.DownloadEventConverter;
import cm.aptoide.pt.v8engine.download.InstallEventConverter;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.presenter.DownloadsPresenter;
import cm.aptoide.pt.v8engine.presenter.DownloadsView;
import cm.aptoide.pt.v8engine.view.custom.DividerItemDecoration;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class DownloadsFragment extends FragmentView implements DownloadsView {

  private DownloadsAdapter adapter;
  private View noDownloadsView;
  private InstallEventConverter installConverter;
  private DownloadEventConverter downloadConverter;
  private InstallManager installManager;
  private Analytics analytics;

  public static DownloadsFragment newInstance() {
    return new DownloadsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    final BodyInterceptor<BaseBody> baseBodyInterceptorV7 =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    final TokenInvalidator tokenInvalidator =
        ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    installConverter =
        new InstallEventConverter(baseBodyInterceptorV7, httpClient, converterFactory,
            tokenInvalidator, V8Engine.getConfiguration()
            .getAppId(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE));
    downloadConverter =
        new DownloadEventConverter(baseBodyInterceptorV7, httpClient, converterFactory,
            tokenInvalidator, V8Engine.getConfiguration()
            .getAppId(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE));
    installManager = ((V8Engine) getContext().getApplicationContext()).getInstallManager(
        InstallerFactory.ROLLBACK);
    analytics = Analytics.getInstance();
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
        getContext().getResources());
    downloadsRecyclerView.setAdapter(adapter);
    noDownloadsView = view.findViewById(R.id.no_apps_downloaded);

    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    attachPresenter(new DownloadsPresenter(this, installManager), savedInstanceState);
  }

  @UiThread @Override public void showActiveDownloads(List<InstallationProgress> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setActiveDownloads(downloads);
  }

  @UiThread @Override public void showStandByDownloads(List<InstallationProgress> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setStandByDownloads(downloads);
  }

  @UiThread @Override public void showCompletedDownloads(List<InstallationProgress> downloads) {
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