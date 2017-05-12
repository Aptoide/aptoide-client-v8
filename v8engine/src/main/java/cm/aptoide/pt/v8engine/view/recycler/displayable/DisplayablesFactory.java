/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreThemeEnum;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.account.user.CreateStoreDisplayable;
import cm.aptoide.pt.v8engine.view.app.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.app.GridAppListDisplayable;
import cm.aptoide.pt.v8engine.view.app.OfficialAppDisplayable;
import cm.aptoide.pt.v8engine.view.reviews.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.store.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.store.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.store.GridStoreMetaDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreAddCommentDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.store.featured.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.store.my.MyStoreDisplayable;
import cm.aptoide.pt.v8engine.view.store.recommended.RecommendedStoreDisplayable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 01-05-2016.
 */
public class DisplayablesFactory {
  private static final String TAG = DisplayablesFactory.class.getSimpleName();

  public static Observable<Displayable> parse(GetStoreWidgets.WSWidget widget, String storeTheme,
      StoreRepository storeRepository, StoreContext storeContext, Context context,
      AptoideAccountManager accountManager, StoreUtilsProxy storeUtilsProxy) {

    LinkedList<Displayable> displayables = new LinkedList<>();

    // Unknows types are null
    if (widget.getType() != null && widget.getViewObject() != null) {
      switch (widget.getType()) {

        case APPS_GROUP:
          return Observable.just(getApps(widget, storeTheme, storeContext));

        case MY_STORES_SUBSCRIBED:
          return getMyStores(widget, storeRepository, storeTheme, storeContext);

        case STORES_GROUP:
          return Observable.just(getStores(widget, storeTheme, storeContext));

        case DISPLAYS:
          return Observable.just(getDisplays(widget, storeTheme, storeContext));

        case ADS:
          List<Displayable> adsList = getAds(widget);
          if (adsList.size() > 0) {
            DisplayableGroup ads = new DisplayableGroup(adsList);
            // Header hammered
            LinkedList<GetStoreWidgets.WSWidget.Action> actions = new LinkedList<>();
            actions.add(new GetStoreWidgets.WSWidget.Action().setEvent(
                new Event().setName(Event.Name.getAds)));
            widget.setActions(actions);
            StoreGridHeaderDisplayable storeGridHeaderDisplayable =
                new StoreGridHeaderDisplayable(widget, null, widget.getTag(), StoreContext.meta);
            displayables.add(storeGridHeaderDisplayable);
            displayables.add(ads);
            return Observable.from(displayables);
          } else {
            return Observable.empty();
          }

        case HOME_META:
          return Observable.just(new GridStoreMetaDisplayable((GetHomeMeta) widget.getViewObject(),
              new StoreCredentialsProviderImpl()));

        case REVIEWS_GROUP:
          return Observable.from(createReviewsGroupDisplayables(widget));

        case MY_STORE_META:
          return Observable.from(createMyStoreDisplayables(widget.getViewObject()));

        case STORES_RECOMMENDED:
          return Observable.just(
              createRecommendedStores(widget, storeTheme, storeRepository, storeContext, context,
                  accountManager, storeUtilsProxy));

        case COMMENTS_GROUP:
          return Observable.from(createCommentsGroup(widget));

        case APP_META:
          GetStoreWidgets.WSWidget.Data dataObj = widget.getData();
          String message = dataObj.getMessage();
          return Observable.just(
              new OfficialAppDisplayable(new Pair<>(message, (GetApp) widget.getViewObject())));
      }
    }
    return Observable.empty();
  }

