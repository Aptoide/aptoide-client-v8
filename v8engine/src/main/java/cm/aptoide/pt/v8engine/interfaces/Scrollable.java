/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

/**
 * Created by sithengineer on 04/07/16.
 */
public interface Scrollable {

	void scroll(Position position);

	void itemAdded(int pos);

	void itemRemoved(int pos);

	void itemChanged(int pos);

	enum Position {
		FIRST,
		LAST
	}
}
