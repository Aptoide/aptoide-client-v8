package cm.aptoide.pt.v8engine.fragment.implementations;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.networkclient.interfaces.SuccessRequestListener;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.AptoideBaseFragment;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.util.AppBarStateChangeListener;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OtherVersionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.ArrayList;
import java.util.List;

public class OtherVersionsFragment extends AptoideBaseFragment<BaseAdapter> {

  private static final String TAG = OtherVersionsFragment.class.getSimpleName();

  // vars
  private String appName;
  private String appImgUrl;
  private String appPackge;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  // views
  private ViewHeader header;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  //private TextView emptyData;

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
    baseBodyInterceptor = ((V8Engine)getContext().getApplicationContext()).getBaseBodyInterceptor();
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

  @Override public void bindViews(View view) {
    super.bindViews(view);
    final Context context = getContext();
    header = new ViewHeader(context, view);
    collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
    //emptyData = (TextView) view.findViewById(R.id.empty_data);
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(),
            ListAppVersionsRequest.of(appPackge, storeNames, StoreUtils.getSubscribedStoresAuthMap(),
                baseBodyInterceptor),
            otherVersionsSuccessRequestListener, err -> err.printStackTrace());

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

    private final Context context;

    // views
    private final TextView otherVersionsTitle;
    private final AppBarLayout appBarLayout;
    private final ImageView appIcon;

    private final SpannableString composedTitle1;
    private final SpannableString composedTitle2;

    // ctor
    ViewHeader(@NonNull Context context, @NonNull View view) {
      this.context = context;
      composedTitle1 = new SpannableString(
          view.getResources().getString(R.string.other_versions_partial_title_1));
      composedTitle1.setSpan(new StyleSpan(Typeface.ITALIC), 0, composedTitle1.length(), 0);

      composedTitle2 = new SpannableString(
          view.getResources().getString(R.string.other_versions_partial_title_2));
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
                appIcon.animate().alpha(1F).start();
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
                appIcon.animate().alpha(0F).start();
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
      ImageLoader.with(context).load(imgUrl, appIcon);
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
