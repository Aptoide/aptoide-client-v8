/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.ExcludedUpdate;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 15/06/16.
 */
public class ExcludedUpdateDisplayable extends DisplayablePojo<ExcludedUpdate> {

	public ExcludedUpdateDisplayable() {
	}

	public ExcludedUpdateDisplayable(ExcludedUpdate pojo) {
		super(pojo);
	}

	public ExcludedUpdateDisplayable(ExcludedUpdate pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.EXCLUDED_UPDATE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.row_excluded_update;
	}
}
