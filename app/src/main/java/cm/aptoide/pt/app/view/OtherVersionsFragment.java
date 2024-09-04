package cm.aptoide.pt.app.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.displayable.OtherVersionDisplayable;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.SuccessRequestListener;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.util.AppBarStateChangeListener;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import cm.aptoide.pt.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class OtherVersionsFragment extends AptoideBaseFragment<BaseAdapter> {

  private static final String TAG = OtherVersionsFragment.class.getSimpleName();
  @Inject RoomStoreRepository storeRepository;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private ViewHeader header;
  private String appName;
  private String appImgUrl;
  private String appPackge;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private SharedPreferences sharedPreferences;
  private ThemeManager themeManager;

  /**
   * @param appName
   * @param appImgUrl
   * @param appPackage
   * @return
   */
  public static OtherVersionsFragment newInstance(String appName, String appImgUrl,
      String appPackage) {
    OtherVersionsFragment fragment = new OtherVersionsFragment();
    Bundle args = new Bundle();
    args.putString(BundleCons.APP_NAME, appName);
    args.putString(BundleCons.APP_IMG_URL, appImgUrl);
    args.putString(BundleCons.APP_PACKAGE, appPackage);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    themeManager = new ThemeManager(getActivity(),
        ((AptoideApplication) getActivity().getApplicationContext()).getDefaultSharedPreferences());
    sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    baseBodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    setHasOptionsMenu(true);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    appName = args.getString(BundleCons.APP_NAME);
    appImgUrl = args.getString(BundleCons.APP_IMG_URL);
    appPackge = args.getString(BundleCons.APP_PACKAGE);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_other_versions;
  }

  @Override public void onDestroyView() {
    final RecyclerView recyclerView = getRecyclerView();
    if (recyclerView != null && endlessRecyclerOnScrollListener != null) {
      recyclerView.removeOnScrollListener(endlessRecyclerOnScrollListener);
    }
    endlessRecyclerOnScrollListener = null;
    header = null;
    collapsingToolbarLayout = null;
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    header = new ViewHeader(view, sharedPreferences);
    collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    Logger.getInstance()
        .d(TAG, "Other versions should refresh? " + create);
    fetchOtherVersions();
    setHeader();
  }

  @Override public void onResume() {
    super.onResume();
  }

  protected void fetchOtherVersions() {

    final SuccessRequestListener<ListAppVersions> otherVersionsSuccessRequestListener =
        listAppVersions -> {
          List<App> apps = listAppVersions.getList();
          ArrayList<Displayable> displayables = new ArrayList<>(apps.size());
          for (final App app : apps) {
            displayables.add(new OtherVersionDisplayable(app, themeManager));
          }
          addDisplayables(displayables);
          getRecyclerView().setVisibility(View.VISIBLE);
        };

    endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(this.getAdapter(),
        ListAppVersionsRequest.of(appPackge, StoreUtils.getSubscribedStoresAuthMap(storeRepository),
            baseBodyInterceptor, httpClient, converterFactory,
            ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            getContext().getResources()),
        otherVersionsSuccessRequestListener, err -> err.printStackTrace());

    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(false, false);
  }

  protected void setHeader() {
    if (header != null) {
      header.setImage(appImgUrl);
      setTitle(appName);
    }
  }

  private void setTitle(String title) {
    if (hasToolbar()) {
      getToolbar().setTitle(title);
      collapsingToolbarLayout.setTitle(title);
      collapsingToolbarLayout.setExpandedTitleColor(getView().getResources()
          .getColor(themeManager.getAttributeForTheme(R.attr.textColorGrey900).resourceId));
    }
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  //
  // micro widget for header
  //

  private static final class ViewHeader {

    private final boolean animationsEnabled;

    // views
    private final View view;
    private final TextView otherVersionsTitle;
    private final AppBarLayout appBarLayout;
    private final ImageView appIcon;

    // ctor
    ViewHeader(@NonNull View view, SharedPreferences sharedPreferences) {
      this.view = view;

      animationsEnabled = ManagerPreferences.getAnimationsEnabledStatus(sharedPreferences);

      otherVersionsTitle = (TextView) view.findViewById(R.id.other_versions_title);
      appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
      //collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
      appIcon = (ImageView) view.findViewById(R.id.app_icon);

      appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

        @Override public void onStateChanged(AppBarLayout appBarLayout, State state) {
          switch (state) {
            case EXPANDED: {
              if (animationsEnabled) {
                appIcon.animate()
                    .alpha(1F)
                    .start();
              } else {
                appIcon.setVisibility(View.VISIBLE);
              }
              otherVersionsTitle.setVisibility(View.VISIBLE);
              break;
            }
            default:
            case IDLE:
            case MOVING: {
              if (animationsEnabled) {
                appIcon.animate()
                    .alpha(0F)
                    .start();
              } else {
                appIcon.setVisibility(View.INVISIBLE);
              }
              otherVersionsTitle.setVisibility(View.INVISIBLE);
              break;
            }
          }
        }
      });
    }

    private void setImage(String imgUrl) {
      ImageLoader.with(view.getContext())
          .load(imgUrl, appIcon);
    }
  }

  /**
   * Bundle of Constants
   */
  public class BundleCons {
    public static final String APP_NAME = "app_name";
    public static final String APP_IMG_URL = "app_img_url";
    public static final String APP_PACKAGE = "app_package";
  }
}
