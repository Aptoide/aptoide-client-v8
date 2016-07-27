/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SelectableDisplayablePojo;

/**
 * created by SithEngineer
 */
public class ScheduledDownloadDisplayable extends SelectableDisplayablePojo<Scheduled> {

	public ScheduledDownloadDisplayable() {
	}

	public ScheduledDownloadDisplayable(Scheduled pojo) {
		super(pojo);
	}

	public ScheduledDownloadDisplayable(Scheduled pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.SCHEDULED_DOWNLOAD;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_scheduled_download_row;
	}
}
