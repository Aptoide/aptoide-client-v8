/*
 * Copyright (c) 2016.
 * Modified on 28/07/2016.
 */

package cm.aptoide.pt.view.recycler.displayable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Pair;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.MyStoreManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.LoginDisplayable;
import cm.aptoide.pt.account.view.user.CreateStoreDisplayable;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.app.view.GridAppDisplayable;
import cm.aptoide.pt.app.view.GridAppListDisplayable;
import cm.aptoide.pt.app.view.OfficialAppDisplayable;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.ListComments;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.ListStores;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.MyStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.store.view.BadgeDialogFactory;
import cm.aptoide.pt.store.view.GridDisplayDisplayable;
import cm.aptoide.pt.store.view.GridStoreDisplayable;
import cm.aptoide.pt.store.view.GridStoreMetaDisplayable;
import cm.aptoide.pt.store.view.StoreAddCommentDisplayable;
import cm.aptoide.pt.store.view.StoreGridHeaderDisplayable;
import cm.aptoide.pt.store.view.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.store.view.StoreTabNavigator;
import cm.aptoide.pt.store.view.featured.AppBrickDisplayable;
import cm.aptoide.pt.store.view.my.StoreDisplayable;
import cm.aptoide.pt.store.view.recommended.RecommendedStoreDisplayable;
import cm.aptoide.pt.themes.StoreTheme;
import cm.aptoide.pt.themes.ThemeManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 01-05-2016.
 */
public class DisplayablesFactory {
  public static Observable<Displayable> parse(String marketName, GetStoreWidgets.WSWidget widget,
      String storeTheme, RoomStoreRepository storeRepository,
      StoreCredentialsProvider storeCredentials, StoreContext storeContext, Context context,
      AptoideAccountManager accountManager, StoreUtilsProxy storeUtilsProxy,
      WindowManager windowManager, Resources resources, InstalledRepository installedRepository,
      StoreAnalytics storeAnalytics, StoreTabNavigator storeTabNavigator,
      NavigationTracker navigationTracker, BadgeDialogFactory badgeDialogFactory,
      FragmentNavigator fragmentNavigator, BodyInterceptor<BaseBody> bodyInterceptorV7,
      OkHttpClient client, Converter.Factory converter, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, ThemeManager themeManager) {

    LinkedList<Displayable> displayables = new LinkedList<>();

    // Unknown types are null
    if (widget.getType() != null && widget.getViewObject() != null) {
      switch (widget.getType()) {

        case APPS_GROUP:
          return Observable.just(getApps(marketName, widget, storeTheme, storeContext,
              context.getApplicationContext()
                  .getResources(), windowManager, storeTabNavigator, navigationTracker,
              themeManager));

        case MY_STORES_SUBSCRIBED:
          return getMyStores(marketName, widget, storeRepository, storeTheme, storeContext,
              windowManager, resources, context, storeAnalytics, storeTabNavigator,
              navigationTracker, themeManager);

        case STORES_GROUP:
          return Observable.just(
              getStores(marketName, widget, storeTheme, storeContext, windowManager, resources,
                  context, storeAnalytics, storeTabNavigator, navigationTracker, themeManager));

        case DISPLAYS:
          return Observable.just(
              getDisplays(widget, storeTheme, storeContext, windowManager, resources,
                  installedRepository));

        case ADS:
          List<Displayable> adsList = getAds(widget, new MinimalAdMapper(), navigationTracker);
          if (adsList.size() > 0) {
            DisplayableGroup ads = new DisplayableGroup(adsList, windowManager, resources);
            // Header hammered
            LinkedList<GetStoreWidgets.WSWidget.Action> actions = new LinkedList<>();
            actions.add(new GetStoreWidgets.WSWidget.Action().setEvent(
                new Event().setName(Event.Name.getAds)));
            widget.setActions(actions);
            StoreGridHeaderDisplayable storeGridHeaderDisplayable =
                new StoreGridHeaderDisplayable(marketName, widget, null, widget.getTag(),
                    StoreContext.meta, storeTabNavigator, navigationTracker,
                    themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data);
            displayables.add(storeGridHeaderDisplayable);
            displayables.add(ads);
            return Observable.from(displayables);
          } else {
            return Observable.empty();
          }

        case HOME_META:
          return Observable.just(
              new GridStoreMetaDisplayable((GetHomeMeta) widget.getViewObject(), storeCredentials,
                  storeAnalytics, badgeDialogFactory, fragmentNavigator, storeRepository,
                  bodyInterceptorV7, client, converter, tokenInvalidator, sharedPreferences,
                  themeManager, storeUtilsProxy, accountManager));

        case MY_STORE_META:
          return Observable.from(
              createMyStoreDisplayables(widget.getViewObject(), storeAnalytics, storeContext,
                  accountManager, context, storeTheme, themeManager));

        case STORE_META:
          return Observable.from(
              createStoreDisplayables(widget.getViewObject(), storeContext, storeTheme,
                  themeManager));

        case STORES_RECOMMENDED:
          return Observable.just(
              createRecommendedStores(marketName, widget, storeTheme, storeRepository,
                  storeCredentials, storeContext, context, accountManager, storeUtilsProxy,
                  windowManager, resources, storeTabNavigator, navigationTracker, themeManager));

        case COMMENTS_GROUP:
          return Observable.from(
              createCommentsGroup(marketName, widget, themeManager.getStoreTheme(storeTheme),
                  widget.getTag(), storeContext, storeTabNavigator, navigationTracker,
                  themeManager));
      }
    }
    return Observable.empty();
  }

