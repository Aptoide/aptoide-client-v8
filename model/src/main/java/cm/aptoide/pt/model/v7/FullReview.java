/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;

/**
 * Created by sithengineer on 02/08/16.
 */
@Data
public class FullReview extends Review {
	//	private GetAppMeta.App data;

	private AppData data;

	@Data
	public static class AppData {

		//		private GetAppMeta.App app;
		private App app;
	}

	@Data
	public static class App {

		private long id;
		private String name;
		private String icon;
	}
}
