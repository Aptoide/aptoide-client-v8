package cm.aptoide.pt.v8engine.fragment.implementations;

import android.graphics.drawable.ColorDrawable;
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
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.BaseLoaderToolbarFragment;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import lombok.Getter;

public class DescriptionFragment extends BaseLoaderToolbarFragment {

  private static final String TAG = DescriptionFragment.class.getSimpleName();

  @Getter private static final String APP_ID = "app_id";
  @Getter private static final String PACKAGE_NAME = "packageName";
  @Getter private static final String STORE_NAME = "store_name";
  @Getter private static final String STORE_THEME = "store_theme";
  @Getter private static final String DESCRIPTION = "description";
  @Getter private static final String APP_NAME = "APP_NAME";
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
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
    baseBodyBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();
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
        bar.setTitle(appName);
      }
      finishLoading();
    } else if (hasAppId) {
      GetAppRequest.of(appId, V8Engine.getConfiguration().getPartnerId() == null ? null : storeName,
          StoreUtils.getStoreCredentials(storeName, storeCredentialsProvider), packageName,
          baseBodyBodyInterceptor).execute(getApp -> {
        setupAppDescription(getApp);
        setupTitle(getApp);
        finishLoading();
      }, false);
    } else {
      Logger.e(TAG, "App id unavailable");
      setDataUnavailable();
    }
  }

  private void setupAppDescription(GetApp getApp) {
    try {
      GetAppMeta.Media media = getApp.getNodes().getMeta().getData().getMedia();
      if (!TextUtils.isEmpty(media.getDescription())) {
        descriptionContainer.setText(AptoideUtils.HtmlU.parse(media.getDescription()));
        return;
      }
    } catch (NullPointerException e) {
      CrashReport.getInstance().log(e);
    }
    setDataUnavailable();
  }

  private void setupTitle(GetApp getApp) {
    try {
      String appName = getApp.getNodes().getMeta().getData().getName();
      if (!TextUtils.isEmpty(appName)) {
        if (hasToolbar()) {
          ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
          bar.setTitle(appName);
          return;
        }
      }
    } catch (NullPointerException e) {
      CrashReport.getInstance().log(e);
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
    ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (bar != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
      bar.setBackgroundDrawable(new ColorDrawable(
          getActivity().getResources().getColor(StoreThemeEnum.get(storeTheme).getStoreHeader())));
    }
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    emptyData = (TextView) view.findViewById(R.id.empty_data);
    descriptionContainer = (TextView) view.findViewById(R.id.data_container);
    setHasOptionsMenu(true);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_app_view_description;
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }
}
