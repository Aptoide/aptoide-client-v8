/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 20-04-2016.
 */
//@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseV7EndlessResponse extends BaseV7Response {

	protected static final int NEXT_STEP = 10;

	private final boolean stableTotal;

	public BaseV7EndlessResponse(){ this(true); }

	public BaseV7EndlessResponse(boolean stableTotal) {
		this.stableTotal = stableTotal;
	}

	public abstract int getTotal();
	public abstract int getNextSize();
	public abstract boolean hasData();

	public boolean hasStableTotal() {
		return stableTotal;
	}
}
