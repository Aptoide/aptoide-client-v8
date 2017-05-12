package cm.aptoide.pt.v8engine.view.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import cm.aptoide.pt.v8engine.util.AppBarStateChangeListener;
import cm.aptoide.pt.v8engine.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class OtherVersionsFragment extends AptoideBaseFragment<BaseAdapter> {

  private static final String TAG = OtherVersionsFragment.class.getSimpleName();

  private CollapsingToolbarLayout collapsingToolbarLayout;
  private ViewHeader header;

  private String appName;
  private String appImgUrl;
  private String appPackge;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;

  /**
   * @param appName
   * @param appImgUrl
   * @param appPackage
   *
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

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
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
    header = new ViewHeader(view);
    collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
    setHasOptionsMenu(true);
    super.onViewCreated(view, savedInstanceState);
  }

  @Partners @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    //super.load(create, refresh, savedInstanceState);
    Logger.d(TAG, "Other versions should refresh? " + create);

    fetchOtherVersions(new ArrayList<>());
    setHeader();
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Partners protected void fetchOtherVersions(List<String> storeNames) {

    final SuccessRequestListener<ListAppVersions> otherVersionsSuccessRequestListener =
        listAppVersions -> {
          List<App> apps = listAppVersions.getList();
          ArrayList<Displayable> displayables = new ArrayList<>(apps.size());
          for (final App app : apps) {
            displayables.add(new OtherVersionDisplayable(app));
          }
          addDisplayables(displayables);
        };

    endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(this.getAdapter(),
        ListAppVersionsRequest.of(appPackge, storeNames, StoreUtils.getSubscribedStoresAuthMap(),
            baseBodyInterceptor, httpClient, converterFactory), otherVersionsSuccessRequestListener,
        err -> err.printStackTrace());

    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(false);
  }

  @Partners protected void setHeader() {
    if (header != null) {
      header.setImage(appImgUrl);
      setTitle(appName);
    }
  }

  private void setTitle(String title) {
    if (hasToolbar()) {
      getToolbar().setTitle(title);
      collapsingToolbarLayout.setTitle(title);
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

    private final SpannableString composedTitle1;
    private final SpannableString composedTitle2;

    // ctor
    ViewHeader(@NonNull View view) {
      composedTitle1 = new SpannableString(view.getResources()
          .getString(R.string.other_versions_partial_title_1));
      this.view = view;
      composedTitle1.setSpan(new StyleSpan(Typeface.ITALIC), 0, composedTitle1.length(), 0);

      composedTitle2 = new SpannableString(view.getResources()
          .getString(R.string.other_versions_partial_title_2));
      composedTitle2.setSpan(new StyleSpan(Typeface.ITALIC), 0, composedTitle2.length(), 0);

      animationsEnabled = ManagerPreferences.getAnimationsEnabledStatus();

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
            case COLLAPSED: {
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
  @Partners public class BundleCons {
    public static final String APP_NAME = "app_name";
    public static final String APP_IMG_URL = "app_img_url";
    public static final String APP_PACKAGE = "app_package";
  }
}