  private static Displayable getApps(GetStoreWidgets.WSWidget wsWidget, String storeTheme,
      StoreContext storeContext) {
    ListApps listApps = (ListApps) wsWidget.getViewObject();
    if (listApps == null) {
      return new EmptyDisplayable();
    }

    List<App> apps = listApps.getDatalist()
        .getList();
    List<Displayable> displayables = new ArrayList<>(apps.size());

    for (App app : apps) {
      app.getStore()
          .setAppearance(new Store.Appearance(storeTheme, null));
    }

    if (Layout.BRICK.equals(wsWidget.getData()
        .getLayout())) {
      if (apps.size() > 0) {

        boolean useBigBrick = V8Engine.getContext()
            .getResources()
            .getBoolean(R.bool.use_big_app_brick);

        int nrAppBricks = V8Engine.getContext()
            .getResources()
            .getInteger(R.integer.nr_small_app_bricks);

        nrAppBricks = Math.min(nrAppBricks, apps.size());

        if (apps.size() == 1) {
          useBigBrick = true;
        } else if (apps.size() == 2) {
          useBigBrick = false;
        }

        if (useBigBrick) {
          displayables.add(new AppBrickDisplayable(apps.get(0), wsWidget.getTag()).setFullRow());

          nrAppBricks++;
        }

        if (apps.size() > 1) {
          for (int i = (useBigBrick ? 1 : 0); i < nrAppBricks; i++) {
            Displayable appDisplayablePojo =
                new AppBrickDisplayable(apps.get(i), wsWidget.getTag());
            displayables.add(appDisplayablePojo);
          }
        }
        displayables.add(new FooterDisplayable(wsWidget, wsWidget.getTag(), storeContext));
      }
    } else if (Layout.LIST.equals(wsWidget.getData()
        .getLayout())) {
      if (apps.size() > 0) {
        displayables.add(new StoreGridHeaderDisplayable(wsWidget));
      }

      for (App app : apps) {
        displayables.add(new GridAppListDisplayable(app));
      }
    } else {
      if (apps.size() > 0) {
        displayables.add(
            new StoreGridHeaderDisplayable(wsWidget, storeTheme, wsWidget.getTag(), storeContext));
      }

      for (App app : apps) {
        DisplayablePojo<App> diplayable =
            new GridAppDisplayable(app, wsWidget.getTag(), storeContext == StoreContext.home);
        displayables.add(diplayable);
      }
    }
    return new DisplayableGroup(displayables);
  }

  private static Observable<Displayable> getMyStores(GetStoreWidgets.WSWidget wsWidget,
      StoreRepository storeRepository, String storeTheme, StoreContext storeContext) {
    return loadLocalSubscribedStores(storeRepository).map(stores -> {
      List<Displayable> tmp = new ArrayList<>(stores.size());
      int maxStoresToShow = stores.size();
      if (wsWidget.getViewObject() instanceof ListStores) {
        ListStores listStores = (ListStores) wsWidget.getViewObject();
        stores.addAll(listStores.getDatalist()
            .getList());
        maxStoresToShow = listStores.getDatalist()
            .getLimit() > stores.size() ? stores.size() : listStores.getDatalist()
            .getLimit();
      }
      Collections.sort(stores, (store, t1) -> store.getName()
          .compareTo(t1.getName()));
      for (int i = 0; i < stores.size() && tmp.size() < maxStoresToShow; i++) {
        if (i == 0
            || stores.get(i - 1)
            .getId() != stores.get(i)
            .getId()) {
          GridStoreDisplayable diplayable = new GridStoreDisplayable(stores.get(i));
          tmp.add(diplayable);
        }
      }
      if (tmp.size() > 0) {
        StoreGridHeaderDisplayable header =
            new StoreGridHeaderDisplayable(wsWidget, storeTheme, wsWidget.getTag(), storeContext);
        if (stores.size() <= maxStoresToShow) {
          header.setMoreVisible(false);
        }
        tmp.add(0, header);
      }
      return new DisplayableGroup(tmp);
    });
  }

  private static Displayable getStores(GetStoreWidgets.WSWidget wsWidget, String storeTheme,
      StoreContext storeContext) {
    Object viewObject = wsWidget.getViewObject();
    ListStores listStores = (ListStores) viewObject;
    if (listStores == null) {
      return new EmptyDisplayable();
    }
    List<Store> stores = listStores.getDatalist()
        .getList();
    List<Displayable> tmp = new ArrayList<>(stores.size());
    tmp.add(new StoreGridHeaderDisplayable(wsWidget, storeTheme, wsWidget.getTag(), storeContext));
    for (Store store : stores) {

      GridStoreDisplayable diplayable = new GridStoreDisplayable(store);
      tmp.add(diplayable);
    }
    return new DisplayableGroup(tmp);
  }