  private static Displayable getApps(String marketName, GetStoreWidgets.WSWidget wsWidget,
      String storeTheme, StoreContext storeContext, Resources resources,
      WindowManager windowManager, StoreTabNavigator storeTabNavigator,
      NavigationTracker navigationTracker, ThemeManager themeManager) {
    ListApps listApps = (ListApps) wsWidget.getViewObject();
    if (listApps == null) {
      return new EmptyDisplayable();
    }

    List<App> apps = listApps.getDataList()
        .getList();
    List<Displayable> displayables = new ArrayList<>(apps.size());

    for (App app : apps) {
      app.getStore()
          .setAppearance(new Store.Appearance(storeTheme, null));
    }

    if (Layout.BRICK.equals(wsWidget.getData()
        .getLayout())) {
      if (apps.size() > 0) {

        boolean useBigBrick = resources.getBoolean(R.bool.use_big_app_brick);

        int nrAppBricks = resources.getInteger(R.integer.nr_small_app_bricks);

        nrAppBricks = Math.min(nrAppBricks, apps.size());

        if (apps.size() == 1) {
          useBigBrick = true;
        } else if (apps.size() == 2) {
          useBigBrick = false;
        }

        if (useBigBrick) {
          displayables.add(new AppBrickDisplayable(apps.get(0), wsWidget.getTag(),
              navigationTracker).setFullRow());

          nrAppBricks++;
        }

        if (apps.size() > 1) {
          for (int i = (useBigBrick ? 1 : 0); i < nrAppBricks; i++) {
            Displayable appDisplayablePojo =
                new AppBrickDisplayable(apps.get(i), wsWidget.getTag(), navigationTracker);
            displayables.add(appDisplayablePojo);
          }
        }
      }
    } else if (Layout.LIST.equals(wsWidget.getData()
        .getLayout())) {
      if (apps.size() > 0) {
        displayables.add(new StoreGridHeaderDisplayable(marketName, wsWidget, storeTabNavigator,
            navigationTracker,
            themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data));
      }

      for (App app : apps) {
        displayables.add(new GridAppListDisplayable(app, wsWidget.getTag()));
      }
    } else {
      if (apps.size() > 0) {
        displayables.add(
            new StoreGridHeaderDisplayable(marketName, wsWidget, storeTheme, wsWidget.getTag(),
                storeContext, storeTabNavigator, navigationTracker,
                themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data));
      }

      for (App app : apps) {
        DisplayablePojo<App> diplayable =
            new GridAppDisplayable(app, wsWidget.getTag(), navigationTracker, storeContext);
        displayables.add(diplayable);
      }
    }
    return new DisplayableGroup(displayables, windowManager, resources);
  }

