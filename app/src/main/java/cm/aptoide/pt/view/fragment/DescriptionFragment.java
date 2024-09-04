package cm.aptoide.pt.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.NotBottomNavigationView;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class DescriptionFragment extends BaseLoaderToolbarFragment
    implements NotBottomNavigationView {

  private static final String TAG = DescriptionFragment.class.getSimpleName();
  private static final String APP_ID = "app_id";
  private static final String PACKAGE_NAME = "packageName";
  private static final String STORE_NAME = "store_name";
  private static final String DESCRIPTION = "description";
  private static final String APP_NAME = "APP_NAME";
  private static final String HAS_APPC = "HAS_APPC";
  @Inject ThemeManager themeManager;
  @Inject StoreCredentialsProvider storeCredentialsProvider;
  private boolean hasAppId = false;
  private long appId;
  private String packageName;
  private TextView emptyData;
  private TextView descriptionContainer;
  private String storeName;
  private String description;
  private String appName;
  private boolean hasAppc;
  private BodyInterceptor<BaseBody> baseBodyBodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private String partnerId;
  private Toolbar toolbar;

  public static DescriptionFragment newInstance(String appName, String description,
      boolean isAppc) {
    DescriptionFragment fragment = new DescriptionFragment();
    Bundle args = new Bundle();
    args.putString(APP_NAME, appName);
    args.putString(DESCRIPTION, description);
    args.putBoolean(HAS_APPC, isAppc);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    baseBodyBodyInterceptor = application.getAccountSettingsBodyInterceptorPoolV7();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    partnerId = application.getPartnerId();
    setHasOptionsMenu(true);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);

    if (args.containsKey(APP_ID)) {
      appId = args.getLong(APP_ID, -1);
      hasAppId = true;
    }

    if (args.containsKey(PACKAGE_NAME)) {
      packageName = args.getString(PACKAGE_NAME);
    }

    if (args.containsKey(STORE_NAME)) {
      storeName = args.getString(STORE_NAME);
    }

    if (args.containsKey(DESCRIPTION)) {
      description = args.getString(DESCRIPTION);
    }
    if (args.containsKey(APP_NAME)) {
      appName = args.getString(APP_NAME);
    }
    if (args.containsKey(HAS_APPC)) {
      hasAppc = args.getBoolean(HAS_APPC);
    }
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override protected int[] getViewsToShowAfterLoadingId() {
    return new int[] {};
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.data_container;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(appName)) {

      descriptionContainer.setText(AptoideUtils.HtmlU.parse(description));
      if (hasToolbar()) {
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
          bar.setTitle(appName);
        }
        if (hasAppc) {
          setupAppcAppView();
        }
      }
      finishLoading();
    } else if (hasAppId) {
      GetAppRequest.of(appId, partnerId == null ? null : storeName,
          StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), packageName,
          baseBodyBodyInterceptor, httpClient, converterFactory,
          ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
          ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())
          .execute(getApp -> {
            setupAppDescription(getApp);
            setupTitle(getApp);
            finishLoading();
            if (hasAppc(getApp)) {
              hasAppc = true;
              setupAppcAppView();
            }
          }, false);
    } else {
      Logger.getInstance()
          .e(TAG, "App id unavailable");
      setDataUnavailable();
    }
  }

  private boolean hasAppc(GetApp getApp) {
    return getApp.getNodes()
        .getMeta()
        .getData()
        .hasAdvertising() || getApp.getNodes()
        .getMeta()
        .getData()
        .hasBilling();
  }

  private void setupAppcAppView() {
    Drawable drawable = ContextCompat.getDrawable(getContext(),
        themeManager.getAttributeForTheme(R.attr.appDescriptionToolbarAppc).resourceId);
    toolbar.setBackground(drawable);
  }

  private void setupAppDescription(GetApp getApp) {
    try {
      GetAppMeta.Media media = getApp.getNodes()
          .getMeta()
          .getData()
          .getMedia();
      if (!TextUtils.isEmpty(media.getDescription())) {
        descriptionContainer.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
        return;
      }
    } catch (NullPointerException e) {
      CrashReport.getInstance()
          .log(e);
    }
    setDataUnavailable();
  }

  private void setupTitle(GetApp getApp) {
    try {
      String appName = getApp.getNodes()
          .getMeta()
          .getData()
          .getName();
      if (!TextUtils.isEmpty(appName)) {
        if (hasToolbar()) {
          ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
          bar.setTitle(appName);
          return;
        }
      }
    } catch (NullPointerException e) {
      CrashReport.getInstance()
          .log(e);
    }
    setDataUnavailable();
  }

  private void setDataUnavailable() {
    emptyData.setVisibility(View.VISIBLE);
    descriptionContainer.setVisibility(View.GONE);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = view.findViewById(R.id.empty_data);
    descriptionContainer = view.findViewById(R.id.data_container);
    toolbar = view.findViewById(R.id.toolbar);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_app_view_description;
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }
}
