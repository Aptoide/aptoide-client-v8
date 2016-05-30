/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by neuro on 17-05-2016.
 */
public class UpdateDisplayable extends DisplayablePojo<Update> {

	public UpdateDisplayable() {
	}

	public UpdateDisplayable(Update pojo) {
		super(pojo);
	}

	@Override
	public Type getType() {
		return Type.UPDATE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.update_row;
	}
}
