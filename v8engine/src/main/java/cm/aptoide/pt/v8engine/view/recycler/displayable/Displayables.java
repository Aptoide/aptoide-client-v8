/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 18-04-2016.
 */
public class Displayables {

	private final List<Displayable> displayables = new LinkedList<>();

	public Displayables() {
	}

	public void add(Collection<? extends Displayable> collection) {
		for (Displayable displayable : collection) {
			if (displayable instanceof DisplayableGroup) {
				add(((DisplayableGroup) displayable).getChildren());
			} else {
				displayables.add(displayable);
			}
		}
	}

	public Displayable get(Integer position) {
		return displayables.get(position);
	}

	public int size() {
		return displayables.size();
	}

	public void clear() {
		displayables.clear();
	}
}
