/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import android.content.Context;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class AppCompatActivityView extends RxAppCompatActivity implements View {

	@NonNull
	@Override
	public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull Event event) {
		return bindUntilEvent(convertFromEvent(event));
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

	private ActivityEvent convertFromEvent(Event event) {
		switch (event) {
			case CREATE:
				return ActivityEvent.CREATE;
			case START:
				return ActivityEvent.START;
			case RESUME:
				return ActivityEvent.RESUME;
			case PAUSE:
				return ActivityEvent.PAUSE;
			case STOP:
				return ActivityEvent.STOP;
			case DESTROY:
				return ActivityEvent.DESTROY;
			default:
				throw new IllegalStateException("Unrecognized event: " + event.name());
		}
	}
}
