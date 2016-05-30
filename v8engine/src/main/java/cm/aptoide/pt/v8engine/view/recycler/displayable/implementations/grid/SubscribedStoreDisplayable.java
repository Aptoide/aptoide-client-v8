/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 11-05-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubscribedStoreDisplayable extends DisplayablePojo<Store> {

	public SubscribedStoreDisplayable() {
		super();
	}

	public SubscribedStoreDisplayable(Store pojo) {
		super(pojo);
	}

	@Override
	public Type getType() {
		return Type.SUBSCRIBED_STORE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_grid_store_subscribed;
	}
}
