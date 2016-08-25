/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.v8engine.interfaces.LifecycleSchim;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;

/**
 * Created by neuro on 18-04-2016.
 */
public class Displayables implements LifecycleSchim {

	private final List<Displayable> displayables = new LinkedList<>();

	public Displayables() {
	}

	public void add(int position, Displayable displayable) {
		// Ignore empty displayables
		if (displayable instanceof EmptyDisplayable) {
			return;
		}

		if (displayable instanceof DisplayableGroup) {
			add(position, ((DisplayableGroup) displayable).getChildren());
		} else {
			displayables.add(position, displayable);
		}
	}

	public void add(int position, List<? extends Displayable> collection) {
		Collections.reverse(collection);

		for (Displayable displayable : collection) {
			add(position, displayable);
		}
	}

	public void add(Displayable displayable) {
		// Ignore empty displayables
		if (displayable instanceof EmptyDisplayable) {
			return;
		}

		if (displayable instanceof DisplayableGroup) {
			add(((DisplayableGroup) displayable).getChildren());
		} else {
			displayables.add(displayable);
		}
	}

	public void add(List<? extends Displayable> collection) {
		for (Displayable displayable : collection) {
			add(displayable);
		}
	}

	public Displayable pop() {
		if (displayables.size() > 0) {
			return displayables.remove(displayables.size() - 1);
		} else {
			return null;
		}
	}

	public Displayable get(Integer position) {
		if (displayables.size() > position) {
			return displayables.get(position);
		} else {
			return null;
		}
	}

	public void remove(int pos) {
		if (pos >= 0 && pos < displayables.size()) {
			displayables.remove(pos);
		}
	}

	/**
	 * remove displayables from <code>startPos</code> startPos until the <code>endPos</code>(inclusive)
	 *
	 * @param startPos position of the first element to be removed
	 * @param endPos   position of the last element to be removed
	 */
	public int remove(int startPos, int endPos) {
		if (startPos >= 0 && startPos < size() && endPos >= startPos && endPos <= size()) {
			int numberLoops = (endPos + 1) - startPos;
			if (numberLoops == 0) {
				remove(startPos);
				return 1;
			}
			for (int i = 0 ; i < numberLoops ; i++) {
				displayables.remove(startPos);
			}
			return numberLoops;
		}
		return 0;
	}

	public int size() {
		return displayables.size();
	}

	public void clear() {
		displayables.clear();
	}

	//
	// LifecycleSchim interface
	//

	public void onResume() {
		for (final Displayable displayable : displayables) {
			displayable.onResume();
		}
	}

	public void onPause() {
		for (final Displayable displayable : displayables) {
			displayable.onPause();
		}
	}

	@Override
	public void onViewCreated() {
		for (final Displayable displayable : displayables) {
			displayable.onViewCreated();
		}
	}

	@Override
	public void onDestroyView() {
		for (final Displayable displayable : displayables) {
			displayable.onDestroyView();
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		for (final Displayable displayable : displayables) {
			displayable.onSaveInstanceState(outState);
		}
	}

	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		for (final Displayable displayable : displayables) {
			displayable.onViewStateRestored(savedInstanceState);
		}
	}
}