  private static Displayable getDisplays(GetStoreWidgets.WSWidget wsWidget, String storeTheme,
      StoreContext storeContext) {
    GetStoreDisplays getStoreDisplays = (GetStoreDisplays) wsWidget.getViewObject();
    if (getStoreDisplays == null) {
      return new EmptyDisplayable();
    }
    List<GetStoreDisplays.EventImage> getStoreDisplaysList = getStoreDisplays.getList();
    List<Displayable> tmp = new ArrayList<>(getStoreDisplaysList.size());

    for (GetStoreDisplays.EventImage eventImage : getStoreDisplaysList) {
      DisplayablePojo<GetStoreDisplays.EventImage> displayablePojo =
          new GridDisplayDisplayable(eventImage, storeTheme, wsWidget.getTag(), storeContext);

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
    return new DisplayableGroup(tmp);
  }

  private static @NonNull List<Displayable> getAds(GetStoreWidgets.WSWidget wsWidget) {
    GetAdsResponse getAdsResponse = (GetAdsResponse) wsWidget.getViewObject();
    if (getAdsResponse != null
        && getAdsResponse.getAds() != null
        && getAdsResponse.getAds()
        .size() > 0) {
      List<GetAdsResponse.Ad> ads = getAdsResponse.getAds();
      List<Displayable> tmp = new ArrayList<>(ads.size());
      for (GetAdsResponse.Ad ad : ads) {

        GridAdDisplayable diplayable = new GridAdDisplayable(MinimalAd.from(ad), wsWidget.getTag());
        tmp.add(diplayable);
      }
      return tmp;
    }

    return Collections.emptyList();
  }

  private static List<Displayable> createReviewsGroupDisplayables(
      GetStoreWidgets.WSWidget wsWidget) {
    List<Displayable> displayables = new LinkedList<>();

    ListFullReviews reviewsList = (ListFullReviews) wsWidget.getViewObject();
    if (reviewsList != null
        && reviewsList.getDatalist() != null
        && reviewsList.getDatalist()
        .getList()
        .size() > 0) {
      displayables.add(new StoreGridHeaderDisplayable(wsWidget));
      displayables.add(createReviewsDisplayables(reviewsList));
    }

    return displayables;
  }

  private static List<Displayable> createMyStoreDisplayables(Object viewObject) {
    LinkedList<Displayable> displayables = new LinkedList<>();
    if (viewObject instanceof GetHomeMeta && ((GetHomeMeta) viewObject).getData() != null) {
      displayables.add(new MyStoreDisplayable(((GetHomeMeta) viewObject)));
    } else {
      displayables.add(new CreateStoreDisplayable());
    }
    return displayables;
  }

  private static Displayable createRecommendedStores(GetStoreWidgets.WSWidget wsWidget,
      String storeTheme, StoreRepository storeRepository, StoreContext storeContext,
      Context context, AptoideAccountManager accountManager, StoreUtilsProxy storeUtilsProxy) {
    ListStores listStores = (ListStores) wsWidget.getViewObject();
    if (listStores == null) {
      return new EmptyDisplayable();
    }
    List<Store> stores = listStores.getDatalist()
        .getList();
    List<Displayable> displayables = new LinkedList<>();
    displayables.add(
        new StoreGridHeaderDisplayable(wsWidget, storeTheme, wsWidget.getTag(), storeContext));
    for (Store store : stores) {
      if (wsWidget.getData()
          .getLayout() == Layout.LIST) {
        displayables.add(
            new RecommendedStoreDisplayable(store, storeRepository, accountManager, storeUtilsProxy,
                new StoreCredentialsProviderImpl()));
      } else {
        displayables.add(new GridStoreDisplayable(store));
      }
    }

    return new DisplayableGroup(displayables);
  }

  private static List<Displayable> createCommentsGroup(GetStoreWidgets.WSWidget wsWidget) {
    List<Displayable> displayables = new LinkedList<>();

    Pair<ListComments, BaseRequestWithStore.StoreCredentials> data =
        (Pair<ListComments, BaseRequestWithStore.StoreCredentials>) wsWidget.getViewObject();
    ListComments comments = data.first;
    displayables.add(new StoreGridHeaderDisplayable(wsWidget));
    if (comments != null
        && comments.getDatalist() != null
        && comments.getDatalist()
        .getList()
        .size() > 0) {
      displayables.add(
          new StoreLatestCommentsDisplayable(data.second.getId(), data.second.getName(),
              comments.getDatalist()
                  .getList()));
    } else {
      displayables.add(new StoreAddCommentDisplayable(data.second.getId(), data.second.getName(),
          StoreThemeEnum.APTOIDE_STORE_THEME_DEFAULT));
    }

    return displayables;
  }

  public static Observable<List<Store>> loadLocalSubscribedStores(StoreRepository storeRepository) {
    return storeRepository.getAll()
        .first()
        .observeOn(Schedulers.computation())
        .flatMap(stores -> Observable.from(stores)
            .map(store -> {
              Store nwStore = new Store();
              nwStore.setName(store.getStoreName());
              nwStore.setId(store.getStoreId());
              nwStore.setAvatar(store.getIconPath());
              nwStore.setAppearance(new Store.Appearance().setTheme(store.getTheme()));
              return nwStore;
            })
            .toList());
  }

  private static Displayable createReviewsDisplayables(ListFullReviews listFullReviews) {
    List<FullReview> reviews = listFullReviews.getDatalist()
        .getList();
    final List<Displayable> displayables = new ArrayList<>(reviews.size());
    for (int i = 0; i < reviews.size(); i++) {
      displayables.add(new RowReviewDisplayable(reviews.get(i)));
    }

    return new DisplayableGroup(displayables);
  }
}