  private static Observable<Displayable> getMyStores(String marketName,
      GetStoreWidgets.WSWidget wsWidget, RoomStoreRepository storeRepository, String storeTheme,
      StoreContext storeContext, WindowManager windowManager, Resources resources, Context context,
      StoreAnalytics storeAnalytics, StoreTabNavigator storeTabNavigator,
      NavigationTracker navigationTracker, ThemeManager themeManager) {
    return loadLocalSubscribedStores(storeRepository).map(stores -> {
      List<Displayable> tmp = new ArrayList<>(stores.size());
      int maxStoresToShow = stores.size();
      if (wsWidget.getViewObject() instanceof ListStores) {
        ListStores listStores = (ListStores) wsWidget.getViewObject();
        stores.addAll(listStores.getDataList()
            .getList());
        maxStoresToShow = listStores.getDataList()
            .getLimit() > stores.size() ? stores.size() : listStores.getDataList()
            .getLimit();
      }
      Collections.sort(stores, (store, t1) -> store.getName()
          .compareTo(t1.getName()));
      for (int i = 0; i < stores.size() && tmp.size() < maxStoresToShow; i++) {
        if (i == 0
            || stores.get(i - 1)
            .getId() != stores.get(i)
            .getId()) {
          GridStoreDisplayable diplayable =
              new GridStoreDisplayable(stores.get(i), "Open a Followed Store", storeAnalytics);
          tmp.add(diplayable);
        }
      }
      if (tmp.size() > 0) {
        StoreGridHeaderDisplayable header =
            new StoreGridHeaderDisplayable(marketName, wsWidget, storeTheme, wsWidget.getTag(),
                storeContext, storeTabNavigator, navigationTracker,
                themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data);
        if (stores.size() <= maxStoresToShow) {
          header.getModel()
              .setMoreVisible(false);
        }
        tmp.add(0, header);
      }
      return new DisplayableGroup(tmp, windowManager, resources);
    });
  }

  private static Displayable getStores(String marketName, GetStoreWidgets.WSWidget wsWidget,
      String storeTheme, StoreContext storeContext, WindowManager windowManager,
      Resources resources, Context context, StoreAnalytics storeAnalytics,
      StoreTabNavigator storeTabNavigator, NavigationTracker navigationTracker,
      ThemeManager themeManager) {
    Object viewObject = wsWidget.getViewObject();
    ListStores listStores = (ListStores) viewObject;
    if (listStores == null) {
      return new EmptyDisplayable();
    }
    List<Store> stores = listStores.getDataList()
        .getList();
    List<Displayable> tmp = new ArrayList<>(stores.size());
    tmp.add(new StoreGridHeaderDisplayable(marketName, wsWidget, storeTheme, wsWidget.getTag(),
        storeContext, storeTabNavigator, navigationTracker,
        themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data));
    for (Store store : stores) {

      GridStoreDisplayable diplayable = new GridStoreDisplayable(store, "Home", storeAnalytics);
      tmp.add(diplayable);
    }
    return new DisplayableGroup(tmp, windowManager, resources);
  }

