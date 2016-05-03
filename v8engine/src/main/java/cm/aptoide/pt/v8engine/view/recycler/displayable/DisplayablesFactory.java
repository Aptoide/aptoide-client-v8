/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;

/**
 * Created by neuro on 01-05-2016.
 */
public class DisplayablesFactory {

	public static List<Displayable> parse(GetStoreWidgets getStoreWidgets) {

		LinkedList<Displayable> displayables = new LinkedList<>();

		List<GetStoreWidgets.WSWidget> wsWidgetList = getStoreWidgets.getDatalist().getList();

		for (GetStoreWidgets.WSWidget wsWidget : wsWidgetList) {
			switch (wsWidget.getType()) {

				case APPS_GROUP:
					displayables.add(getApps(wsWidget.getViewObject()));
					break;

				case STORES_GROUP:
					displayables.add(getStores(wsWidget.getViewObject()));
					break;

				case DISPLAYS:
					displayables.add(getDisplays(wsWidget.getViewObject()));
					break;

				case HEADER_ROW:
					displayables.add(getHeader(wsWidget.getViewObject()));
					break;
			}
		}

		// FIXME remove this lines. for debug only
		if(cm.aptoide.pt.v8engine.BuildConfig.DEBUG) {
			GetStoreWidgets.WSWidget header1 = new GetStoreWidgets.WSWidget();
			header1.setTitle("header 1");
			displayables.addFirst(getHeader(header1));

			GetStoreWidgets.WSWidget header2 = new GetStoreWidgets.WSWidget();
			header2.setTitle("header 2");
			displayables.addLast(getHeader(header2));
		}

		return displayables;
	}

	private static Displayable getApps(Object viewObject) {
		ListApps listApps = (ListApps) viewObject;
		List<App> apps = listApps.getDatalist().getList();
		List<Displayable> tmp = new ArrayList<>(apps.size());
		// Todo: row
		for (App app : apps) {
			DisplayablePojo<App> diplayable = (DisplayablePojo<App>) DisplayableLoader.INSTANCE
					.newDisplayable(GetStoreWidgets.Type.APPS_GROUP
					.name());
			diplayable.setPojo(app);
			tmp.add(diplayable);
		}
		return new DisplayableGroup(tmp);
	}

	private static Displayable getStores(Object viewObject) {
		ListStores listStores = (ListStores) viewObject;
		List<Store> stores = listStores.getDatalist().getList();
		List<Displayable> tmp = new ArrayList<>(stores.size());
		for (Store store : stores) {
			DisplayablePojo<Store> diplayable = (DisplayablePojo<Store>) DisplayableLoader
					.INSTANCE.newDisplayable(GetStoreWidgets.Type.STORES_GROUP
					.name());
			diplayable.setPojo(store);
			tmp.add(diplayable);
		}
		return new DisplayableGroup(tmp);
	}

	private static Displayable getDisplays(Object viewObject) {
		// TODO
		return null;
	}

	private static Displayable getHeader(Object viewObject) {
		GetStoreWidgets.WSWidget header = (GetStoreWidgets.WSWidget) viewObject;
		DisplayablePojo<GetStoreWidgets.WSWidget> displayable = (DisplayablePojo<GetStoreWidgets
				.WSWidget>) DisplayableLoader.INSTANCE
				.newDisplayable(GetStoreWidgets.Type.HEADER_ROW.name());
		displayable.setPojo(header);
		return displayable;
	}
}
