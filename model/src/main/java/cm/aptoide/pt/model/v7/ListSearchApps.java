/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
 */

package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 26-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListSearchApps extends BaseV7EndlessDatalistResponse<ListSearchApps.SearchAppsApp> {

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SearchAppsApp extends App {

		private boolean hasVersions;
		private Obb obb;
	}
}