  private static Displayable getDisplays(GetStoreWidgets.WSWidget wsWidget, String storeTheme,
      StoreContext storeContext, WindowManager windowManager, Resources resources,
      InstalledRepository installedRepository) {
    GetStoreDisplays getStoreDisplays = (GetStoreDisplays) wsWidget.getViewObject();
    if (getStoreDisplays == null) {
      return new EmptyDisplayable();
    }
    List<GetStoreDisplays.EventImage> getStoreDisplaysList = getStoreDisplays.getList();
    List<Displayable> tmp = new ArrayList<>(getStoreDisplaysList.size());

    for (GetStoreDisplays.EventImage eventImage : getStoreDisplaysList) {
      DisplayablePojo<GetStoreDisplays.EventImage> displayablePojo =
          new GridDisplayDisplayable(eventImage, storeTheme, wsWidget.getTag(), storeContext,
              installedRepository);

      Event.Name name = displayablePojo.getPojo()
          .getEvent()
          .getName();
      if (Event.Name.facebook.equals(name)
          || Event.Name.twitch.equals(name)
          || Event.Name.youtube.equals(name)) {
        displayablePojo.setFullRow();
      }
      tmp.add(displayablePojo);
    }
    return new DisplayableGroup(tmp, windowManager, resources);
  }

  private static @NonNull List<Displayable> getAds(GetStoreWidgets.WSWidget wsWidget,
      MinimalAdMapper adMapper, NavigationTracker navigationTracker) {
    GetAdsResponse getAdsResponse = (GetAdsResponse) wsWidget.getViewObject();
    if (getAdsResponse != null
        && getAdsResponse.getAds() != null
        && getAdsResponse.getAds()
        .size() > 0) {
      List<GetAdsResponse.Ad> ads = getAdsResponse.getAds();
      List<Displayable> tmp = new ArrayList<>(ads.size());
      for (GetAdsResponse.Ad ad : ads) {

        GridAdDisplayable diplayable =
            new GridAdDisplayable(adMapper.map(ad), wsWidget.getTag(), navigationTracker);
        tmp.add(diplayable);
      }
      return tmp;
    }

    return Collections.emptyList();
  }

  private static List<Displayable> createMyStoreDisplayables(Object viewObject,
      StoreAnalytics storeAnalytics, StoreContext storeContext,
      AptoideAccountManager accountManager, Context context, String storeTheme,
      ThemeManager themeManager) {
    LinkedList<Displayable> displayables = new LinkedList<>();

    if (viewObject instanceof MyStore) {
      MyStore store = (MyStore) viewObject;
      if (!store.isCreateStore()) {
        TimelineStats.StatusData followerStats = store.getTimelineStats()
            .getData();
        displayables.add(new StoreDisplayable(store.getGetHomeMeta()
            .getData()
            .getStore(), storeContext, followerStats.getFollowing(), followerStats.getFollowers(),
            R.string.storetab_short_followings, R.string.storetab_short_followers, true,
            getStoreDescriptionMessage(context, store.getGetHomeMeta()
                .getData()
                .getStore()),
            themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data));
      } else if (accountManager.isLoggedIn()) {
        if (MyStoreManager.shouldShowCreateStore()) {
          displayables.add(new CreateStoreDisplayable(storeAnalytics, store.getTimelineStats(),
              themeManager.getAttributeForTheme(R.attr.themeTextColor).data));
        }
      } else {
        displayables.add(new LoginDisplayable());
      }
    }
    return displayables;
  }

  private static String getStoreDescriptionMessage(Context context, Store store) {
    String message;
    if (TextUtils.isEmpty(store.getAppearance()
        .getDescription())) {
      message = context.getString(R.string.create_store_displayable_empty_description_message);
    } else {
      message = store.getAppearance()
          .getDescription();
    }
    return message;
  }

  private static List<Displayable> createStoreDisplayables(Object viewObject,
      StoreContext storeContext, String storeTheme, ThemeManager themeManager) {
    ArrayList<Displayable> displayables = new ArrayList<>();
    if (viewObject instanceof GetStoreMeta) {
      Store store = ((GetStoreMeta) viewObject).getData();
      displayables.add(new StoreDisplayable(store, storeContext, store.getStats()
          .getApps(), store.getStats()
          .getDownloads(), R.string.storehometab_short_apps, R.string.storehometab_short_downloads,
          false, store.getAppearance()
          .getDescription(),
          themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data));
    }
    return displayables;
  }

