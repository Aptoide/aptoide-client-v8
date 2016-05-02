/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.AppGridDisplayable;

/**
 * Created by neuro on 01-05-2016.
 */
public class DisplayablesFactory {

	public static List<Displayable> parse(GetStoreWidgets getStoreWidgets) {

		LinkedList<Displayable> displayables = new LinkedList<>();

		List<GetStoreWidgets.WSWidget> wsWidgetList = getStoreWidgets.getDatalist().getList();

		for (GetStoreWidgets.WSWidget wsWidget : wsWidgetList) {

			LinkedList<Displayable> tmp = new LinkedList<>();

			switch (wsWidget.getType()) {
				case APPS_GROUP:
					ListApps listApps = (ListApps) wsWidget.getViewObject();
					List<App> list = listApps.getDatalist().getList();

					// Todo: row
					for (App app : list) {
						tmp.add(new AppGridDisplayable(app));
					}
					displayables.add(new DisplayableGroup(tmp));

					break;
				case STORES_GROUP:
					//todo
					break;
				case DISPLAYS:
					//todo
					break;
				case HEADER_ROW:
					//todo
					break;
			}
		}

		return displayables;
	}
}
