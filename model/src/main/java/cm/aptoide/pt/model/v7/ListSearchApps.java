/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 26-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListSearchApps extends BaseV7Response {

	private List<SearchAppsApp> list;

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class SearchAppsApp extends App {

		private boolean hasVersions;
		private Obb obb;
	}
}
