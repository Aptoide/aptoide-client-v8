/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/08/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
 */
public abstract class ActivityView extends RxAppCompatActivity implements View {

	private Presenter presenter;

	@NonNull
	@Override
	public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull Event event) {
		return RxLifecycle.bindUntilEvent(getLifecycle(), event);
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public Observable<Event> getLifecycle() {
		return lifecycle().map(event -> {
			return convertToEvent(event);
		});
	}

	@Override
	public void attachPresenter(Presenter presenter, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			presenter.restoreState(savedInstanceState);
		}
		this.presenter = presenter;
		this.presenter.present();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		presenter.saveState(outState);
		super.onSaveInstanceState(outState);
	}

	@NonNull
	private Event convertToEvent(ActivityEvent event) {
		switch (event) {
			case CREATE:
				return Event.CREATE;
			case START:
				return Event.START;
			case RESUME:
				return Event.RESUME;
			case PAUSE:
				return Event.PAUSE;
			case STOP:
				return Event.STOP;
			case DESTROY:
				return Event.DESTROY;
			default:
				throw new IllegalStateException("Unrecognized event: " + event.name());
		}
	}
}
