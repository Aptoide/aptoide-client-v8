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

		ArrayList<Displayable> tmp = new ArrayList<>();

		for (GetStoreWidgets.WSWidget wsWidget : wsWidgetList) {
			tmp.clear();
			switch (wsWidget.getType()) {

				case APPS_GROUP:
					ListApps listApps = (ListApps) wsWidget.getViewObject();
					List<App> apps = listApps.getDatalist().getList();
					tmp.ensureCapacity(apps.size());
					// Todo: row
					for (App app : apps) {
						DisplayablePojo<App> diplayable =
								(DisplayablePojo<App>) DisplayableLoader.INSTANCE
									.newDisplayable(GetStoreWidgets.Type.APPS_GROUP.name());
						diplayable.setPojo(app);
						tmp.add(diplayable);
					}
					displayables.add(new DisplayableGroup(tmp));

					break;

				case STORES_GROUP:
					ListStores listStores = (ListStores) wsWidget.getViewObject();
					List<Store> stores = listStores.getDatalist().getList();
					tmp.ensureCapacity(stores.size());
					for (Store store : stores) {
						DisplayablePojo<Store> diplayable =
								(DisplayablePojo<Store>) DisplayableLoader.INSTANCE
										.newDisplayable(GetStoreWidgets.Type.STORES_GROUP.name());
						diplayable.setPojo(store);
						tmp.add(diplayable);
					}
					displayables.add(new DisplayableGroup(tmp));
					break;

				case DISPLAYS:
					//todo
					break;

				case HEADER_ROW:
					GetStoreWidgets.WSWidget header = (GetStoreWidgets.WSWidget) wsWidget.getViewObject();
					DisplayablePojo<GetStoreWidgets.WSWidget> displayable =
							(DisplayablePojo<GetStoreWidgets.WSWidget>) DisplayableLoader.INSTANCE
									.newDisplayable(GetStoreWidgets.Type.HEADER_ROW.name());
					displayable.setPojo(header);
					displayables.add(displayable);
					break;
			}
		}

		return displayables;
	}
}
