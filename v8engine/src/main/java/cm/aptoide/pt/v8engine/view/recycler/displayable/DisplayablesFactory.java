/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableType;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridHeaderDisplayable;

/**
 * Created by neuro on 01-05-2016.
 */
public class DisplayablesFactory {

	public static List<Displayable> parse(GetStoreWidgets getStoreWidgets) {

		LinkedList<Displayable> displayables = new LinkedList<>();

		List<GetStoreWidgets.WSWidget> wsWidgetList = getStoreWidgets.getDatalist().getList();

		for (GetStoreWidgets.WSWidget wsWidget : wsWidgetList) {
			// Unknows types are null
			if (wsWidget.getType() != null) {
				switch (wsWidget.getType()) {

					case APPS_GROUP:
						displayables.add(getApps(wsWidget));
						break;

					case STORES_GROUP:
						displayables.add(new GridHeaderDisplayable(wsWidget));
						displayables.add(getStores(wsWidget.getViewObject()));
						break;

					case DISPLAYS:
						displayables.add(getDisplays(wsWidget));
						break;
				}
			}
		}

		// FIXME remove this lines. for debug only
		/*
		if(cm.aptoide.pt.v8engine.BuildConfig.DEBUG) {
			GetStoreWidgets.WSWidget header1 = new GetStoreWidgets.WSWidget();
			header1.setTitle("header 1");
			displayables.addFirst(getHeader(header1));

			GetStoreWidgets.WSWidget header2 = new GetStoreWidgets.WSWidget();
			header2.setTitle("header 2");
			displayables.addLast(getHeader(header2));
		}
		*/

		return displayables;
	}

	private static Displayable getApps(GetStoreWidgets.WSWidget wsWidget) {
		ListApps listApps = (ListApps) wsWidget.getViewObject();
		List<App> apps = listApps.getDatalist().getList();
		List<Displayable> tmp = new ArrayList<>(apps.size());

		if (Layout.BRICK.equals(wsWidget.getData().getLayout())) {
			if (apps.size() > 0) {
				tmp.add(DisplayableType.newDisplayable(Type.APP_BRICK, apps.get(0))
						.setDefaultPerLineCount(1));

				for (int i = 1; i < apps.size(); i++) {
					DisplayablePojo<App> appDisplayablePojo = (DisplayablePojo<App>)
							DisplayableType

							.newDisplayable(Type.APP_BRICK, apps
							.get(i));

					tmp.add(appDisplayablePojo);
				}

				tmp.add(new FooterDisplayable(wsWidget));
			}
		} else {
			if (apps.size() > 0) {
				tmp.add(new GridHeaderDisplayable(wsWidget));
			}

			for (App app : apps) {
				DisplayablePojo<App> diplayable = (DisplayablePojo<App>) DisplayableType
						.newDisplayable((wsWidget
						.getType()));
				diplayable.setPojo(app);
				tmp.add(diplayable);
			}
		}
		return new DisplayableGroup(tmp);
	}

	private static Displayable getStores(Object viewObject) {
		ListStores listStores = (ListStores) viewObject;
		List<Store> stores = listStores.getDatalist().getList();
		List<Displayable> tmp = new ArrayList<>(stores.size());
		for (Store store : stores) {

			DisplayablePojo<Store> diplayable = (DisplayablePojo<Store>) DisplayableType
					.newDisplayable(Type.STORES_GROUP);
			diplayable.setPojo(store);
			tmp.add(diplayable);
		}
		return new DisplayableGroup(tmp);
	}

	private static Displayable getDisplays(GetStoreWidgets.WSWidget wsWidget) {
		GetStoreDisplays getStoreDisplays = (GetStoreDisplays) wsWidget.getViewObject();
		List<GetStoreDisplays.EventImage> getStoreDisplaysList = getStoreDisplays.getList();
		List<Displayable> tmp = new ArrayList<>(getStoreDisplaysList.size());

		for (GetStoreDisplays.EventImage eventImage : getStoreDisplaysList) {
			DisplayablePojo<GetStoreDisplays.EventImage> diplayable = (DisplayablePojo
					<GetStoreDisplays.EventImage>) DisplayableType
					.newDisplayable(wsWidget.getType());
			diplayable.setPojo(eventImage);
			tmp.add(diplayable);
		}
		return new DisplayableGroup(tmp);
	}

	private static Displayable getHeader(Object viewObject) {
		GetStoreWidgets.WSWidget header = (GetStoreWidgets.WSWidget) viewObject;
		DisplayablePojo<GetStoreWidgets.WSWidget> displayable = (DisplayablePojo<GetStoreWidgets
				.WSWidget>) DisplayableType
				.newDisplayable(Type.HEADER_ROW);
		displayable.setPojo(header);
		return displayable;
	}
}