  private static Displayable createRecommendedStores(String marketName,
      GetStoreWidgets.WSWidget wsWidget, String storeTheme, RoomStoreRepository storeRepository,
      StoreCredentialsProvider storeCredentials, StoreContext storeContext, Context context,
      AptoideAccountManager accountManager, StoreUtilsProxy storeUtilsProxy,
      WindowManager windowManager, Resources resources, StoreTabNavigator storeTabNavigator,
      NavigationTracker navigationTracker, ThemeManager themeManager) {
    ListStores listStores = (ListStores) wsWidget.getViewObject();
    if (listStores == null) {
      return new EmptyDisplayable();
    }
    List<Store> stores = listStores.getDataList()
        .getList();
    List<Displayable> displayables = new LinkedList<>();
    displayables.add(
        new StoreGridHeaderDisplayable(marketName, wsWidget, storeTheme, wsWidget.getTag(),
            storeContext, storeTabNavigator, navigationTracker,
            themeManager.getAttributeForTheme(storeTheme, R.attr.themeTextColor).data));
    for (Store store : stores) {
      if (wsWidget.getData()
          .getLayout() == Layout.LIST) {
        displayables.add(
            new RecommendedStoreDisplayable(store, storeRepository, accountManager, storeUtilsProxy,
                storeCredentials));
      } else {
        displayables.add(new GridStoreDisplayable(store));
      }
    }

    return new DisplayableGroup(displayables, windowManager, resources);
  }

  private static List<Displayable> createCommentsGroup(String marketName,
      GetStoreWidgets.WSWidget wsWidget, StoreTheme storeTheme, String tag,
      StoreContext storeContext, StoreTabNavigator storeTabNavigator,
      NavigationTracker navigationTracker, ThemeManager themeManager) {
    List<Displayable> displayables = new LinkedList<>();

    Pair<ListComments, BaseRequestWithStore.StoreCredentials> data =
        (Pair<ListComments, BaseRequestWithStore.StoreCredentials>) wsWidget.getViewObject();
    ListComments comments = data.first;
    displayables.add(
        new StoreGridHeaderDisplayable(marketName, wsWidget, storeTheme.getThemeName(), tag,
            storeContext, storeTabNavigator, navigationTracker,
            themeManager.getAttributeForTheme(storeTheme.getThemeName(),
                R.attr.themeTextColor).data));
    if (comments != null
        && comments.getDataList() != null
        && comments.getDataList()
        .getList()
        .size() > 0) {
      displayables.add(
          new StoreLatestCommentsDisplayable(data.second.getId(), data.second.getName(),
              comments.getDataList()
                  .getList(), themeManager));
    } else {
      displayables.add(
          new StoreAddCommentDisplayable(data.second.getId(), data.second.getName(), storeTheme,
              themeManager.getAttributeForTheme(storeTheme.getThemeName(),
                  R.attr.raisedButtonSecondaryBackground).resourceId));
    }

    return displayables;
  }

  public static Observable<List<Store>> loadLocalSubscribedStores(
      RoomStoreRepository storeRepository) {
    return storeRepository.getAll()
        .first()
        .observeOn(Schedulers.computation())
        .flatMap(stores -> Observable.from(stores)
            .map(store -> {
              Store nwStore = new Store();
              nwStore.setName(store.getStoreName());
              nwStore.setId(store.getStoreId());
              nwStore.setAvatar(store.getIconPath());
              Store.Appearance appearance = new Store.Appearance();
              appearance.setTheme(store.getTheme());
              nwStore.setAppearance(appearance);
              return nwStore;
            })
            .toList());
  }
}
