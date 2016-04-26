/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

import cm.aptoide.pt.model.v7.listapp.ListAppData;
import cm.aptoide.pt.model.v7.subclasses.Obb;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 26-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListSearchApps extends BaseV7Response {

	private List<SearchAppsListAppData> list;

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SearchAppsListAppData extends ListAppData {

		private boolean hasVersions;
		private Obb obb;
	}
}
