/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 14/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackDisplayable extends DisplayablePojo<Rollback> {

	public RollbackDisplayable() { }
	public RollbackDisplayable(Rollback rollback) {
		super(rollback);
	}

	@Override
	public Type getType() {
		return Type.ROLLBACK;
	}

	@Override
	public int getViewLayout() {
		return R.layout.rollback_row;
	}
}
