/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 18/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.grid;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.v8engine.view.recycler.widget.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.DisplayableGroup;

/**
 * Created by neuro on 18-04-2016.
 */
class Displayables {

	private final List<Displayable> displayables = new LinkedList<>();

	public Displayables() {
	}

	public void add(Collection<Displayable> collection) {
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
}
