/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableType;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreMetaDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;

/**
 * Created by neuro on 01-05-2016.
 */
public class DisplayablesFactory {

	public static List<Displayable> parse(GetStoreWidgets getStoreWidgets, String storeTheme) {

		LinkedList<Displayable> displayables = new LinkedList<>();

		List<GetStoreWidgets.WSWidget> wsWidgetList = getStoreWidgets.getDatalist().getList();

		for (GetStoreWidgets.WSWidget wsWidget : wsWidgetList) {
			// Unknows types are null
			if (wsWidget.getType() != null) {
				switch (wsWidget.getType()) {

					case APPS_GROUP:
						displayables.add(getApps(wsWidget, storeTheme));
						break;

					case STORES_GROUP:
						displayables.add(new StoreGridHeaderDisplayable(wsWidget, storeTheme));
						displayables.add(getStores(wsWidget.getViewObject()));
						break;

					case DISPLAYS:
						displayables.add(getDisplays(wsWidget, storeTheme));
						break;

					case ADS:
						Displayable ads = getAds(wsWidget.getViewObject());
						if (ads != null) {
							// Header hammered
							LinkedList<GetStoreWidgets.WSWidget.Action> actions = new LinkedList<>();
							actions.add(new GetStoreWidgets.WSWidget.Action().setEvent(new Event().setName(Event.Name.getAds)));
							wsWidget.setActions(actions);
							StoreGridHeaderDisplayable storeGridHeaderDisplayable = new StoreGridHeaderDisplayable(wsWidget);
							displayables.add(storeGridHeaderDisplayable);

							displayables.add(ads);
						}
						break;
					case STORE_META:
						displayables.add(new GridStoreMetaDisplayable((GetStoreMeta) wsWidget.getViewObject()));
						break;
					case REVIEWS_GROUP:
						ListFullReviews reviewsList = (ListFullReviews) wsWidget.getViewObject();
						if (reviewsList != null && reviewsList.getDatalist() != null && reviewsList.getDatalist().getList().size() > 0) {
							displayables.add(new StoreGridHeaderDisplayable(wsWidget));
							displayables.add(createReviewsDisplayables(reviewsList));
						}
						break;
				}
			}
		}

		return displayables;
	}

	private static DisplayableGroup createReviewsDisplayables(ListFullReviews listFullReviews) {
		List<FullReview> reviews = listFullReviews.getDatalist().getList();
		final List<Displayable> displayables = new ArrayList<>(reviews.size());
		for (int i = 0 ; i < reviews.size() ; i++) {
			FullReview review = reviews.get(i);
			displayables.add(new RowReviewDisplayable(review, false));
		}

		return new DisplayableGroup(displayables);
	}

	private static Displayable getAds(Object viewObject) {
		GetAdsResponse getAdsResponse = (GetAdsResponse) viewObject;
		if (viewObject != null) {
			List<GetAdsResponse.Ad> ads = getAdsResponse.getAds();
			List<Displayable> tmp = new ArrayList<>(ads.size());
			for (GetAdsResponse.Ad ad : ads) {

				GridAdDisplayable diplayable = (GridAdDisplayable) DisplayableType.newDisplayable(Type.ADS);
				diplayable.setPojo(ad);
				tmp.add(diplayable);
			}
			return new DisplayableGroup(tmp);
		}

		return null;
	}

	private static Displayable getApps(GetStoreWidgets.WSWidget wsWidget, String storeTheme) {
		ListApps listApps = (ListApps) wsWidget.getViewObject();
		if (listApps == null) {
			return new EmptyDisplayable();
		}

		List<App> apps = listApps.getDatalist().getList();
		List<Displayable> displayables = new ArrayList<>(apps.size());

		for (App app : apps) {
			app.getStore().setAppearance(new Store.Appearance(storeTheme, null));
		}

		if (Layout.BRICK.equals(wsWidget.getData().getLayout())) {
			if (apps.size() > 0) {

				boolean useBigBrick = V8Engine.getContext().getResources().getBoolean(R.bool.use_big_app_brick);

				int nrAppBricks = V8Engine.getContext().getResources().getInteger(R.integer.nr_small_app_bricks);

				nrAppBricks = Math.min(nrAppBricks, apps.size());

				if (useBigBrick) {
					displayables.add(DisplayableType.newDisplayable(Type.APP_BRICK, apps.get(0)).setDefaultPerLineCount(1));

					nrAppBricks++;
				}

				for (int i = (useBigBrick ? 1 : 0) ; i < nrAppBricks ; i++) {
					Displayable appDisplayablePojo = DisplayableType.newDisplayable(Type.APP_BRICK, apps.get(i));
					displayables.add(appDisplayablePojo);
				}

				displayables.add(new FooterDisplayable(wsWidget));
			}
		} else if (Layout.LIST.equals(wsWidget.getData().getLayout())) {
			if (apps.size() > 0) {
				displayables.add(new StoreGridHeaderDisplayable(wsWidget));
			}

			for (App app : apps) {
				displayables.add(DisplayableType.newDisplayable(Type.APPS_GROUP_LIST, app));
			}
		} else {
			if (apps.size() > 0) {
				displayables.add(new StoreGridHeaderDisplayable(wsWidget, storeTheme));
			}

			for (App app : apps) {
				DisplayablePojo<App> diplayable = (DisplayablePojo<App>) DisplayableType.newDisplayable((wsWidget.getType()));
				diplayable.setPojo(app);
				displayables.add(diplayable);
			}
		}
		return new DisplayableGroup(displayables);
	}

	private static Displayable getStores(Object viewObject) {
		ListStores listStores = (ListStores) viewObject;
		if (listStores == null) {
			return new EmptyDisplayable();
		}
		List<Store> stores = listStores.getDatalist().getList();
		List<Displayable> tmp = new ArrayList<>(stores.size());
		for (Store store : stores) {

			DisplayablePojo<Store> diplayable = (DisplayablePojo<Store>) DisplayableType.newDisplayable(Type.STORES_GROUP);
			diplayable.setPojo(store);
			tmp.add(diplayable);
		}
		return new DisplayableGroup(tmp);
	}

	private static Displayable getDisplays(GetStoreWidgets.WSWidget wsWidget, String storeTheme) {
		GetStoreDisplays getStoreDisplays = (GetStoreDisplays) wsWidget.getViewObject();
		if (getStoreDisplays == null) {
			return new EmptyDisplayable();
		}
		List<GetStoreDisplays.EventImage> getStoreDisplaysList = getStoreDisplays.getList();
		List<Displayable> tmp = new ArrayList<>(getStoreDisplaysList.size());

		for (GetStoreDisplays.EventImage eventImage : getStoreDisplaysList) {
			DisplayablePojo<GetStoreDisplays.EventImage> displayablePojo = new GridDisplayDisplayable(eventImage, storeTheme);

			Event.Name name = displayablePojo.getPojo().getEvent().getName();
			if (Event.Name.facebook.equals(name) || Event.Name.twitch.equals(name) || Event.Name.youtube.equals(name)) {
				displayablePojo.setDefaultPerLineCount(1);
			}
			tmp.add(displayablePojo);
		}
		return new DisplayableGroup(tmp);
	}

	private static Displayable getHeader(Object viewObject) {
		GetStoreWidgets.WSWidget header = (GetStoreWidgets.WSWidget) viewObject;
		DisplayablePojo<GetStoreWidgets.WSWidget> displayable = (DisplayablePojo<GetStoreWidgets.WSWidget>) DisplayableType.newDisplayable(Type.HEADER_ROW);
		displayable.setPojo(header);
		return displayable;
	}
}
