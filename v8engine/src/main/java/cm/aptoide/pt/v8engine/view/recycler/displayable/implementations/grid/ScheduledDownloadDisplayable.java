/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * created by SithEngineer
 */
public class ScheduledDownloadDisplayable extends DisplayablePojo<Scheduled> {

	private boolean selected;

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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
