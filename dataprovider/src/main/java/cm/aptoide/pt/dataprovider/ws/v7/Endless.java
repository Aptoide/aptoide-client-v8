/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

/**
 * Created by neuro on 07-06-2016.
 */
public interface Endless {

	int getOffset();

	void setOffset(int offset);

	int getLimit();

	static int getDefaultLimit() {
		return 10;
	}
}