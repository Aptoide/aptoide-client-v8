package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.ThemeUtils;
import javax.inject.Inject;
import javax.inject.Named;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class DescriptionFragment extends BaseLoaderToolbarFragment
    implements NotBottomNavigationView {

  private static final String TAG = DescriptionFragment.class.getSimpleName();
  private static final String APP_ID = "app_id";
  private static final String PACKAGE_NAME = "packageName";
  private static final String STORE_NAME = "store_name";
  private static final String STORE_THEME = "store_theme";
  private static final String DESCRIPTION = "description";
  private static final String APP_NAME = "APP_NAME";
  @Inject @Named("aptoide-theme") String theme;
  private boolean hasAppId = false;
  private long appId;
  private String packageName;
  private TextView emptyData;
  private TextView descriptionContainer;
  private String storeName;
  private String storeTheme;
  private String description;
  private String appName;
  private BodyInterceptor<BaseBody> baseBodyBodyInterceptor;
  private StoreCredentialsProvider storeCredentialsProvider;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private String partnerId;

  public static DescriptionFragment newInstance(String appName, String description,
      String storeTheme) {
    DescriptionFragment fragment = new DescriptionFragment();
    Bundle args = new Bundle();
    args.putString(APP_NAME, appName);
    args.putString(STORE_THEME, storeTheme);
    args.putString(DESCRIPTION, description);
    fragment.setArguments(args);
    return fragment;
  }

  public static DescriptionFragment newInstance(long appId, String packageName, String storeName,
      String storeTheme) {
    DescriptionFragment fragment = new DescriptionFragment();
    Bundle args = new Bundle();
    args.putLong(APP_ID, appId);
    args.putString(PACKAGE_NAME, packageName);
    args.putString(STORE_NAME, storeName);
    args.putString(STORE_THEME, storeTheme);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
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

    if (args.containsKey(STORE_THEME)) {
      storeTheme = args.getString(STORE_THEME);
    }

    if (args.containsKey(DESCRIPTION)) {
      description = args.getString(DESCRIPTION);
    }
    if (args.containsKey(APP_NAME)) {
      appName = args.getString(APP_NAME);
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
          }, false);
    } else {
      Logger.getInstance()
          .e(TAG, "App id unavailable");
      setDataUnavailable();
    }
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

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    if (storeTheme != null) {
      ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      ThemeUtils.setStatusBarThemeColor(getActivity(), storeTheme);
      if (bar != null) {
        bar.setBackgroundDrawable(getActivity().getResources()
            .getDrawable(StoreTheme.get(storeTheme)
                .getGradientDrawable()));
      }
    }
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    descriptionContainer = (TextView) view.findViewById(R.id.data_container);
  }

  @Override public void onDestroyView() {
    ThemeUtils.setStatusBarThemeColor(getActivity(), theme);
    ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (bar != null) {
      bar.setBackgroundDrawable(getActivity().getResources()
          .getDrawable(StoreTheme.get(theme)
              .getGradientDrawable()));
    }
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
