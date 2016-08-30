/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;

import cm.aptoide.pt.v8engine.presenter.Presenter;
import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
 */
public interface View {

	@NonNull
	@CheckResult
	<T> LifecycleTransformer<T> bindUntilEvent(@NonNull View.Event event);

	Observable<Event> getLifecycle();

	Context getContext();

	void attachPresenter(Presenter presenter, Bundle savedInstanceState);

	// TODO: Make it simple. We need to abstract implementation details (e.g. Activity and Fragment life cycle events).
	enum Event {
		ATTACH,
		CREATE,
		CREATE_VIEW,
		START,
		RESUME,
		PAUSE,
		STOP,
		DESTROY_VIEW,
		DESTROY,
		DETACH
	}
	
}
