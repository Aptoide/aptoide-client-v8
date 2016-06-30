/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/06/2016.
 */

package cm.aptoide.pt.v8engine.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.activity.AptoideFragmentActivity;
import cm.aptoide.pt.v8engine.interfaces.Lifecycle;
import cm.aptoide.pt.v8engine.interfaces.ShowSnackbar;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 06-05-2016.
 */
public abstract class AptoideSimpleFragmentActivity extends AptoideFragmentActivity implements Lifecycle, ShowSnackbar {

	private View view;

	@Override
	public void bindViews(View view) {
		this.view = view;
	}

	@Override
	public void loadExtras(Bundle extras) {

	}

	@Override
	public void setupViews() {

	}

	@Override
	public void setupToolbar() {

	}

	@Override
	public int getContentViewId() {
		return R.layout.frame_layout;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return null;
	}

	public CustomSnackBar make() {
		return new CustomSnackBar(view.findViewById(CustomSnackBar.VIEW_ID));
	}

	public static class CustomSnackBar {

		public static final int VIEW_ID = R.id.custom_snackbar_layout;
		private final View rootView;
		public ImageView image;
		public TextView text;
		public TextView dismiss;
		public TextView action;
		@Getter @Setter private long duration = 1500L;

		public CustomSnackBar(View view) {
			rootView = view;
			image = (ImageView) view.findViewById(R.id.snackbar_image);
			text = (TextView) view.findViewById(R.id.snackbar_text);
			dismiss = (TextView) view.findViewById(R.id.snackbar_dismiss_action);
			action = (TextView) view.findViewById(R.id.snackbar_action);
		}

		public void show() {
			final int height = rootView.getHeight();
			rootView.animate().translationY(-height).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					if (rootView != null) {
						rootView.postDelayed(() -> {
							rootView.animate().translationY(height)
//									.setListener(new AnimatorListenerAdapter() {
//										@Override
//										public void onAnimationEnd(Animator animation) {
//											if (rootView != null) {
//												rootView.setTranslationY(-params.height);
//											}
//										}
//									})
									.start();
						}, duration);
					}
				}
			}).start();
		}
	}
}
